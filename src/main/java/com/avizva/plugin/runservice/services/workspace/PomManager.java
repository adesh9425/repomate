package com.avizva.plugin.runservice.services.workspace;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.openapi.wm.ToolWindow;

public final class PomManager {
	private static Document document = null;
	private static File pomFile = null;

	private PomManager() {
	}

	public static void handle(ToolWindow toolWindow, List<String> projects) {
		getDocument(toolWindow);
		manage(projects);
		writeDocumentToFile();
	}

	private static @Nullable Document getDocument(ToolWindow toolWindow) {
		var pomPath = toolWindow.getProject()
								.getBasePath() + StringConstants.SLASH + StringConstants.POM;
		try {

			pomFile = new File(pomPath);
			var factory = DocumentBuilderFactory.newInstance();
			var builder = factory.newDocumentBuilder();

			document = builder.parse(pomFile);
			document.getDocumentElement()
					.normalize();

			return document;

		} catch (ParserConfigurationException | IOException e) {
			e.printStackTrace();
		} catch (org.xml.sax.SAXException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private static void manage(List<String> projects) {
		NodeList existingModulesList = document.getElementsByTagName("modules");
		NodeList packagingList = document.getElementsByTagName("packaging");
		if (existingModulesList.getLength() > 0) {
			Node existingModules = existingModulesList.item(0);
			existingModules.getParentNode()
						   .removeChild(existingModules);

		}
		if (packagingList.getLength() > 0) {
			Node packagingNode = packagingList.item(0);
			packagingNode.getParentNode()
						 .removeChild(packagingNode);
		}

		// Create a new <modules> element
		Element modulesElement = document.createElement("modules");
		Element packagingElement = document.createElement("packaging");
		packagingElement.appendChild(document.createTextNode("pom"));
		// Add new modules
		projects.forEach(moduleName -> {
			Element moduleElement = document.createElement("module");
			moduleElement.appendChild(document.createTextNode(moduleName));
			modulesElement.appendChild(moduleElement);
		});

		document.getDocumentElement()
				.appendChild(packagingElement);
		document.getDocumentElement()
				.appendChild(modulesElement);

	}

	private static void writeDocumentToFile() {

		try {
			var transformerFactory = TransformerFactory.newInstance();
			var transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			var source = new DOMSource(document);
			var result = new StreamResult(pomFile);

			transformer.transform(source, result);

		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

	}
}