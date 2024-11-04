package com.avizva.plugin.runservice.render;

import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunServiceComponent;
import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.git.RepoType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTextField;

public final class CloneBox {

	private CloneBox() {
	}

	public static JButton createCheckoutButton(ToolWindow toolWindow) {
		var jButton = new JButton("Clone");
		jButton.addActionListener(e -> {
			showBox(toolWindow);
		});
		jButton.setIcon(AllIcons.Vcs.Vendors.Github);
		return jButton;
	}

	private static void showBox(ToolWindow toolWindow) {
		var projectTextField = new JBTextField();
		var name = new JBTextField();
		var repoTypeComboBox = new ComboBox<>(RepoType.values());
		Object[] fields = {"Project Name:", projectTextField, "User Name:", name, "Repo Type:", repoTypeComboBox};
		var customIcon = AllIcons.Vcs.Vendors.Github;
		var result = JOptionPane.showConfirmDialog(null, fields, "Clone", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, customIcon);

		if (result == JOptionPane.OK_OPTION) {
			var projectNames = projectTextField.getText()
											   .split(" ");
			var selectedRepoType = (RepoType) repoTypeComboBox.getSelectedItem();
			var userName = name.getText();
			var projectMetaDataList = Arrays.stream(projectNames)
											.map(projectName -> {
												var projectMetaData = new ProjectMetaData(projectName, selectedRepoType);
												return projectMetaData;
											})
											.toList();
			clone(toolWindow, projectMetaDataList, userName);

		}

	}

	private static void clone(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData, String userName) {
		var runService = new RunServiceComponent();
		runService.cloneProjects(toolWindow, projectMetaData, userName);

	}

}