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

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private WizardNewProjectCreationPage projectPage;
	private BuildConfigPage buildConfigPage;

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
		this.workbench = workbench;
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		IRunnableWithProgress operation = new PaperProjectCreationOperation(workbench, !projectPage.useDefaults(),
				projectPage.getLocationPath(), projectPage.getProjectName(), createPom(), getShell());
		try {
			getContainer().run(true, true, operation);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

}