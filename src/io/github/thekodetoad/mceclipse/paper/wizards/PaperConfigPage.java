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

import java.util.function.Supplier;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.github.thekodetoad.mceclipse.MCWizardPage;
import io.github.thekodetoad.mceclipse.paper.PaperUtil;
import lombok.Getter;

public class PaperConfigPage extends MCWizardPage {

	private Text pluginNameText;
	private Text groupText;
	private Text versionText;
	private Text mainClassText;
	private boolean populatedFields;
	private Supplier<String> projectName;
	private String pluginName;
	private String group;
	private String version;
	@Getter
	private String mainClass;

	protected PaperConfigPage(Supplier<String> projectName) {
		super("paperConfig");
		setDescription("Configure the plugin description.");
		this.projectName = projectName;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		Label pluginNameLabel = new Label(container, SWT.NONE);
		pluginNameLabel.setText("Plugin &name:");

		pluginNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		pluginNameText.setLayoutData(HORIZONTAL_FILL);
		pluginNameText.addModifyListener(this);

		Label groupLabel = new Label(container, SWT.NONE);
		groupLabel.setText("&Group:");

		groupText = new Text(container, SWT.BORDER | SWT.SINGLE);
		groupText.setLayoutData(HORIZONTAL_FILL);
		groupText.addModifyListener(this);
		groupText.setMessage("com.example"); // set hint

		Label versionLabel = new Label(container, SWT.NONE);
		versionLabel.setText("&Version:");

		versionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		versionText.setLayoutData(HORIZONTAL_FILL);
		versionText.addModifyListener(this);
		versionText.setText("1.0.0");

		Label mainClassLabel = new Label(container, SWT.NONE);
		mainClassLabel.setText("Main &class:");

		mainClassText = new Text(container, SWT.BORDER | SWT.SINGLE);
		mainClassText.setLayoutData(HORIZONTAL_FILL);
		mainClassText.addModifyListener(this);

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
	public void modifyText(ModifyEvent event) {
		super.modifyText(event);
		if(event.getSource() == pluginNameText || event.getSource() == groupText) {
			String mainClass = group.replace("-", "") + "." + pluginName.toLowerCase().replace("-", "").replace(".", "") + "." + toPascal(toSnakeHyphen(pluginName));
			if(!mainClass.toLowerCase().endsWith("plugin")) {
				mainClass += "Plugin";
			}
			if(!mainClassText.getText().equals(mainClass)) {
				mainClassText.setText(mainClass);
			}
		}
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

		group = groupText.getText();
		if(group.isEmpty()) {
			error("Group is blank.");
			return;
		}

		version = versionText.getText();
		if(version.isEmpty()) {
			error("Version is blank.");
			return;
		}

		mainClass = mainClassText.getText();

		error(null);
	}

	private static String toPascal(String name) {
		StringBuilder result = new StringBuilder();
		char c = 0;
		for(int i = 0; i < name.length(); i++) {
			char lastC = c;
			c = name.charAt(i);
			if(c == '-') {
				continue;
			}
			else if(i == 0) {
				c = Character.toUpperCase(c);
			}
			else if(lastC == '-') {
				c = Character.toUpperCase(c);
			}
			result.append(c);
		}
		return result.toString();
	}

	private static String toSnakeHyphen(String name) {
		StringBuilder result = new StringBuilder();
		char c = 0;
		char realC = 0;
		for(int i = 0; i < name.length(); i++) {
			char lastRealC = realC;
			char lastC = c;
			realC = name.charAt(i);
			c = realC;
			char nextC = 'A';
			if(i + 1 < name.length()) {
				nextC = name.charAt(i + 1);
			}
			if(c == '_' || c == ' ' || c == '.') {
				c = '-';
			}
			else if(Character.isUpperCase(c)) {
				c = Character.toLowerCase(c);
				if(i != 0 && lastC != '-' && (!Character.isUpperCase(lastRealC) || !Character.isUpperCase(nextC))) {
					result.append('-');
				}
			}
			result.append(c);
		}
		return result.toString();
	}

	public Model createModel() {
		Model model = new Model();

		model.setModelVersion("4.0.0");
		model.setGroupId(group);
		model.setArtifactId(pluginName);
		model.setVersion(version);
		model.addDependency(PaperUtil.mavenDependency("1.19"));
		model.addRepository(PaperUtil.MAVEN_REPO);

		// TODO change version based on Minecraft version.
		model.addProperty("maven.compiler.source", "17");
		model.addProperty("maven.compiler.target", "17");

		Build build = new Build();

		Resource resources = new Resource();
		resources.setDirectory("src/main/resources");
		resources.setFiltering(true);
		resources.addInclude("plugin.yml");

		build.addResource(resources);
		model.setBuild(build);

		return model;
	}

}
