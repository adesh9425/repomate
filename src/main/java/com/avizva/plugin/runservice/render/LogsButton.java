package com.avizva.plugin.runservice.render;

import java.util.stream.Stream;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunService;
import com.avizva.plugin.runservice.services.RunServiceComponent;
import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.git.RepoType;
import com.avizva.plugin.runservice.services.runconfiguration.Env;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTextField;

public final class LogsButton {
	private final RunService runService;

	public LogsButton(RunService runService) {
		this.runService = runService;
	}

	public JButton createLogsButton(ToolWindow toolWindow) {
		var logs = new JButton("Logs");
		logs.setIcon(AllIcons.RunConfigurations.Remote);
		logs.addActionListener(e -> {
			showBox(toolWindow);
		});
		return logs;
	}

	private void showBox(ToolWindow toolWindow) {
		var projectTextField = new JBTextField();
		var projectsGroup = runService.getProjectsGroup(toolWindow)
									  .stream()
									  .map(ProjectMetaData::getConfigName)
									  .filter(configName -> !configName.contains(StringConstants.USECASES))
									  .filter(configName -> !configName.contains(StringConstants.COMMON))
									  .toArray();
		var combVal = Stream.of(RepoType.API, RepoType.APP)
							.toArray();
		var repoTypeComboBox = new ComboBox<>(combVal);
		var projectsGroupComboBox = new ComboBox<>(projectsGroup);
		var envComboBox = new ComboBox<>(Env.values());
		Object[] fields = {"Env:", envComboBox, "Repo Type:", repoTypeComboBox, "Project", projectsGroupComboBox};
		var customIcon = AllIcons.Vcs.Vendors.Github;
		var result = JOptionPane.showConfirmDialog(null, fields, "Logs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, customIcon);

		if (result == JOptionPane.OK_OPTION) {
			var projectName = (String) projectsGroupComboBox.getSelectedItem();
			var selectedRepoType = (RepoType) repoTypeComboBox.getSelectedItem();
			var env = (Env) envComboBox.getSelectedItem();
			var projectMetaData = new ProjectMetaData(projectName, selectedRepoType);
			logs(toolWindow, projectMetaData, env);

		}

	}

	private static void logs(ToolWindow toolWindow, ProjectMetaData projectMetaData, Env env) {
		var runService = new RunServiceComponent();

		runService.getLogs(toolWindow, env, projectMetaData);

	}

}