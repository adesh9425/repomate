package com.avizva.plugin.runservice.services.runconfiguration;

import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellij.execution.DefaultExecutionTarget;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.wm.ToolWindow;

public final class BuildConfigurationManager {

	private BuildConfigurationManager() {
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(BuildConfigurationManager.class);

	public static void run(ToolWindow toolWindow, String configurationName) {
		var project = toolWindow.getProject();
		var runManager = RunManager.getInstance(project);
		var executionManager = ExecutionManagerImpl.getInstance(project);
		var configuration = runManager.findConfigurationByTypeAndName(MavenRunConfigurationType.getInstance(), configurationName);

		if (configuration != null) {
			var applicationRunConfiguration = ((RunnerAndConfigurationSettingsImpl) configuration).getConfiguration();
			((RunnerAndConfigurationSettingsImpl) configuration).setConfiguration(applicationRunConfiguration);

			var executor = DefaultRunExecutor.getRunExecutorInstance();
			var executionEnvironmentBuilder = ExecutionEnvironmentBuilder.create(executor, applicationRunConfiguration);
			try {
				var state = applicationRunConfiguration.getState(executor, executionEnvironmentBuilder.build());
				var executionResult = state.execute(executor, ProgramRunner.findRunnerById(executor.getId()));
				var processHandler = executionResult.getProcessHandler();
				var thread = new Thread(() -> executionManager.restartRunProfile(project, executor, DefaultExecutionTarget.INSTANCE, configuration, processHandler, null));
				thread.start();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}

		} else {
			LOGGER.info("Configuration not found: {}", configurationName);
		}
	}

}