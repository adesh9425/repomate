package com.avizva.plugin.runservice.services;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.git.RepoSetup;
import com.intellij.openapi.wm.ToolWindow;

public final class RunManager {

	private RunManager() {
	}

	private static class SingletonHelper {
		private static final RunManager INSTANCE = new RunManager();
	}

	public static RunManager getInstance() {
		return SingletonHelper.INSTANCE;
	}

	public void clone(ToolWindow toolWindow, ProjectMetaData projectMetaData, String userName) {
		var repoSetup = new RepoSetup(toolWindow);
		repoSetup.cloneRepo(projectMetaData, userName);

	}
}