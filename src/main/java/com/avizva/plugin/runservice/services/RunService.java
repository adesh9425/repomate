package com.avizva.plugin.runservice.services;

import java.util.List;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.entity.ProjectSpace;
import com.avizva.plugin.runservice.services.runconfiguration.Env;
import com.intellij.openapi.wm.ToolWindow;

public interface RunService {
	void cloneProjects(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData, String userName);

	List<ProjectMetaData> getProjectsGroup(ToolWindow toolWindow);

	void runConfigurationSetup(ToolWindow toolWindow, List<ProjectMetaData> projectMetaData);

	void getLogs(ToolWindow toolWindow, Env env, ProjectMetaData projectMetaData);

	void getPull(ToolWindow toolWindow);

	void init(ToolWindow toolWindow);

	void updateEnv(ToolWindow toolWindow, ProjectMetaData projectMetaData);

	void build(ToolWindow toolWindow, List<String> projects);

	void run(ToolWindow toolWindow);

	void refresh(ToolWindow toolWindow);

	void runModule(ToolWindow toolWindow, List<String> project);

	void jenkinBuild(ToolWindow toolWindow, List<String> project);

	void handleConfigurations(ToolWindow toolWindow, String projectSpace, String prefix);

	ProjectSpace getConfigurations(ToolWindow toolWindow);
	void remoteDebugging(ToolWindow toolWindow);
	void cleanM2Folder(ToolWindow toolWindow);

}