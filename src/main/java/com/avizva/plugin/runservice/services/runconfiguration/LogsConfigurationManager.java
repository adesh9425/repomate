package com.avizva.plugin.runservice.services.runconfiguration;

import java.nio.file.Path;
import java.util.Map;

import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.sh.run.ShConfigurationType;
import com.intellij.sh.run.ShRunConfiguration;

public final class LogsConfigurationManager {

	private LogsConfigurationManager() {
	}

	public static void run(Project project, String configurationName, Map<String, String> args, ToolWindow toolWindow) {

		var configuration = getConfiguration(toolWindow, configurationName, args);

		var executor = DefaultRunExecutor.getRunExecutorInstance();
		ProgramRunnerUtil.executeConfiguration(configuration, executor);

	}

	private static RunnerAndConfigurationSettings getConfiguration(ToolWindow toolWindow, String configurationName, Map<String, String> args) {
		var project = toolWindow.getProject();
		var runManager = RunManager.getInstance(project);
		var filePath = resourcesFilePath(toolWindow);

		ConfigurationType logConfigType = ShConfigurationType.getInstance(); // Change this if necessary
		var settings = runManager.createConfiguration(configurationName, logConfigType.getConfigurationFactories()[0]);

		var logRunConfig = (ShRunConfiguration) settings.getConfiguration();

		// Set configuration options using Java code
		logRunConfig.setName(configurationName);

		// Set independent script path and other parameters

		logRunConfig.setScriptPath(filePath);
		logRunConfig.setScriptOptions("");
		var environmentVariablesData = EnvironmentVariablesData.create(args, true, null);

		logRunConfig.setEnvData(environmentVariablesData);

		logRunConfig.setScriptWorkingDirectory(project.getBasePath());

		logRunConfig.setInterpreterPath("/bin/bash");
		logRunConfig.setInterpreterOptions("");
		logRunConfig.setExecuteInTerminal(false);
		logRunConfig.setExecuteScriptFile(true);

		return settings;
	}

	private static String resourcesFilePath(ToolWindow toolWindow) {
		var ideaPath = toolWindow.getProject()
								 .getBasePath() + StringConstants.SLASH + StringConstants.DOT + StringConstants.IDEA;

		var resourceFilePath = Path.of(ideaPath, StringConstants.UTILS, StringConstants.LOG + StringConstants.DOT + StringConstants.SH)
								   .toString();
		return resourceFilePath;
	}
}