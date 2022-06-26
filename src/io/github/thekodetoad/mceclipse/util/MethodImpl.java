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

package io.github.thekodetoad.mceclipse.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

import lombok.Getter;

public record MethodImpl(@Getter int flags, @Getter IType declaringType, String name, String[] exceptions,
		String[] typeParamSignatures, ITypeParameter[] typeParams, @Getter ILocalVariable[] parameters,
		@Getter String[] parameterNames, @Getter String[] parameterTypes, @Getter String returnType)
		implements IMethod {

	@Override
	public String[] getCategories() throws JavaModelException {
		return new String[0];
	}

	@Override
	public IClassFile getClassFile() {
		return null;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		return null;
	}

	@Override
	public ISourceRange getJavadocRange() throws JavaModelException {
		return null;
	}

	@Override
	public int getOccurrenceCount() {
		return 1;
	}

	@Override
	public ITypeRoot getTypeRoot() {
		return null;
	}

	@Override
	public IType getType(String name, int occurrenceCount) {
		return null;
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public IJavaElement getAncestor(int ancestorType) {
		return null;
	}

	@Override
	public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
		return null;
	}

	@Override
	public IResource getCorrespondingResource() throws JavaModelException {
		return null;
	}

	@Override
	public int getElementType() {
		return IJavaElement.METHOD;
	}

	@Override
	public String getHandleIdentifier() {
		return null;
	}

	@Override
	public IJavaModel getJavaModel() {
		return null;
	}

	@Override
	public IJavaProject getJavaProject() {
		return null;
	}

	@Override
	public IOpenable getOpenable() {
		return null;
	}

	@Override
	public IJavaElement getParent() {
		return null;
	}

	@Override
	public IPath getPath() {
		return null;
	}

	@Override
	public IJavaElement getPrimaryElement() {
		return null;
	}

	@Override
	public IResource getResource() {
		return null;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return null;
	}

	@Override
	public IResource getUnderlyingResource() throws JavaModelException {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isStructureKnown() throws JavaModelException {
		return true;
	}

	@Override
	public <T> T getAdapter(Class<T> arg0) {
		return null;
	}

	@Override
	public String getSource() throws JavaModelException {
		return null;
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		return null;
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		return null;
	}

	@Override
	public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
			IProgressMonitor monitor) throws JavaModelException {
	}

	@Override
	public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {
	}

	@Override
	public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
			IProgressMonitor monitor) throws JavaModelException {
	}

	@Override
	public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException {
	}

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		return null;
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		return false;
	}

	@Override
	public IAnnotation getAnnotation(String name) {
		return null;
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		return new IAnnotation[0];
	}

	@Override
	public IMemberValuePair getDefaultValue() throws JavaModelException {
		return null;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public String[] getExceptionTypes() throws JavaModelException {
		return exceptions;
	}

	@Override
	public String[] getTypeParameterSignatures() throws JavaModelException {
		return typeParamSignatures;
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		return typeParams;
	}

	@Override
	public int getNumberOfParameters() {
		return parameters.length;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public String[] getRawParameterNames() throws JavaModelException {
		return parameterNames;
	}

	@Override
	public String getSignature() throws JavaModelException {
		return null;
	}

	@Override
	public ITypeParameter getTypeParameter(String name) {
		return null;
	}

	@Override
	public boolean isConstructor() throws JavaModelException {
		return name.equals("<init>");
	}

	@Override
	public boolean isMainMethod() throws JavaModelException {
		return name.equals("main") && parameterTypes.length == 1 && parameterTypes[0].equals("Ljava/lang/String;")
				&& returnType.equals("V");
	}

	@Override
	public boolean isLambdaMethod() {
		return false;
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public boolean isSimilar(IMethod method) {
		return false;
	}
}
