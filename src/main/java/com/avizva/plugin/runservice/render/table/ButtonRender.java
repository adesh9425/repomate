package com.avizva.plugin.runservice.render.table;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class ButtonRender extends JButton implements TableCellRenderer {
	public void ButtonRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		setText((value == null) ? "" : value.toString());
		return this;
	}

	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);

	}

}