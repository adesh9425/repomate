package com.avizva.plugin.runservice.render.table;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.CellEditorListener;

import com.intellij.ui.components.editors.JBComboBoxTableCellEditorComponent;

public class TCellEditor extends DefaultCellEditor {
	private final JBComboBoxTableCellEditorComponent comboBoxEditor;

	public TCellEditor() {
		super(new JComboBox<>());
		this.comboBoxEditor = new JBComboBoxTableCellEditorComponent();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		comboBoxEditor.setRow(row);
		comboBoxEditor.setColumn(column);
		comboBoxEditor.setOptions("Integration", "Test");
		return comboBoxEditor;
	}

	@Override
	public Object getCellEditorValue() {
		return comboBoxEditor.getEditorValue();
	}

	@Override
	public boolean stopCellEditing() {
		return false;
	}

	@Override
	public void cancelCellEditing() {

	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {

	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {

	}
}