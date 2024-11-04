package com.avizva.plugin.runservice.services.git;

public enum RepoType {
	USECASES("usecases", "master", "-usecases"),
	APP("app", "integration", "-web"),
	API("api", "integration", "-api"),
	FRAMEWORK("framework", "4.0.0", "");

	RepoType(String code, String branch, String projectSuffix) {
		this.code = code;
		this.branch = branch;
		this.projectSuffix = projectSuffix;
	}

	private final String code;

	private final String branch;

	private final String projectSuffix;

	public String getCode() {
		return code;
	}

	public String getBranch() {
		return branch;
	}

	public String getProjectSuffix() {
		return projectSuffix;
	}
}