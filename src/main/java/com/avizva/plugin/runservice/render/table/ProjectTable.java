package com.avizva.plugin.runservice.render.table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

import com.avizva.plugin.runservice.services.RunService;
import com.avizva.plugin.runservice.services.RunServiceImpl;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class ProjectTable {
	private static JBTable jbTable = new JBTable();

	private ProjectTable() {
	}

	@NotNull
	public static JBScrollPane createRunTable(ToolWindow toolWindow, RunService runService) {
		var projectIntellij = toolWindow.getProject();

		var dm = new ButtonTableModel();
		var comboBox = new JBCombox(new String[]{"Integration", "Local"});

		var runButton = new ButtonRender();
		runButton.setIcon(AllIcons.Actions.StartDebugger);

		var buildButton = new ButtonRender();
		buildButton.setIcon(AllIcons.Actions.BuildLoadChanges);

		var projects = runService.getProjectsGroup(toolWindow)
								 .stream()
								 .filter(projectMetaData -> !projectMetaData.getName()
																			.contains(StringConstants.USECASES))
								 .toList();

		projects.forEach(project -> {
			if (project.getName()
					   .contains("ex")) {
				dm.addRow(new Object[]{project.getConfigName(), "", ""});
			} else {
				dm.addRow(new Object[]{project.getConfigName(), "", ""});
			}
		});

		var jTable = new JBTable(dm);

		var buildButtonEditor = new ButtonEditor(new JTextField());
		buildButtonEditor.listener(e -> {
			var row = jbTable.getSelectedRow();
			var projectName = (String) jbTable.getValueAt(row, 0);
			build(toolWindow, List.of(projectName));

		});

		var runButtonEditor = new ButtonEditor(new JTextField());
		runButtonEditor.listener(e ->

		{
			var row = jbTable.getSelectedRow();
			var projectName = (String) jbTable.getValueAt(row, 0);
			debug(toolWindow, List.of(projectName));

		});

//		jTable.getColumnModel()
//			  .getColumn(4)
//			  .setCellEditor(new DefaultCellEditor(comboBox));
//		jTable.setEditingColumn(4);
		jTable.getColumnModel()
			  .getColumn(1)
			  .setCellRenderer(runButton);
		jTable.getColumnModel()
			  .getColumn(1)
			  .setCellEditor(runButtonEditor);
		jTable.getColumnModel()
			  .getColumn(2)
			  .setCellRenderer(buildButton);
		jTable.getColumnModel()
			  .getColumn(2)
			  .setCellEditor(buildButtonEditor);

		jbTable = jTable;

		var scrollPane = new JBScrollPane(jTable);
		return scrollPane;
	}

	public static Map<String, String> env() {
		var rowCount = jbTable.getRowCount();
		var columnCount = jbTable.getColumnCount();

		var environment = IntStream.range(0, rowCount)
								   .mapToObj(i -> List.of(jbTable.getValueAt(i, 0), jbTable.getValueAt(i, columnCount - 1)))
								   .collect(Collectors.toMap(list -> (String) list.get(0), list -> (String) list.get(1)));
		return environment;
	}

	private static void build(ToolWindow toolWindow, List<String> projects) {
		var runService = new RunServiceImpl();
		runService.build(toolWindow, projects);
	}

	private static void debug(ToolWindow toolWindow, List<String> projects) {
		var runService = new RunServiceImpl();
		runService.runModule(toolWindow, projects);
	}
}