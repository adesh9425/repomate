package com.avizva.plugin.runservice.render;

import java.awt.*;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.avizva.plugin.runservice.services.RunService;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;

public class ButtonPanel {
	private final ToolWindow toolWindow;

	public ButtonPanel(ToolWindow toolWindow) {
		this.toolWindow = toolWindow;
	}

	@NotNull
	public JBPanel createButtonPanel(RunService runService) {
		var jbPanel = new BorderLayoutPanel();
		var topPanel = getTopPanel(runService);
		var bottomPanel = getBottomPanel(runService);
		jbPanel.setLayout(new BoxLayout(jbPanel, BoxLayout.PAGE_AXIS));
		jbPanel.add(topPanel, Component.RIGHT_ALIGNMENT);
		jbPanel.add(bottomPanel, Component.BOTTOM_ALIGNMENT);
		return jbPanel;
	}

	private JBPanel getTopPanel(RunService runService) {
		var buttonPanel = new JBPanel();
		var pullButton = PullButton.createPullButton(toolWindow);
		buttonPanel.add(pullButton, BorderLayout.PAGE_START);

		var buildButton = BuildButton.createBuildButton(toolWindow);
		buttonPanel.add(buildButton);

		var debugButton = DebugButton.createDebugButton(toolWindow);
		buttonPanel.add(debugButton);

		var checkoutButton = CloneBox.createCheckoutButton(toolWindow);
		buttonPanel.add(checkoutButton);

		return buttonPanel;
	}

	private JBPanel getBottomPanel(RunService runService) {
		var buttonPanel = new JBPanel();

		var logsButton = new LogsButton(runService).createLogsButton(toolWindow);
		buttonPanel.add(logsButton, BorderLayout.AFTER_LINE_ENDS);

		var m2Button = M2Button.createButton(toolWindow);
		buttonPanel.add(m2Button, BorderLayout.PAGE_END);

		var configurationButton = ConfigurationButton.createConfigurationButton(toolWindow);
		buttonPanel.add(configurationButton);

		return buttonPanel;
	}

}