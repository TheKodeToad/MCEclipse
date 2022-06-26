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
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import io.github.thekodetoad.mceclipse.MCProjectCreationOperation;
import io.github.thekodetoad.mceclipse.Util;

public class PaperProjectCreationOperation extends MCProjectCreationOperation {

	public PaperProjectCreationOperation(IWorkbench workbench, boolean useLocation, IPath location, String name, Model model, Shell shell) {
		super(workbench, useLocation, location, name, model, shell);
	}

	@Override
	protected void postCreate(IProgressMonitor monitor) throws CoreException, IOException {
		super.postCreate(monitor);
		IJavaProject jproject = JavaCore.create(result.project());
		IPackageFragmentRoot root = jproject.getPackageFragmentRoot(result.project().getFolder(MAIN_JAVA));

		String delimiter = "\n";

		String packageName = model.getGroupId() + "." + model.getArtifactId();
		root.createPackageFragment(packageName, true, monitor);

		String className = "Main";

		IPackageFragment frag = root.getPackageFragment(packageName);

		ICompilationUnit mainUnit = frag.createCompilationUnit(className + ".java", "", true, monitor);
		mainUnit.becomeWorkingCopy(monitor);

		IBuffer sourceBuffer = mainUnit.getBuffer();
		ImportRewrite imports = ImportRewrite.create(mainUnit, false);

		StringBuilder mainContent = new StringBuilder();
		mainContent.append(delimiter);
		mainContent.append("public class ");
		mainContent.append(className);
		mainContent.append(" extends ");
		mainContent.append(imports.addImport("org.bukkit.plugin.java.JavaPlugin"));
		mainContent.append("{");
		mainContent.append(delimiter);
		mainContent.append(delimiter);
		mainContent.append("@Override");
		mainContent.append(delimiter);
		mainContent.append("public void onEnable() {");
		mainContent.append(delimiter);
		mainContent.append("// TODO Plugin enable logic");
		mainContent.append(delimiter);
		mainContent.append(delimiter);
		mainContent.append("}");
		mainContent.append(delimiter);
		mainContent.append(delimiter);
		mainContent.append("@Override");
		mainContent.append(delimiter);
		mainContent.append("public void onDisable() {");
		mainContent.append(delimiter);
		mainContent.append("// TODO Plugin disable logic");
		mainContent.append(delimiter);
		mainContent.append(delimiter);
		mainContent.append("}");
		mainContent.append(delimiter);
		mainContent.append(delimiter);
		mainContent.append("}");

		String mainContentStr = Util.applyImports(mainContent.toString(), imports);
		mainContentStr = "package ".concat(packageName).concat(";").concat(delimiter).concat(mainContentStr);

		sourceBuffer
				.append(Util.format(mainContentStr, CodeFormatter.K_UNKNOWN, jproject.getOptions(true), 0, delimiter));

		mainUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);

		mainUnit.commitWorkingCopy(false, monitor);
		mainUnit.save(monitor, true);
		mainUnit.discardWorkingCopy();
		monitor.done();

		Display display = shell.getDisplay();

		if(display != null) {
			display.asyncExec(() -> {
				try {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
							result.project().getFile(mainUnit.getResource().getProjectRelativePath()), true);
				}
				catch(PartInitException error) {
					Util.LOG.error("Could not open editor", error);
				}
				BasicNewResourceWizard.selectAndReveal(mainUnit.getResource(), workbench.getActiveWorkbenchWindow());
			});
		}
	}

}
