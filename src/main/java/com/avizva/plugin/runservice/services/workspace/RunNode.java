package com.avizva.plugin.runservice.services.workspace;

import java.util.stream.IntStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.services.git.RepoType;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.openapi.wm.ToolWindow;

public final class RunNode {

	private RunNode() {
	}

	public static Element getLogFilters(Document document) {
		var elements = document.getElementsByTagName("component");
		var logFilter = IntStream.range(0, elements.getLength())
								 .mapToObj(i -> (Element) elements.item(i))
								 .filter(config -> "LogFilter".equals(config.getAttribute("name")))
								 .findFirst();
		logFilter.ifPresent(document::removeChild);

		var component = document.createElement("LogFilters");
		component.setAttribute("FILTER_ERRORS", "false");
		component.setAttribute("FILTER_WARNINGS", "false");
		component.setAttribute("FILTER_INFO", "false");
		component.setAttribute("FILTER_DEBUG", "false");
		component.setAttribute("CUSTOM_FILTER", "false");
		return component;

	}

	public static Element getRunApplication(Document document, RepoType repoType, String project) {
		var repoName =   StringConstants.HYPHEN + project;
		var existingConfig = findConfigurationByName(document, repoName);
		if (existingConfig != null) {
			return existingConfig;
		}
		var configuration = document.createElement("configuration");
		configuration.setAttribute("name", repoName);
		configuration.setAttribute("type", "Application");
		configuration.setAttribute("factoryName", "Application");

		var option1 = document.createElement("option");
		option1.setAttribute("name", "ALTERNATIVE_JRE_PATH");
		option1.setAttribute("value", "temurin-17");
		configuration.appendChild(option1);

		var option2 = document.createElement("option");
		option2.setAttribute("name", "ALTERNATIVE_JRE_PATH_ENABLED");
		option2.setAttribute("value", "true");
		configuration.appendChild(option2);

		var option3 = document.createElement("option");
		option3.setAttribute("name", "MAIN_CLASS_NAME");
		option3.setAttribute("value", "com.avizva.frameworks.websetup.WebAppInitializer");
		configuration.appendChild(option3);

		var module = document.createElement("module");
		module.setAttribute("name",  StringConstants.HYPHEN + project + repoType.getProjectSuffix());
		configuration.appendChild(module);

		var extension = document.createElement("extension");
		extension.setAttribute("name", "net.ashald.envfile");

		var extOption1 = document.createElement("option");
		extOption1.setAttribute("name", "IS_ENABLED");
		extOption1.setAttribute("value", "false");
		extension.appendChild(extOption1);

		var extOption2 = document.createElement("option");
		extOption2.setAttribute("name", "IS_SUBST");
		extOption2.setAttribute("value", "false");
		extension.appendChild(extOption2);

		var extOption3 = document.createElement("option");
		extOption3.setAttribute("name", "IS_PATH_MACRO_SUPPORTED");
		extOption3.setAttribute("value", "false");
		extension.appendChild(extOption3);

		var extOption4 = document.createElement("option");
		extOption4.setAttribute("name", "IS_IGNORE_MISSING_FILES");
		extOption4.setAttribute("value", "false");
		extension.appendChild(extOption4);

		var extOption5 = document.createElement("option");
		extOption5.setAttribute("name", "IS_ENABLE_EXPERIMENTAL_INTEGRATIONS");
		extOption5.setAttribute("value", "false");
		extension.appendChild(extOption5);

		var entries = document.createElement("ENTRIES");

		var entry = document.createElement("ENTRY");
		entry.setAttribute("IS_ENABLED", "true");
		entry.setAttribute("IS_EXECUTABLE", "false");
		entry.setAttribute("PARSER", "runconfig");
		entries.appendChild(entry);

		extension.appendChild(entries);
		configuration.appendChild(extension);

		var method = document.createElement("method");
		method.setAttribute("v", "2");
		configuration.appendChild(method);

		return configuration;
	}

	public static Element getMavenRunConfiguration(Document document, RepoType repoType, String project) {
		var repoName =  StringConstants.HYPHEN + project;
		var existingConfig = findConfigurationByName(document, repoName + StringConstants.HYPHEN + StringConstants.BUILD);
		if (existingConfig != null) {
			return existingConfig;
		}
		var configuration = document.createElement("configuration");
		configuration.setAttribute("name", repoName + StringConstants.HYPHEN + StringConstants.BUILD);
		configuration.setAttribute("type", "MavenRunConfiguration");
		configuration.setAttribute("factoryName", "Maven");

		var mavenSettings = document.createElement("MavenSettings");

		var generalSettings = document.createElement("option");
		generalSettings.setAttribute("name", "myGeneralSettings");
		mavenSettings.appendChild(generalSettings);

		var runnerSettings = document.createElement("option");
		runnerSettings.setAttribute("name", "myRunnerSettings");

		var mavenRunnerSettings = document.createElement("MavenRunnerSettings");

		appendOption(document, mavenRunnerSettings, "delegateBuildToMaven", "false");

		var environmentProperties = document.createElement("option");
		environmentProperties.setAttribute("name", "environmentProperties");

		var envPropertiesMap = document.createElement("map");
		environmentProperties.appendChild(envPropertiesMap);

		mavenRunnerSettings.appendChild(environmentProperties);

		var mavenProperties = document.createElement("option");
		mavenProperties.setAttribute("name", "mavenProperties");

		var mavenPropertiesMap = document.createElement("map");
		mavenProperties.appendChild(mavenPropertiesMap);

		mavenRunnerSettings.appendChild(mavenProperties);

		appendOption(document, mavenRunnerSettings, "passParentEnv", "true");
		appendOption(document, mavenRunnerSettings, "runMavenInBackground", "true");
		appendOption(document, mavenRunnerSettings, "skipTests", "false");
		appendOption(document, mavenRunnerSettings, "vmOptions", "");

		runnerSettings.appendChild(mavenRunnerSettings);
		mavenSettings.appendChild(runnerSettings);

		Element runnerParameters = document.createElement("option");
		runnerParameters.setAttribute("name", "myRunnerParameters");

		var mavenRunnerParameters = document.createElement("MavenRunnerParameters");

		var profiles = document.createElement("option");
		profiles.setAttribute("name", "profiles");

		var profilesSet = document.createElement("set");
		profiles.appendChild(profilesSet);

		mavenRunnerParameters.appendChild(profiles);

		var goals = document.createElement("option");
		goals.setAttribute("name", "goals");

		var goalsList = document.createElement("list");

		appendOption(document, goalsList, "value", "-T");
		appendOption(document, goalsList, "value", "5");
		appendOption(document, goalsList, "value", "-U");
		appendOption(document, goalsList, "value", "install");
		appendOption(document, goalsList, "value", "-Denv=local");
		appendOption(document, goalsList, "value", "-Dmaven.compiler.useIncrementalCompilation=true");

		goals.appendChild(goalsList);
		mavenRunnerParameters.appendChild(goals);

		appendOption(document, mavenRunnerParameters, "pomFileName", "");

		Element profilesMap = document.createElement("option");
		profilesMap.setAttribute("name", "profilesMap");

		Element mapElement = document.createElement("map");
		profilesMap.appendChild(mapElement);

		mavenRunnerParameters.appendChild(profilesMap);

		appendOption(document, mavenRunnerParameters, "resolveToWorkspace", "false");
		appendOption(document, mavenRunnerParameters, "workingDirPath", "$PROJECT_DIR$/" + repoName);

		runnerParameters.appendChild(mavenRunnerParameters);
		mavenSettings.appendChild(runnerParameters);

		configuration.appendChild(mavenSettings);

		Element extension = document.createElement("extension");
		extension.setAttribute("name", "net.ashald.envfile");

		appendOption(document, extension, "IS_ENABLED", "true");
		appendOption(document, extension, "IS_SUBST", "false");
		appendOption(document, extension, "IS_PATH_MACRO_SUPPORTED", "false");
		appendOption(document, extension, "IS_IGNORE_MISSING_FILES", "false");
		appendOption(document, extension, "IS_ENABLE_EXPERIMENTAL_INTEGRATIONS", "false");

		var entries = document.createElement("ENTRIES");

		var entry = document.createElement("ENTRY");
		entry.setAttribute("IS_ENABLED", "true");
		entry.setAttribute("IS_EXECUTABLE", "false");
		entry.setAttribute("PARSER", "runconfig");
		entries.appendChild(entry);

		extension.appendChild(entries);
		configuration.appendChild(extension);

		var method = document.createElement("method");
		method.setAttribute("v", "2");
		configuration.appendChild(method);

		return configuration;
	}

	private static void appendOption(Document document, Element parent, String name, String value) {
		var option = document.createElement("option");
		option.setAttribute("name", name);
		option.setAttribute("value", value);
		parent.appendChild(option);
	}

	private static Element findConfigurationByName(Document document, String name) {
		var configurations = document.getElementsByTagName("configuration");
		return IntStream.range(0, configurations.getLength())
						.mapToObj(i -> (Element) configurations.item(i))
						.filter(config -> name.equals(config.getAttribute("name")))
						.findFirst()
						.orElse(null);
	}

	private static Element createRemoteDebugElement(Document doc, ToolWindow toolWindow, ProjectMetaData projectMetaData) {
		var name = projectMetaData.getName();
		var repoType = projectMetaData.getRepoType();
		var projectSuffix = repoType.getProjectSuffix();
		Element configuration = doc.createElement("configuration");
		configuration.setAttribute("name", "");
		configuration.setAttribute("type", "Remote");
		configuration.setAttribute("show_console_on_std_out", "true");

		appendChildElement(doc, configuration, "output_file", "path", "$PROJECT_DIR$/logs/");

		Element logFile1 = appendChildElement(doc, configuration, "log_file", "alias", "logs");
		logFile1.setAttribute("path", "$PROJECT_DIR$/logs/");
		logFile1.setAttribute("show_all", "true");
		logFile1.setAttribute("skipped", "false");

		appendChildElement(doc, configuration, "module", "name",  StringConstants.HYPHEN + name + repoType.getProjectSuffix());
		appendChildElement(doc, configuration, "option", "name", "USE_SOCKET_TRANSPORT", "value", "true");
		appendChildElement(doc, configuration, "option", "name", "SERVER_MODE", "value", "false");
		appendChildElement(doc, configuration, "option", "name", "SHMEM_ADDRESS");
		appendChildElement(doc, configuration, "option", "name", "HOST", "value", "");
		appendChildElement(doc, configuration, "option", "name", "PORT", "value", "8000");
		appendChildElement(doc, configuration, "option", "name", "AUTO_RESTART", "value", "false");

		Element runnerSettings = doc.createElement("RunnerSettings");
		runnerSettings.setAttribute("RunnerId", "Debug");
		appendChildElement(doc, runnerSettings, "option", "name", "DEBUG_PORT", "value", "8000");
		appendChildElement(doc, runnerSettings, "option", "name", "LOCAL", "value", "false");
		configuration.appendChild(runnerSettings);

		appendChildElement(doc, configuration, "method", "v", "2");

		return configuration;
	}

	public static Element LogConfiguration(Document doc) {
		var existingConfig = findConfigurationByName(doc, "Logs");
		if (existingConfig != null) {
			return existingConfig;
		}
		Element configuration = doc.createElement("configuration");
		configuration.setAttribute("name", "Logs");
		configuration.setAttribute("type", "ShConfigurationType");
		configuration.setAttribute("singleton", "false");

		// Create and append <option> elements
		createOptionElement(doc, configuration, "INDEPENDENT_SCRIPT_PATH", "true");
		createOptionElement(doc, configuration, "SCRIPT_PATH", "");
		createOptionElement(doc, configuration, "SCRIPT_OPTIONS", "");
		createOptionElement(doc, configuration, "INDEPENDENT_SCRIPT_WORKING_DIRECTORY", "true");
		createOptionElement(doc, configuration, "SCRIPT_WORKING_DIRECTORY", "$PROJECT_DIR$");
		createOptionElement(doc, configuration, "INDEPENDENT_INTERPRETER_PATH", "true");
		createOptionElement(doc, configuration, "INTERPRETER_PATH", "/bin/bash");
		createOptionElement(doc, configuration, "INTERPRETER_OPTIONS", "");
		createOptionElement(doc, configuration, "EXECUTE_IN_TERMINAL", "false");
		createOptionElement(doc, configuration, "EXECUTE_SCRIPT_FILE", "true");

		// Create the <envs> element
		Element envs = doc.createElement("envs");
		configuration.appendChild(envs);

		// Create and append <env> elements
		createEnvElement(doc, envs, "path", "");
		createEnvElement(doc, envs, "name", "");
		createEnvElement(doc, envs, "env", "");
		createEnvElement(doc, envs, "type", "");

		// Create and append the <method> element
		Element method = doc.createElement("method");
		method.setAttribute("v", "2");
		configuration.appendChild(method);
		return configuration;

	}

	private static Element appendChildElement(Document doc, Element parent, String tagName, String attrName, String attrValue) {
		Element child = doc.createElement(tagName);
		child.setAttribute(attrName, attrValue);
		parent.appendChild(child);
		return child;
	}

	private static Element appendChildElement(Document doc, Element parent, String tagName, String attrName1, String attrValue1, String attrName2, String attrValue2) {
		Element child = doc.createElement(tagName);
		child.setAttribute(attrName1, attrValue1);
		child.setAttribute(attrName2, attrValue2);
		parent.appendChild(child);
		return child;
	}

	private static void createOptionElement(Document doc, Element parent, String name, String value) {
		Element option = doc.createElement("option");
		option.setAttribute("name", name);
		option.setAttribute("value", value);
		parent.appendChild(option);
	}

	private static void createEnvElement(Document doc, Element parent, String name, String value) {
		Element env = doc.createElement("env");
		env.setAttribute("name", name);
		env.setAttribute("value", value);
		parent.appendChild(env);
	}

}