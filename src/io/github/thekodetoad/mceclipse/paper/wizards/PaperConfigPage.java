package io.github.thekodetoad.mceclipse.paper.wizards;

import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.github.thekodetoad.mceclipse.MCWizardPage;

public class PaperConfigPage extends MCWizardPage {

	private Text pluginNameText;
	private boolean populatedFields;
	private Supplier<String> projectName;
	private String pluginName;

	protected PaperConfigPage(Supplier<String> projectName) {
		super("paperConfig");
		setDescription("Configure the plugin description.");
		this.projectName = projectName;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label pluginNameLabel = new Label(container, SWT.NULL);
		pluginNameLabel.setText("Plugin &name:");

		pluginNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		pluginNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pluginNameText.addModifyListener(this);

		updateValues();
		setControl(container);
	}

	@Override
	public void setVisible(boolean visible) {
		if(!populatedFields && visible) {
			pluginNameText.setText(projectName.get());
			populatedFields = true;
		}
		super.setVisible(visible);
	}

	@Override
	protected void updateValues() {
		pluginName = pluginNameText.getText();
		if(pluginName.isEmpty()) {
			error("No plugin name specified.");
			return;
		}
		else if(pluginName.equalsIgnoreCase("bukkit") || pluginName.equalsIgnoreCase("minecraft") || pluginName.equalsIgnoreCase("mojang")) {
			error("\"" +  pluginName + "\" is a reserved plugin name.");
			return;
		}
		else if(pluginName.contains(" ")) {
			error("Plugin name cannot contain spaces.");
			return;
		}
		error(null);
	}

}
