package com.avizva.plugin.runservice.services.runconfiguration.base_configuration;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.wm.ToolWindow;

public final class ApplicationConfigurationHandler {

	private ApplicationConfigurationHandler() {
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfigurationHandler.class);

	public static void createOrUpdateRunConfiguration(ToolWindow toolWindow, ProjectMetaData projectMetaData) {
		var project = toolWindow.getProject();
		var repoType = projectMetaData.getRepoType();
		var appName = projectMetaData.getConfigName();
		var workingDir = project.getBasePath() + StringConstants.SLASH + projectMetaData.getName();
		var runManager = RunManager.getInstance(project);
		var moduleManager = ModuleManager.getInstance(project);
		var modulePath = getModulePath(projectMetaData);

		var existingConfig = runManager.findConfigurationByTypeAndName(ApplicationConfigurationType.getInstance(), appName);

		if (existingConfig == null) {

			var configSettings = runManager.createConfiguration(appName, ApplicationConfigurationType.class);

			var configuration = (ApplicationConfiguration) configSettings.getConfiguration();
			var pathOfJava = getJavaVersionForProject(toolWindow);
			configuration.setAlternativeJrePath(pathOfJava);
			configuration.setMainClassName("com.avizva.frameworks.websetup.WebAppInitializer");
			var getBeforeRunTasks = configuration.getBeforeRunTasks();
			getBeforeRunTasks.clear();
			configuration.setBeforeRunTasks(getBeforeRunTasks);

			configuration.setAlternativeJrePathEnabled(true);
			configuration.setWorkingDirectory(workingDir);

			configuration.setModuleName(modulePath);

			runManager.addConfiguration(configSettings);
		} else {

			var configuration = (ApplicationConfiguration) existingConfig.getConfiguration();
			configuration.setMainClassName("com.avizva.frameworks.websetup.WebAppInitializer");
			var pathOfJava = getJavaVersionForProject(toolWindow);
			configuration.setAlternativeJrePath(pathOfJava);
			configuration.setAlternativeJrePathEnabled(true);
			configuration.setAlternativeJrePathEnabled(true);
			configuration.setModuleName(modulePath);
		}

	}

	public static void createMavenRunConfiguration(ToolWindow toolWindow, ProjectMetaData projectMetaData) {
		var project = toolWindow.getProject();
		var repoName = projectMetaData.getConfigName();
		var workingDir = project.getBasePath() + StringConstants.SLASH + projectMetaData.getName();
		var runManager = RunManager.getInstance(project);

		var mavenConfigType = MavenRunConfigurationType.getInstance();
		var existingConfig = runManager.findConfigurationByTypeAndName(MavenRunConfigurationType.getInstance(), repoName);

		if (existingConfig != null) {
			LOGGER.info("Configuration already exists: {}", repoName);
			return;
		}
		var settings = runManager.createConfiguration(repoName, mavenConfigType.getConfigurationFactories()[0]);

		var mavenRunConfig = (MavenRunConfiguration) settings.getConfiguration();

		var runnerParameters = new MavenRunnerParameters();
		runnerParameters.setWorkingDirPath(workingDir);
		var mavenGoal = getMavenGoal();
		runnerParameters.setGoals(mavenGoal);
		mavenRunConfig.setRunnerParameters(runnerParameters);

		var generalSettings = new MavenGeneralSettings();

		mavenRunConfig.setGeneralSettings(generalSettings);

		var runnerSettings = new MavenRunnerSettings();
		runnerSettings.setDelegateBuildToMaven(false);
		runnerSettings.setPassParentEnv(true);
		runnerSettings.setVmOptions("-Xmx1024m");
		mavenRunConfig.setRunnerSettings(runnerSettings);

		runManager.addConfiguration(settings);
		runManager.setSelectedConfiguration(settings);
	}

	private static String getModulePath(ProjectMetaData projectMetaData) {
		var stringBuilder = new StringBuilder();
		var name = projectMetaData.getName();
		var projectSuffix = projectMetaData.getRepoType()
										   .getProjectSuffix();
		var modulePath = stringBuilder.append(name)
									  .append(projectSuffix)
									  .toString();

		return modulePath;
	}

	public static String getJavaVersionForProject(ToolWindow toolWindow) {

		var project = toolWindow.getProject();

		var sdk = ProjectRootManager.getInstance(project)
									.getProjectSdk();

		if ((sdk != null) && (sdk.getSdkType() instanceof JavaSdk)) {

			var javaSdkVersion = JavaSdk.getInstance()
										.getVersion(sdk);

			if (javaSdkVersion != null) {
				LOGGER.info("Java version enabled for the project: {} ", javaSdkVersion.getDescription());
			} else {
				LOGGER.error("Unable to determine the Java version.");
			}
		} else {
			LOGGER.info("The project does not have a Java SDK configured.");
		}
		return sdk.getName();
	}

	private static List<String> getMavenGoal() {
		List<String> goals = new ArrayList<>();
		goals.add(StringConstants.HYPHEN + StringConstants.T);
		goals.add(StringConstants.FIVE);
		goals.add(StringConstants.CLEAN);
		goals.add(StringConstants.HYPHEN + StringConstants.DSPELL_CHECK + StringConstants.EQUAL + StringConstants.FALSE);
		goals.add(StringConstants.INSTALL);
		goals.add(StringConstants.HYPHEN + StringConstants.DENV + StringConstants.EQUAL + StringConstants.LOCAL);
		goals.add(StringConstants.HYPHEN + StringConstants.DMAVEN + StringConstants.DOT + StringConstants.COMPILER + StringConstants.DOT + StringConstants.USER_INCREMENTAL_COMPILATION + StringConstants.EQUAL + StringConstants.TRUE);

		return goals;
	}

}