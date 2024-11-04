package com.avizva.plugin.runservice.services.jarfileprocessor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.avizva.plugin.runservice.render.table.ProjectTable;
import com.avizva.plugin.runservice.services.RunServiceComponent;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.openapi.wm.ToolWindow;

public class JarProcessor {

	public void resetJar(ToolWindow toolWindow) {
		var runServiceComponent = new RunServiceComponent();
		var basePath = toolWindow.getProject()
								 .getBasePath();

		var optionalProjectMetaData = runServiceComponent.getProjectsGroup(basePath)
														 .stream()
														 .filter(pro -> pro.getName()
																		   .contains("ex"))
														 .findFirst();
		if (optionalProjectMetaData.isEmpty()) {
			return;
		}

		var projectMetaData = optionalProjectMetaData.get();
		var jarFileLocalYamlPath = getJarFileLocalYamlPath(basePath, projectMetaData.getName());
		var localYamlPath = getFileLocalYamlPath(basePath, projectMetaData.getName());

		var env = ProjectTable.env();
		List<String> projectlist = List.of();

		updateLocalYml(jarFileLocalYamlPath, localYamlPath, projectlist);

	}

	public void updateJar(ToolWindow toolWindow) {
		var runServiceComponent = new RunServiceComponent();
		var basePath = toolWindow.getProject()
								 .getBasePath();

		var optionalProjectMetaData = runServiceComponent.getProjectsGroup(basePath)
														 .stream()
														 .filter(pro -> pro.getName()
																		   .contains("ex"))
														 .findFirst();
		if (optionalProjectMetaData.isEmpty()) {
			return;
		}

		var projectMetaData = optionalProjectMetaData.get();
		var jarFileLocalYamlPath = getJarFileLocalYamlPath(basePath, projectMetaData.getName());
		var localYamlPath = getFileLocalYamlPath(basePath, projectMetaData.getName());

		var env = ProjectTable.env();
		var projectlist = env.entrySet()
							 .stream()
							 .filter(kv -> kv.getValue()
											 .equalsIgnoreCase(StringConstants.INTEGRATION))
							 .map(Map.Entry::getKey)
							 .toList();

		updateLocalYml(jarFileLocalYamlPath, localYamlPath, projectlist);

	}

	private static void updateLocalYml(String jarFileLocalYamlPath, String localYamlPath, List<String> projects) {

		try {
			Yaml yaml = new Yaml();
			FileInputStream inputStream = new FileInputStream(localYamlPath);
			Map<String, Object> yamlData = yaml.load(inputStream);
			projects.forEach(s -> swap(yamlData, s));



			Files.delete(Path.of(jarFileLocalYamlPath));

			var options = new DumperOptions();
			options.setIndent(2);
			options.setPrettyFlow(true);
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

			var yamlWriter = new Yaml(options);
			var writer = new FileWriter(jarFileLocalYamlPath, StandardCharsets.UTF_8);
			yamlWriter.dump(yamlData, writer);
			writer.close();
			System.out.println("YAML file updated successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getJarFileLocalYamlPath(String basePath, String project) {
		var stringBuilder = new StringBuilder();
		var path = stringBuilder.append(basePath)
								.append(StringConstants.SLASH)
								.append(project)
								.append(StringConstants.SLASH)
								.append(project)
								.append(StringConstants.HYPHEN)
								.append(StringConstants.WEB)
								.append(StringConstants.SLASH)
								.append(StringConstants.TARGET)
								.append(StringConstants.SLASH)
								.append(StringConstants.CLASSES)
								.append(StringConstants.SLASH)
								.append(StringConstants.LOCAL)
								.append(StringConstants.DOT)
								.append(StringConstants.YML)
								.toString();

		return path;

	}

	private static String getFileLocalYamlPath(String basePath, String project) {
		var stringBuilder = new StringBuilder();
		var path = stringBuilder.append(basePath)
								.append(StringConstants.SLASH)
								.append(project)
								.append(StringConstants.SLASH)
								.append(project)
								.append(StringConstants.HYPHEN)
								.append(StringConstants.WEB)
								.append(StringConstants.SLASH)
								.append(StringConstants.SRC)
								.append(StringConstants.SLASH)
								.append(StringConstants.MAIN)
								.append(StringConstants.SLASH)
								.append(StringConstants.RESOURCES)
								.append(StringConstants.SLASH)
								.append(StringConstants.LOCAL)
								.append(StringConstants.DOT)
								.append(StringConstants.YML)
								.toString();

		return path;

	}

	private static void swap(Map<String, Object> yamlData, String projectName) {

		var application = (Map<String, Object>) yamlData.get("application");
		if (application != null) {
			var remoteapp = (Map<String, String>) application.get("remoteapp");
			var integration = (Map<String, String>) application.get("integration");
			if ((remoteapp != null) && (integration != null)) {
				var projectNameArray = projectName.split("-");
				var projectSearch = projectNameArray[projectNameArray.length - 1];

				if (remoteapp.containsKey(projectSearch) && integration.containsKey(projectSearch)) {
					remoteapp.put(projectSearch, integration.get(projectSearch));
				} else {
					System.out.println("Entry not found in both sections: " + projectName);
				}
			} else {
				System.out.println("remoteapp or integration section missing in YAML file.");
			}
		} else {
			System.out.println("application section missing in YAML file.");
		}

	}
}