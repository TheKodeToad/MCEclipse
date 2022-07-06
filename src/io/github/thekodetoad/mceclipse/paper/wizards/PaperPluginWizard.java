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

package io.github.thekodetoad.mceclipse.paper.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import io.github.thekodetoad.mceclipse.MCEclipsePlugin;
import io.github.thekodetoad.mceclipse.util.Util;
import lombok.Cleanup;

public class PaperPluginWizard extends Wizard implements IWorkbenchWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private WizardNewProjectCreationPage projectPage;
	private PaperConfigPage paperPage;

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
		addPage(paperPage = new PaperConfigPage(() -> projectPage.getProjectName()));

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
				projectPage.getLocationPath(), projectPage.getProjectName(), paperPage.createModel(), getShell(),
				paperPage.getMainClass(), projectPage.getSelectedWorkingSets());
		try {
			getContainer().run(true, true, operation);
		}
		catch(InvocationTargetException | InterruptedException error) {
			MCEclipsePlugin.log().error("Could not create Paper plugin", error);
		}
		return true;
	}

}