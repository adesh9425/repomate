package com.avizva.plugin.runservice.services.runconfiguration;

public enum Env {

	INTEGRATION("INTEGRATION"),
	TEST("TEST"),
	RELEASE("RELEASE");
	private String name;

	Env(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}