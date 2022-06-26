package io.github.thekodetoad.mceclipse.paper.wizards;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.apache.maven.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.manipulation.CodeGeneration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import io.github.thekodetoad.mceclipse.MCProjectCreationOperation;
import io.github.thekodetoad.mceclipse.util.MethodImpl;
import io.github.thekodetoad.mceclipse.util.TypeImpl;
import io.github.thekodetoad.mceclipse.util.Util;

public class PaperProjectCreationOperation extends MCProjectCreationOperation {

	private String mainClass;

	public PaperProjectCreationOperation(IWorkbench workbench, boolean useLocation, IPath location, String name, Model model, Shell shell, String mainClass) {
		super(workbench, useLocation, location, name, model, shell);
		this.mainClass = mainClass;
	}

	@Override
	protected void postCreate(IProgressMonitor monitor) throws CoreException, IOException {
		super.postCreate(monitor);
		IJavaProject jproject = JavaCore.create(result.project());
		IPackageFragmentRoot root = jproject.getPackageFragmentRoot(result.project().getFolder(MAIN_JAVA));

		String delimiter = "\n";
		String packageName = null;
		String className;
		IPackageFragment frag;

		if(mainClass.contains(".")) {
			packageName = mainClass.substring(0, mainClass.lastIndexOf('.'));
			root.createPackageFragment(packageName, true, monitor);
			frag = root.getPackageFragment(packageName);
			className = mainClass.substring(mainClass.lastIndexOf('.') + 1);
		}
		else {
			packageName = null; // no package header
			frag = root.getPackageFragment(""); // default package
			className = mainClass;
		}

		ICompilationUnit mainUnit = frag.createCompilationUnit(className + ".java", "", true, monitor);
		mainUnit.becomeWorkingCopy(monitor);

		IBuffer sourceBuffer = mainUnit.getBuffer();
		ImportRewrite imports = ImportRewrite.create(mainUnit, false);

		StringBuilder mainContent = new StringBuilder();

		mainContent.append("\n");
		mainContent.append(CodeGeneration.getTypeComment(mainUnit, mainClass, delimiter));
		mainContent.append("public final class ");
		mainContent.append(className);
		mainContent.append(" extends ");
		mainContent.append(imports.addImport("org.bukkit.plugin.java.JavaPlugin"));
		mainContent.append("{\n\n");
		mainContent.append(CodeGeneration.getMethodComment(mainUnit, mainClass, "onEnable", new String[0],
				new String[0], "V",
				new MethodImpl(Modifier.PUBLIC, new TypeImpl(Modifier.PUBLIC, "org.bukkit.plugin.java.JavaPlugin"),
						"onEnable", new String[0], new String[0], new ITypeParameter[0], new ILocalVariable[0],
						new String[0], new String[0], "V"),
				delimiter));
		mainContent.append("\n@Override\n");
		mainContent.append("public void onEnable() {\n");
		mainContent.append("// TODO Plugin startup\n\n");
		mainContent.append("}\n\n");
		mainContent.append(CodeGeneration.getMethodComment(mainUnit, mainClass, "onDisable", new String[0],
				new String[0], "V",
				new MethodImpl(Modifier.PUBLIC, new TypeImpl(Modifier.PUBLIC, "org.bukkit.plugin.java.JavaPlugin"),
						"onDisable", new String[0], new String[0], new ITypeParameter[0], new ILocalVariable[0],
						new String[0], new String[0], "V"),
				delimiter));
		mainContent.append("\n@Override\n");
		mainContent.append("public void onDisable() {\n");
		mainContent.append("// TODO Plugin shutdown\n\n");
		mainContent.append("}\n\n");
		mainContent.append("}");

		String mainContentStr = Util.applyImports(mainContent.toString(), imports);

		StringBuilder header = new StringBuilder();
		header.append(CodeGeneration.getFileComment(mainUnit, "\n"));
		header.append("\n");

		if(packageName != null) {
			header.append("package ");
			header.append(packageName);
			header.append(";");
		}

		mainContentStr = header + mainContentStr;

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
