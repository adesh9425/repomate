package com.avizva.plugin.runservice.services.entity;

import com.avizva.plugin.runservice.services.git.RepoType;

public class ProjectMetaData {

	private String name;
	private String configName;
	private RepoType repoType;

	public ProjectMetaData() {
	}

	public ProjectMetaData(String name, RepoType repoType) {
		this.name = name;
		this.repoType = repoType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RepoType getRepoType() {
		return repoType;
	}

	public void setRepoType(RepoType repoType) {
		this.repoType = repoType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
}