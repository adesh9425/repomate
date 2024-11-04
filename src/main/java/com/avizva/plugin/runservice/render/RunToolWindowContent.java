package com.avizva.plugin.runservice.render;

import java.awt.*;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.avizva.plugin.runservice.render.table.ProjectTable;
import com.avizva.plugin.runservice.services.RunService;
import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;

public class RunToolWindowContent {

	private final JBPanel contentPanel = new JBPanel();
	private static final RunService runService = new RunServiceImpl();

	public RunToolWindowContent(ToolWindow toolWindow, Project project) {
		runService.init(toolWindow);
		contentPanel.setLayout(new BorderLayout(50, 0));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 100));
		contentPanel.add(createTableControlsPanel(toolWindow));
		contentPanel.add(mainButtonControlsPanel(toolWindow), BorderLayout.LINE_END);
		contentPanel.add(refreshButton(toolWindow), BorderLayout.SOUTH);

	}

	private void updatePanel(ToolWindow toolWindow) {
		contentPanel.removeAll();
		contentPanel.add(createTableControlsPanel(toolWindow));
		contentPanel.add(mainButtonControlsPanel(toolWindow), BorderLayout.LINE_END);
		contentPanel.add(refreshButton(toolWindow), BorderLayout.SOUTH);
		contentPanel.revalidate();
		contentPanel.repaint();

	}

	private JButton refreshButton(ToolWindow toolWindow) {
		var refreshButton = new JButton("Refresh");
		refreshButton.setIcon(AllIcons.Actions.ForceRefresh);
		refreshButton.addActionListener(e -> {
			updatePanel(toolWindow);
			runService.refresh(toolWindow);
		});
		return refreshButton;
	}

	public JBPanel getContentPanel() {
		return contentPanel;
	}

	@NotNull
	private static JBPanel mainButtonControlsPanel(ToolWindow toolWindow) {
		var jbPanel = new JBPanel();
		var buttonPanelClass = new ButtonPanel(toolWindow);
		var buttonPanel = buttonPanelClass.createButtonPanel(runService);
		jbPanel.add(buttonPanel, BorderLayout.CENTER);
		return jbPanel;
	}

	@NotNull
	private static JBScrollPane createTableControlsPanel(ToolWindow toolWindow) {
		var runTable = ProjectTable.createRunTable(toolWindow, runService);
		runTable.setSize(10, 10);
		return runTable;
	}

}