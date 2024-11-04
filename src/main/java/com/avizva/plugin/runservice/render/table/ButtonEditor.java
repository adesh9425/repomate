package com.avizva.plugin.runservice.render.table;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ButtonEditor extends DefaultCellEditor {

	private final JButton button = new JButton();

	public ButtonEditor(JTextField textField) {
		super(textField);
		button.setOpaque(true);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		button.setText((value == null) ? "" : value.toString());
		return button;
	}

	public void listener(ActionListener e) {
		button.addActionListener(e);
	}

}