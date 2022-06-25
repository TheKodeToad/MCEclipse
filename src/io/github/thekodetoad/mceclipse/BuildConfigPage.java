package io.github.thekodetoad.mceclipse;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BuildConfigPage extends MCWizardPage {

	private Text groupText;
	private Text artifactText;
	private Text versionText;
	private String group;
	private String artifact;
	private String version;

	public BuildConfigPage() {
		super("buildConfig");
		setDescription("Configure the build.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		Label groupLabel = new Label(container, 0);
		groupLabel.setText("&Group ID:");

		groupText = new Text(container, SWT.BORDER | SWT.SINGLE);
		groupText.setLayoutData(HORIZONTAL_FILL);
		groupText.addModifyListener(this);

		Label artifactLabel = new Label(container, 0);
		artifactLabel.setText("&Artifact ID:");

		artifactText = new Text(container, SWT.BORDER | SWT.SINGLE);
		artifactText.setLayoutData(HORIZONTAL_FILL);
		artifactText.addModifyListener(this);

		Label versionLabel = new Label(container, 0);
		versionLabel.setText("&Version:");

		versionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		versionText.setLayoutData(HORIZONTAL_FILL);
		versionText.addModifyListener(this);

		updateValues();
		setControl(container);
	}

	@Override
	protected void updateValues() {
		group = groupText.getText();
		artifact = artifactText.getText();
		version = versionText.getText();

		String groupError = getIdError(group, "group");

		if(groupError != null) {
			error(groupError);
			return;
		}

		String artifactError = getIdError(artifact, "artifact");

		if(artifactError != null) {
			error(artifactError);
			return;
		}

		if(versionText.getText().isEmpty()) {
			error("No version specified.");
			return;
		}

		error(null);
	}

	private String getIdError(String id, String errorName) {
		if(id.isEmpty()) {
			return "No " + errorName + " specified.";
		}
		else if(id.contains(" ")) {
			return "Invalid " + errorName + " - contains spaces.";
		}
		else if(!(ResourcesPlugin.getWorkspace().validateName(id, 4).isOK()
				&& id.matches("[A-Za-z0-9_\\-.]+"))) {
			return "Invalid " + errorName + ".";
		}

		return null;
	}

	public Model createModel() {
		Model model = new Model();
		model.setModelVersion("4.0.0");
		model.setGroupId(group);
		model.setArtifactId(artifact);
		model.setVersion(version);
		return model;
	}

}
