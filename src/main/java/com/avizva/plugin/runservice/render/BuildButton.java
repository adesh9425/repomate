package com.avizva.plugin.runservice.render;

import javax.swing.*;

import com.avizva.plugin.runservice.render.table.ProjectTable;
import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;

public final class BuildButton {

	private BuildButton() {
	}

	public static JButton createBuildButton(ToolWindow toolWindow) {
		var jButton = new JButton("Build");
		jButton.addActionListener(e -> {
			build(toolWindow);
		});
		jButton.setIcon(AllIcons.Actions.BuildLoadChanges);
		return jButton;
	}

	private static void build(ToolWindow toolWindow) {
		var runService = new RunServiceImpl();
		var projectList= ProjectTable.env()
						  .keySet()
						  .stream()
						  .toList();
		runService.build(toolWindow,projectList);
	}
}