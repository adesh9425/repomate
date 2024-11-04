package com.avizva.plugin.runservice.services.workspace;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.avizva.plugin.runservice.services.git.RepoType;
import com.intellij.openapi.wm.ToolWindow;

public class WorkSpaceManager {

	public @Nullable Document getDocument(ToolWindow toolWindow) {

		try {

			var virtualFile = toolWindow.getProject()
										.getWorkspaceFile();
			var factory = DocumentBuilderFactory.newInstance();
			var builder = factory.newDocumentBuilder();
			var inputStream = virtualFile.getInputStream();
			var document = builder.parse(inputStream);
			inputStream.close();

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

	public Element findOrCreateRunManager(Document document) {
		var components = document.getElementsByTagName("component");
		var length = components.getLength();
		var element = IntStream.range(0, length)
							   .mapToObj(components::item)
							   .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
							   .map(node -> (Element) node)
							   .filter(ele -> "RunManager".equalsIgnoreCase(ele.getAttribute("name")))
							   .findFirst()
							   .orElse(null);
		if (element != null) {
			return element;
		}

		var projectElement = (Element) document.getElementsByTagName("project")
											   .item(0);
		var runManagerElement = document.createElement("component");
		runManagerElement.setAttribute("name", "RunManager");
		projectElement.appendChild(runManagerElement);

		return runManagerElement;
	}

	public void manipulateRunManager(Element runManagerElement, RepoType repoType, String project) {
		var document = runManagerElement.getOwnerDocument();
		var application = RunNode.getRunApplication(document, repoType, project);
		var mavenRunConfiguration = RunNode.getMavenRunConfiguration(document, repoType, project);
		var logConfiguration = RunNode.LogConfiguration(document);
		runManagerElement.appendChild(application);
		runManagerElement.appendChild(mavenRunConfiguration);
		runManagerElement.appendChild(logConfiguration);
	}

	public void writeDocumentToFile(Document document, File file) {

		try {
			var transformerFactory = TransformerFactory.newInstance();
			var transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			var source = new DOMSource(document);
			var result = new StreamResult(file);

			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

	}

}