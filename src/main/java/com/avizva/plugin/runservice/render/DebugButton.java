package com.avizva.plugin.runservice.render;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;

public final class DebugButton {

	private DebugButton() {
	}

	public static JButton createDebugButton(ToolWindow toolWindow) {
		var jButton = new JButton("Debug");
		jButton.addActionListener(e -> {
			debug(toolWindow);
		});
		jButton.setIcon(AllIcons.Actions.StartDebugger);
		return jButton;
	}

	private static void debug(ToolWindow toolWindow) {
		var runService = new RunServiceImpl();
		runService.run(toolWindow);
	}

}