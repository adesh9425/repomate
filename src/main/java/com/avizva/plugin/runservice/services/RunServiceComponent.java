package com.avizva.plugin.runservice.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.entity.ProjectSpace;
import com.avizva.plugin.runservice.services.git.RepoPull;
import com.avizva.plugin.runservice.services.git.RepoType;
import com.avizva.plugin.runservice.services.runconfiguration.BuildConfigurationManager;
import com.avizva.plugin.runservice.services.runconfiguration.Env;
import com.avizva.plugin.runservice.services.runconfiguration.LogsConfigurationManager;
import com.avizva.plugin.runservice.services.runconfiguration.RunConfigurationManager;
import com.avizva.plugin.runservice.services.runconfiguration.base_configuration.ApplicationConfigurationHandler;
import com.avizva.plugin.runservice.services.task.TaskManager;
import com.avizva.plugin.runservice.services.workspace.WorkSpaceManager;
import com.avizva.plugin.runservice.utils.LogsSecrets;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.vcs.ui.VcsBalloonProblemNotifier;
import com.intellij.openapi.wm.ToolWindow;

public class RunServiceComponent {
	private static final CompletableFuture<Void>[] EMPTY_FUTURE_ARRAY = new CompletableFuture[0];

	private static final Logger LOGGER = LoggerFactory.getLogger(RunServiceComponent.class);

	public void cloneProjects(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData, String name) {
		var baseProject = toolWindow.getProject();
		var completableFutures = projectMetaData.stream()
												.map(project -> CompletableFuture.runAsync(() -> {
													var taskManager = new TaskManager(baseProject, "Downloading " + project.getName(), false);
													taskManager.setProjectMetaData(project);
													taskManager.setToolWindow(toolWindow);
													taskManager.setUserName(name);

													try {
														ProgressManager.getInstance()
																	   .run(taskManager);
													} catch (Exception e) {

														if (Thread.currentThread()
																  .isInterrupted()) {
															LOGGER.error("Task for {} was interrupted.", project.getName());
														}
													}
												}))
												.collect(Collectors.toCollection(CopyOnWriteArrayList::new));

		LOGGER.info("All tasks completed or cancelled. Proceeding with the next part.");

	}

	public void createConfigurationSetup(ToolWindow toolWindow) {
		var project = toolWindow.getProject();
		var projectMetaData = getProjectsGroup(project.getBasePath());

		projectMetaData.stream()
					   .filter(projectData -> !projectData.getConfigName()
														  .contains(StringConstants.USECASES))
					   .forEach(projectData -> {
						   ApplicationConfigurationHandler.createMavenRunConfiguration(toolWindow, projectData);
					   });

		projectMetaData.stream()
					   .filter(projectData -> !projectData.getConfigName()
														  .contains(StringConstants.COMMON))
					   .filter(projectData -> !projectData.getConfigName()
														  .contains(StringConstants.USECASES))
					   .forEach(projectData -> {
						   ApplicationConfigurationHandler.createOrUpdateRunConfiguration(toolWindow, projectData);
					   });

	}

	public void runConfigurationSetup(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData) {
		var virtualFile = toolWindow.getProject()
									.getWorkspaceFile();
		if (!virtualFile.isValid()) {
			return;
		}
		var workSpaceManager = new WorkSpaceManager();
		var document = workSpaceManager.getDocument(toolWindow);
		var runManagerElement = workSpaceManager.findOrCreateRunManager(document);
		projectMetaData.forEach(project -> {
			workSpaceManager.manipulateRunManager(runManagerElement, project.getRepoType(), project.getName());
		});

		workSpaceManager.writeDocumentToFile(document, new File(virtualFile.getPath()));
	}

	public void getPull(ToolWindow toolWindow) {
		var basePath = toolWindow.getProject()
								 .getBasePath();
		var projectMetaDataList = getProjectsGroup(basePath);
		var projects = projectMetaDataList.stream()
										  .map(ProjectMetaData::getName)
										  .toList();
		var repoPull = new RepoPull();

		repoPull.getLatestPull(basePath, projects);
	}

	public void getLogs(ToolWindow toolWindow, Env env, ProjectMetaData projectMetaData) {
		var project = toolWindow.getProject();
		var args = getArgs(env.getName(), projectMetaData);
		LogsConfigurationManager.run(project, projectMetaData.getName() + StringConstants.HYPHEN + env.getName(), args, toolWindow);

	}

	private static Map<String, String> getArgs(String env, ProjectMetaData projectMetaData) {
		var name = projectMetaData.getName();
		var projectSuffix = projectMetaData.getRepoType()
										   .getProjectSuffix();
		var logsSecrets = LogsSecrets.valueOf(env);

		Map<String, String> args = new HashMap<>();
		args.put(StringConstants.ENV, env);
		args.put(StringConstants.NAME, name);
		args.put(StringConstants.TYPE, projectSuffix);
		args.put(StringConstants.ACCESS_KEY_ID, logsSecrets.getAccessKey());
		args.put(StringConstants.SECRET_KEY_ID, logsSecrets.getSecretKey());
		args.put(StringConstants.CLUSTER_NAME, logsSecrets.getClusterName());
		args.put(StringConstants.PROFILE_NAME, logsSecrets.getProfileName());

		return args;
	}

	private static String resourcePath(ToolWindow toolWindow) {
		var basePath = toolWindow.getProject()
								 .getBasePath();
		var pathBuilder = new StringBuilder();
		var path = pathBuilder.append(basePath)
							  .append(StringConstants.SLASH)
							  .append(StringConstants.SRC)
							  .append(StringConstants.SLASH)
							  .append(StringConstants.MAIN)
							  .append(StringConstants.SLASH)
							  .append(StringConstants.RESOURCES)
							  .toString();
		return path;
	}

	public List<ProjectMetaData> getProjectsGroup(String basePath) {

		var projectFile = new File(basePath);
		var projectslist = Arrays.stream(projectFile.listFiles())
								 .filter(File::isDirectory)
								 .filter(file -> {
									 var nestedFiles = Arrays.stream(file.listFiles())
															 .filter(nested -> "pom.xml".equalsIgnoreCase(nested.getName()))
															 .findAny();
									 return nestedFiles.isPresent();
								 })
								 .map(RunServiceComponent::getProjectMetaData)
								 .toList();

		return projectslist;
	}

	private static ProjectMetaData getProjectMetaData(File file) {
		var projectMetaData = new ProjectMetaData();
		projectMetaData.setName(file.getName());
		var nestedFilesCount = Arrays.stream(file.listFiles())
									 .filter(nested -> nested.getName()
															 .contains("api") || nested.getName()
																					   .contains("web"))
									 .count();

		var nameList = file.getName()
						   .split(StringConstants.HYPHEN);
		var configName = nameList[nameList.length - 1];
		projectMetaData.setConfigName(configName);
		if (nestedFilesCount == 2) {
			projectMetaData.setRepoType(RepoType.APP);
		} else if (nestedFilesCount == 1) {
			projectMetaData.setRepoType(RepoType.API);
		} else {
			projectMetaData.setRepoType(RepoType.FRAMEWORK);
		}

		return projectMetaData;
	}

	public void init(ToolWindow toolWindow) {
		var ideaPath = toolWindow.getProject()
								 .getBasePath() + StringConstants.SLASH + StringConstants.DOT + StringConstants.IDEA;
		var fileName = fileName();
		var configfileName = configfileName();
		getResourceAsPath(fileName, ideaPath);
		createConfigJson(configfileName, ideaPath);
	}

	private void createConfigJson(String fileName, String ideaPath) {
		var file = new File(String.join(StringConstants.SLASH, ideaPath, StringConstants.UTILS, fileName));
		if (file.exists()) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void getResourceAsPath(String resourceName, String targetPath) {

		try (var resourceStream = getClass().getClassLoader()
											.getResourceAsStream(resourceName)) {
			if (resourceStream != null) {
				var targetDirectory = Path.of(targetPath, StringConstants.UTILS);
				Files.createDirectories(targetDirectory);

				var targetFile = targetDirectory.resolve(resourceName);
				Files.copy(resourceStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new IOException("Resource not found: " + resourceName);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String fileName() {
		var stringBuilder = new StringBuilder();
		var logsFile = stringBuilder.append(StringConstants.LOG)
									.append(StringConstants.DOT)
									.append(StringConstants.SH)
									.toString();
		return logsFile;

	}

	private static String configfileName() {
		var stringBuilder = new StringBuilder();
		var logsFile = stringBuilder.append(StringConstants.CONFIGURATIONS)
									.append(StringConstants.DOT)
									.append(StringConstants.JSON)
									.toString();
		return logsFile;

	}

	public void build(ToolWindow toolWindow, List<String> projects) {
		projects.forEach(project -> BuildConfigurationManager.run(toolWindow, project));
	}

	public void run(ToolWindow toolWindow, List<String> projects) {
		var basePath = toolWindow.getProject()
								 .getBasePath();

		projects.forEach(project -> {
			RunConfigurationManager.run(toolWindow, project);
		});
	}

	public void handleConfigurations(ToolWindow toolWindow, String projectSpace, String prefix) {
		var objectMapper = new ObjectMapper();
		var projectSpaceObject = getProjectSpaceObject(projectSpace, prefix);

		try {
			var jsonString = objectMapper.writeValueAsString(projectSpaceObject);
			var filePath = resourcesFilePath(toolWindow);
			var path = Paths.get(filePath);
			Files.writeString(path, jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		;
	}

	private static ProjectSpace getProjectSpaceObject(String projectSpace, String prefix) {
		var projectSpaceObject = new ProjectSpace();
		projectSpaceObject.setProjectSpace(projectSpace);
		projectSpaceObject.setProjectPrefix(prefix);
		return projectSpaceObject;

	}

	public ProjectSpace getConfigurations(ToolWindow toolWindow) {
		return getConfig(toolWindow);

	}

	public static ProjectSpace getConfig(ToolWindow toolWindow) {
		var resourcesFilePath = resourcesFilePath(toolWindow);
		var objectMapper = new ObjectMapper();
		try {
			var jsonNode = objectMapper.readTree(new File(resourcesFilePath));
			var projectSpace = objectMapper.convertValue(jsonNode, ProjectSpace.class);
			if (projectSpace == null) {
				return new ProjectSpace();
			}
			return projectSpace;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static String resourcesFilePath(ToolWindow toolWindow) {
		var ideaPath = toolWindow.getProject()
								 .getBasePath() + StringConstants.SLASH + StringConstants.DOT + StringConstants.IDEA;

		var resourceFilePath = Path.of(ideaPath, StringConstants.UTILS, StringConstants.CONFIGURATIONS + StringConstants.DOT + StringConstants.JSON)
								   .toString();
		return resourceFilePath;
	}

	public void cleanM2Folder(ToolWindow toolWindow) {
		var avizvaM2Path = getM2Path();
		try {
			var avizvaM2Dir = new File(avizvaM2Path);
			FileUtils.deleteDirectory(avizvaM2Dir);
			var notification = VcsBalloonProblemNotifier.NOTIFICATION_GROUP.createNotification("The avizva M2 repositories has been deleted", NotificationType.INFORMATION);
			Notifications.Bus.notify(notification);
		} catch (IOException e) {
			var notification = VcsBalloonProblemNotifier.NOTIFICATION_GROUP.createNotification("Some problem has occurred please manually delete M2", NotificationType.ERROR);
			Notifications.Bus.notify(notification);
			throw new RuntimeException(e);
		}

	}

	private String getM2Path() {
		var stringBuilder = new StringBuilder();
		var homeDirectory = System.getProperty(StringConstants.USER + StringConstants.DOT + StringConstants.HOME);
		var avizvaM2Path = stringBuilder.append(homeDirectory)
										.append(StringConstants.SLASH)
										.append(StringConstants.DOT)
										.append(StringConstants.M2)
										.append(StringConstants.SLASH)
										.append(StringConstants.REPOSITORY)
										.append(StringConstants.SLASH)
										.append(StringConstants.COM)
										.append(StringConstants.SLASH)
										.append(StringConstants.AVIZVA)
										.toString();
		return avizvaM2Path;

	}
}