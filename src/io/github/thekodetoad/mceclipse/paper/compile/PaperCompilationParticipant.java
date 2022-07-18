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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;

import io.github.thekodetoad.mceclipse.MCEclipsePlugin;
import io.github.thekodetoad.mceclipse.paper.PaperProjectNature;
import io.github.thekodetoad.mceclipse.util.ContextualAstVisitor;

public class PaperCompilationParticipant extends CompilationParticipant {

	private final ContextualAstVisitor[] visitors = new ContextualAstVisitor[] { new ResourceWarning(), new StdioWarning() };

	@Override
	public void reconcile(ReconcileContext context) {
		try {
			CompilationUnit unit = context.getAST(AST.getJLSLatest());
			IJavaProject project = unit.getJavaElement().getJavaProject();
			IResource resource = context.getWorkingCopy().getUnderlyingResource();

			if((context.getDelta().getFlags() & IJavaElementDelta.F_AST_AFFECTED) != 0) {
				for(ContextualAstVisitor visitor : visitors) {
					visitor.context(context, unit, project, resource);
					unit.accept(visitor);
				}
			}
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
		try {
			return project.getProject().hasNature(PaperProjectNature.ID);
		}
		catch(CoreException error) {
			MCEclipsePlugin.log().error("Could not determine state of nature", error);
		}

		return false;
	}

}
