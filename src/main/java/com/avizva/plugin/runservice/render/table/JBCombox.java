package com.avizva.plugin.runservice.render.table;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import com.intellij.openapi.ui.ComboBox;

public class JBCombox extends ComboBox implements TableCellRenderer {
	public JBCombox(String[] items) {
		super(items);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setSelectedItem((value != null) ? value.toString() : "");
		return this;
	}



}