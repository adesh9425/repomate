package com.avizva.plugin.runservice.render;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunServiceComponent;
import com.avizva.plugin.runservice.services.entity.ProjectSpace;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTextField;

public class ConfigurationButton {

	private ConfigurationButton() {
	}

	public static JButton createConfigurationButton(ToolWindow toolWindow) {
		var jButton = new JButton("Configuration");
		jButton.addActionListener(e -> {
			showBox(toolWindow);
		});
		jButton.setIcon(AllIcons.General.Settings);
		return jButton;
	}

	private static void showBox(ToolWindow toolWindow) {
		var projectTextField = new JBTextField();
		var prefix = new JBTextField();
		var config = getConfig(toolWindow);
		var space = config.getProjectSpace();
		var projectPrefix = config.getProjectPrefix();
		projectTextField.setText(space);
		prefix.setText(projectPrefix);

		Object[] fields = {"Bitbucket Work Space:", projectTextField};
		var customIcon = AllIcons.General.Settings;
		var result = JOptionPane.showConfirmDialog(null, fields, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, customIcon);

		if (result == JOptionPane.OK_OPTION) {
			var projectSpace = projectTextField.getText()
											   .trim();

			handleConfig(toolWindow, projectSpace, StringConstants.EMPTY);

		}

	}

	private static ProjectSpace getConfig(ToolWindow toolWindow) {
		var runServiceComponent = new RunServiceComponent();
		var projectSpace = runServiceComponent.getConfigurations(toolWindow);
		return projectSpace;
	}

	private static void handleConfig(ToolWindow toolWindow, String projectSpace, String prefixText) {
		var runServiceComponent = new RunServiceComponent();
		runServiceComponent.handleConfigurations(toolWindow, projectSpace, prefixText);
	}

}