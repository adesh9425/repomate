package com.avizva.plugin.runservice.services;

import java.util.List;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.entity.ProjectSpace;
import com.avizva.plugin.runservice.services.runconfiguration.Env;
import com.avizva.plugin.runservice.services.workspace.PomManager;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class RunServiceImpl implements RunService {

	private final RunServiceComponent runServiceComponent;

	public RunServiceImpl() {
		this.runServiceComponent = new RunServiceComponent();
	}

	@Override
	public void cloneProjects(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData, String userName) {
		runServiceComponent.cloneProjects(toolWindow, projectMetaData, userName);
		refreshProjectView(toolWindow.getProject());
	}

	@Override
	public List<ProjectMetaData> getProjectsGroup(ToolWindow toolWindow) {
		var basePath = toolWindow.getProject()
								 .getBasePath();
		var projectMetaData = runServiceComponent.getProjectsGroup(basePath);
		return projectMetaData;
	}

	@Override
	public void runConfigurationSetup(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData) {
		runServiceComponent.createConfigurationSetup(toolWindow);

	}

	@Override
	public void getLogs(ToolWindow toolWindow, Env env, ProjectMetaData projectMetaData) {
		runServiceComponent.getLogs(toolWindow, env, projectMetaData);

	}

	@Override
	public void getPull(ToolWindow toolWindow) {
		runServiceComponent.getPull(toolWindow);
		refreshProjectView(toolWindow.getProject());
	}

	@Override
	public void init(ToolWindow toolWindow) {
		var basePath = toolWindow.getProject()
								 .getBasePath();

		var projectMetaDataList = runServiceComponent.getProjectsGroup(basePath);
		projectMetaDataList.forEach(projectMetaData -> {
			var fileName = projectMetaData.getName();
			var fileNameList = fileName.split("-");
			var name = fileNameList[fileNameList.length - 1];
			projectMetaData.setName(name);

		});
		runServiceComponent.createConfigurationSetup(toolWindow);
		runServiceComponent.init(toolWindow);

	}

	@Override
	public void updateEnv(ToolWindow toolWindow, ProjectMetaData projectMetaData) {

	}

	@Override
	public void build(ToolWindow toolWindow, List<String> projects) {
		var thread = new Thread(() -> runServiceComponent.build(toolWindow, projects));
		thread.start();
	}

	@Override
	public void run(ToolWindow toolWindow) {
		//var env = ProjectTable.env();
		//var jarProcessor = new JarProcessor();
		//jarProcessor.updateJar(toolWindow);

		var projectMetaData = getProjectsGroup(toolWindow);
		var projects = projectMetaData.stream()
									  .map(ProjectMetaData::getConfigName)
									  .toList();
		runServiceComponent.run(toolWindow, projects);
	}

	@Override
	public void runModule(ToolWindow toolWindow, List<String> project) {
		runServiceComponent.run(toolWindow, project);
	}

	@Override
	public void jenkinBuild(ToolWindow toolWindow, List<String> project) {

	}

	@Override
	public void refresh(ToolWindow toolWindow) {
		var projectMetaDataList = getProjectsGroup(toolWindow);
		runServiceComponent.createConfigurationSetup(toolWindow);
		var projects = projectMetaDataList.stream()
										  .map(ProjectMetaData::getName)
										  .toList();
		PomManager.handle(toolWindow, projects);
//		var jarProcessor = new JarProcessor();
//		jarProcessor.resetJar(toolWindow);
		refreshProjectView(toolWindow.getProject());

	}

	public static void refreshProjectView(Project project) {
		ApplicationManager.getApplication()
						  .invokeLater(() -> {
							  var projectView = ProjectView.getInstance(project);
							  projectView.refresh();
						  });
	}

	@Override
	public void handleConfigurations(ToolWindow toolWindow, String projectSpace, String prefix) {
		runServiceComponent.handleConfigurations(toolWindow, projectSpace, prefix);
	}

	@Override
	public ProjectSpace getConfigurations(ToolWindow toolWindow) {
		var projectSpace = runServiceComponent.getConfigurations(toolWindow);
		return projectSpace;
	}

	@Override
	public void remoteDebugging(ToolWindow toolWindow) {

	}

	@Override
	public void cleanM2Folder(ToolWindow toolWindow) {
		runServiceComponent.cleanM2Folder(toolWindow);
	}
}