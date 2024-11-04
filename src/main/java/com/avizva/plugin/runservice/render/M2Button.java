package com.avizva.plugin.runservice.render;

import javax.swing.*;

import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;

public class M2Button {
	private M2Button() {
	}

	public static JButton createButton(ToolWindow toolWindow) {
		var jButton = new JButton("M2 Delete");
		jButton.addActionListener(e -> {
			delete(toolWindow);
		});
		jButton.setIcon(AllIcons.Actions.DeleteTagHover);
		return jButton;
	}

	public static void delete(ToolWindow toolWindow) {
		var runService = new RunServiceImpl();
		runService.cleanM2Folder(toolWindow);
	}
}