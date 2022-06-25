package io.github.thekodetoad.mceclipse.paper.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.maven.model.Model;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import io.github.thekodetoad.mceclipse.BuildConfigPage;
import io.github.thekodetoad.mceclipse.paper.PaperUtil;

public class PaperPluginWizard extends Wizard implements IWorkbenchWizard {

	private IStructuredSelection selection;
	private WizardNewProjectCreationPage projectPage;
	private BuildConfigPage buildConfigPage;

//	@Override
//	public boolean performFinish() {
//		IProject project = projectPage.getProjectHandle();
//		IPath path = projectPage.useDefaults() ? null : projectPage.getLocationPath();
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//
//		IWorkingSet[] workingSets = projectPage.getSelectedWorkingSets();
//
////		AbstractCreateMavenProjectJob job = new AbstractCreateMavenProjectJob("Creating project...") {
////
////			@Override
////			protected List<IProject> doCreateMavenProjects(IProgressMonitor monitor) throws CoreException {
////				MavenPlugin.getProjectConfigurationManager().createSimpleProject(project, path, createPom(), new String[0],
////						new ProjectImportConfiguration(), new MavenProjectWorkspaceAssigner(Arrays.asList(workingSets)), monitor);
////				return Arrays.asList(project);
////			}
////
////		};
////
////		job.addJobChangeListener(new JobChangeAdapter() {
////			@Override
////			public void done(IJobChangeEvent event) {
////				IStatus result = event.getResult();
////				if(!result.isOK()) {
////					Display.getDefault().asyncExec(() -> MessageDialog.openError(getShell(),
////							"Failed to create project.", result.getMessage()));
////				}
////
////				MappingDiscoveryJob discoveryJob = new MappingDiscoveryJob(job.getCreatedProjects());
////				discoveryJob.schedule();
////			}
////		});
////
////		job.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
////		job.schedule();
//
//
//
//		return true;
//	}

	private Model createPom() {
		Model model = buildConfigPage.createModel();
		model.addDependency(PaperUtil.mavenDependency("1.19"));

		model.addRepository(PaperUtil.MAVEN_REPO);
		return model;
	}

	@Override
	public void addPages() {
		super.addPages();
		projectPage = new WizardNewProjectCreationPage("basicNewProjectPage") {

			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup((Composite) getControl(), selection,
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
				Dialog.applyDialogFont(getControl());
			}

		};
		projectPage.setDescription("Specify the name of the project to create.");
		addPage(projectPage);
		addPage(buildConfigPage = new BuildConfigPage());
		addPage(new PaperConfigPage(() -> projectPage.getProjectName()));

		for(IWizardPage page : getPages()) {
			if(page instanceof WizardNewProjectCreationPage) {
				projectPage = (WizardNewProjectCreationPage) page;
			}
			page.setTitle("Create Paper Plugin");
		}
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		IRunnableWithProgress operation = new PaperProjectCreationOperation(!projectPage.useDefaults(),
				projectPage.getLocationPath(), projectPage.getProjectName(), createPom(), getShell());
		try {
			getContainer().run(true, true, operation);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

}