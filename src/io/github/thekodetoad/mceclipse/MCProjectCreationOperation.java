package io.github.thekodetoad.mceclipse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.maven.model.Model;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import io.github.thekodetoad.mceclipse.util.Util;

// based off https://github.com/JetBrains/kotlin-eclipse/blob/master/kotlin-eclipse-ui/src/org/jetbrains/kotlin/wizards/ProjectCreationOp.java
public class MCProjectCreationOperation implements IRunnableWithProgress {

	protected static final String SRC = "src";
	protected static final String MAIN = SRC + "/main";
	protected static final String MAIN_JAVA = MAIN + "/java";
	protected static final String MAIN_RESOURCES = MAIN + "/resources";
	protected static final String TEST = SRC + "/test";
	protected static final String TEST_JAVA = TEST + "/java";
	protected static final String TEST_RESOURCES = TEST + "/resources";
	protected static final String POM = "pom.xml";
	protected static final String CLASSPATH = ".classpath";

	protected final IWorkbench workbench;
	protected final IProjectDescription desc;
	protected final String name;
	protected final Model model;
	protected final Shell shell;
	protected Result result;

	public MCProjectCreationOperation(IWorkbench workbench, boolean useLocation, IPath location, String name, Model model, Shell shell) {
		this.workbench = workbench;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		desc = workspace.newProjectDescription(name);
		desc.setComment(name);
		desc.setLocation(useLocation ? location : null);
		desc.setNatureIds(new String[] { JavaCore.NATURE_ID, Util.MAVEN_NATURE_ID });

		ICommand javaCommand = desc.newCommand();
		javaCommand.setBuilderName(JavaCore.BUILDER_ID);

		ICommand mavenCommand = desc.newCommand();
		mavenCommand.setBuilderName(Util.MAVEN_BUILDER_ID);

		desc.setBuildSpec(new ICommand[] { javaCommand, mavenCommand });
		this.name = name;
		this.model = model;
		this.shell = new Shell();
	}

	@Override
	public final void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		CreateProjectOperation parentOperation = new CreateProjectOperation(desc, name);
		try {
			PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(parentOperation, monitor, WorkspaceUndoUtil.getUIInfoAdapter(shell));
			result = new Result(ResourcesPlugin.getWorkspace().getRoot().getProject(name), null);
			result.project.setDescription(desc, monitor);

			postCreate(monitor);
		}
		catch(ExecutionException | CoreException | IOException error) {
			result = new Result(null, error);
		}
	}

	protected void postCreate(IProgressMonitor monitor) throws CoreException, IOException {
		mkdir(SRC, monitor);
		mkdir(MAIN, monitor);
		mkdir(MAIN_JAVA, monitor);
		mkdir(MAIN_RESOURCES, monitor);
		mkdir(TEST, monitor);
		mkdir(TEST_JAVA, monitor);
		mkdir(TEST_RESOURCES, monitor);

		IFile pom = result.project.getFile(POM);
		pom.create(new ByteArrayInputStream(Util.modelToByteArray(model)), false, monitor);

		IFile classpath = result.project.getFile(CLASSPATH);
		classpath.create(getClass().getResourceAsStream("/default.classpath"), false, monitor);
	}

	private void mkdir(String name, IProgressMonitor monitor) throws CoreException {
		IFolder folder = result.project.getFolder(name);
		if(!folder.exists()) folder.create(false, true, monitor);
	}

	public record Result(IProject project, Throwable error) {
	}

}
