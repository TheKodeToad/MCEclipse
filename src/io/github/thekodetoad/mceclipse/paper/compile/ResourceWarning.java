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

package io.github.thekodetoad.mceclipse.paper.compile;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import io.github.thekodetoad.mceclipse.MCEclipsePlugin;
import io.github.thekodetoad.mceclipse.util.ContextualAstVisitor;

public class ResourceWarning extends ContextualAstVisitor {

	@Override
	public void preVisit(ASTNode node) {
		super.preVisit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding method = node.resolveMethodBinding();
		if(method.getName().equals("saveDefaultConfig")
				&& method.getDeclaringClass().getQualifiedName().equals("org.bukkit.plugin.java.JavaPlugin")
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
			}
			catch(CoreException error) {
				MCEclipsePlugin.log().error("Could not add marker", error);
			}
		}
		return false;
	}

}
