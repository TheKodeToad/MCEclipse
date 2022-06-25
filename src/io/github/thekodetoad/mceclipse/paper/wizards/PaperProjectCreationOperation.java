package io.github.thekodetoad.mceclipse.paper.wizards;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.swt.widgets.Shell;

import io.github.thekodetoad.mceclipse.MCProjectCreationOperation;

public class PaperProjectCreationOperation extends MCProjectCreationOperation {

	public PaperProjectCreationOperation(boolean useLocation, IPath location, String name, Model model, Shell shell) {
		super(useLocation, location, name, model, shell);
	}

	@Override
	protected void postCreate(IProgressMonitor monitor) throws CoreException, IOException {
		super.postCreate(monitor);
		IJavaProject jproject = JavaCore.create(result.project());
		IPackageFragmentRoot root = jproject.getPackageFragmentRoot(result.project().getFolder(MAIN_JAVA));

		String packageName = model.getGroupId() + "." + model.getArtifactId();
		root.createPackageFragment(packageName, true, monitor);

		String className = "Main";

		IPackageFragment frag = root.getPackageFragment(packageName);
		ICompilationUnit mainUnit = frag.getCompilationUnit(className + ".java");
		IBuffer sourceBuffer = mainUnit.getBuffer();

		ImportRewrite rewrite = ImportRewrite.create(mainUnit, false);

		StringBuilder mainContent = new StringBuilder();
		mainContent.append("public class ");
		mainContent.append(className);
		mainContent.append(" extends ");
		mainContent.append(rewrite.addImport("org.bukkit.plugin.java.JavaPlugin"));
		mainContent.append("{}");

		sourceBuffer.append(mainContent.toString());

		mainUnit.commitWorkingCopy(true, monitor);
	}

}
