package com.avizva.plugin.runservice.services.entity;

public enum ProjectType {
	WEB("web"),
	API("api");

	ProjectType(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}