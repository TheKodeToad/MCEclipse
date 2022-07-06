/**
 * Copyright (C) 2022 TheKodeToad
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.thekodetoad.mceclipse.paper;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import io.github.thekodetoad.mceclipse.MCEclipsePlugin;

public class Participant extends CompilationParticipant {

	private Set<IMarker> markers = new HashSet<>();

	@Override
	public void reconcile(ReconcileContext context) {
		try {
			CompilationUnit unit = context.getAST(AST.getJLSLatest());
			IJavaProject project = unit.getJavaElement().getJavaProject();
			IResource resource = context.getWorkingCopy().getUnderlyingResource();
			Set<IMarker> addedMarkers = new HashSet<>();

			if((context.getDelta().getFlags() & IJavaElementDelta.F_AST_AFFECTED) != 0) {
				unit.accept(new ASTVisitor() {

					@Override
					public boolean visit(MethodInvocation node) {
						IMethodBinding method = node.resolveMethodBinding();
						if(method.getName().equals("saveDefaultConfig")
								&& method.getDeclaringClass().getQualifiedName()
										.equals("org.bukkit.plugin.java.JavaPlugin")
								&& !project.getProject().getFile("src/main/resources/config.yml").exists()) {
							try {
								IMarker marker = resource.createMarker(IMarker.PROBLEM);
								marker.setAttribute(IMarker.LINE_NUMBER, unit.getLineNumber(node.getStartPosition()));
								marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
								marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
								marker.setAttribute(IMarker.MESSAGE,
										"IOException: The embedded resource 'config.yml' cannot be found.");
								marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
								marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
								addedMarkers.add(marker);
							}
							catch(CoreException error) {
								MCEclipsePlugin.log().error("Could not add marker", error);
							}
						}
						return false;
					}

				});
			}

			markers.forEach((item) -> {
				if(!addedMarkers.contains(item)) {
					try {
						item.delete();
					}
					catch(CoreException error) {
						throw new RuntimeException(error);
					}
				}
			});
			markers.addAll(addedMarkers);
		}
		catch(JavaModelException error) {
			MCEclipsePlugin.log().error("Could not add warnings", error);
		}
	}

	@Override
	public int aboutToBuild(IJavaProject project) {
		return READY_FOR_BUILD;
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

}
