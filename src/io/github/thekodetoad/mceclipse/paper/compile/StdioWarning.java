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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import io.github.thekodetoad.mceclipse.MCEclipsePlugin;
import io.github.thekodetoad.mceclipse.util.ContextualAstVisitor;

public class StdioWarning extends ContextualAstVisitor {

	@Override
	public void context(ReconcileContext context, CompilationUnit unit, IJavaProject project, IResource resource) {
		super.context(context, unit, project, resource);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if(node.getExpression() != null && node.getExpression().toString().startsWith("System.out")
				|| node.getExpression().toString().startsWith("System.err")) {
			try {
				IMarker marker = resource.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.LINE_NUMBER, unit.getLineNumber(node.getStartPosition()));
				marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
				marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
				marker.setAttribute(IMarker.MESSAGE, "Direct usage of standard IO is discouraged within plugins.");
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			}
			catch(CoreException error) {
				MCEclipsePlugin.log().error("Could not add marker", error);
			}
		}

		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		IVariableBinding field = node.resolveFieldBinding();

		if(field != null && field.getDeclaringClass().getQualifiedName().equals("java.lang.System")
				&& (field.getName().equals("in") || field.getName().equals("out") || field.getName().equals("err"))) {
			try {
				IMarker marker = resource.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.LINE_NUMBER, unit.getLineNumber(node.getStartPosition()));
				marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
				marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
				marker.setAttribute(IMarker.MESSAGE, "Direct usage of standard IO is discouraged within plugins.");
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
