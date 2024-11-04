package com.avizva.plugin.runservice.services.runconfiguration;

public enum Configuration {
	LOGS("Logs");
	private String name;

	Configuration(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}