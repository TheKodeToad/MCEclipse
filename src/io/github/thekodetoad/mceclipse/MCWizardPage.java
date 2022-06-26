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
