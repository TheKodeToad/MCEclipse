package io.github.thekodetoad.mceclipse;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;

public abstract class MCWizardPage extends WizardPage implements ModifyListener {

	protected static final GridData HORIZONTAL_FILL = new GridData(GridData.FILL_HORIZONTAL);

	protected MCWizardPage(String pageName) {
		super(pageName);
	}

	protected abstract void updateValues();

	@Override
	public void modifyText(ModifyEvent event) {
		updateValues();
	}

	protected void error(String error) {
		setErrorMessage(error);
		setPageComplete(error == null);
	}

}
