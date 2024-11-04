package com.avizva.plugin.runservice.utils;

public enum LogsSecrets {

	INTEGRATION("Integration", "QUtJQTZRTjRONlM2TTdOT0dNQlQ=", "L0hvTlFESG1WWHZjQ2lRNEhPRnEwdXlWU2kvbjQ4a1V4ZytpUXd1cA==", "integration-core-product-eks-cluster", "integration-profile"),
	TEST("Test", "QUtJQTQ3Q1IzQUZNM0JVWVZZWkk=", "OFJvYXlzWloyV3dtSFN2bHhXWTVyZnp3anE4cVBYQ3JTR3RNU2FCaQ==", "test-core-product-eks-cluster", "test-profile"),
	RELEASE("Release", "QUtJQVZSVVZQM0xIUkhNNlRCT1g=", "MUl1UCtuVGVud2VtYVl6VHlta0xUWW84UnMvUUtmSWRTNStncUpKVg==", "release-core-product-eks-cluster", "release-profile");

	LogsSecrets(String env, String accessKey, String secretKey, String clusterName, String profileName) {
		this.env = env;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.clusterName = clusterName;
		this.profileName = profileName;
	}

	private String env;
	private String accessKey;
	private String secretKey;
	private String clusterName;
	private String profileName;

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}