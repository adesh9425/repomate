package com.avizva.plugin.runservice.render;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;

public class PullButton {
	private PullButton() {
	}

	public static JButton createPullButton(ToolWindow toolWindow) {
		var pullButton = new JButton("Pull");
		pullButton.addActionListener(e -> {
			pull(toolWindow);

		});
		pullButton.setIcon(AllIcons.Diff.ArrowLeftDown);

		return pullButton;
	}

	private static void pull(ToolWindow toolWindow) {
		var runService = new RunServiceImpl();
		runService.getPull(toolWindow);
	}
}