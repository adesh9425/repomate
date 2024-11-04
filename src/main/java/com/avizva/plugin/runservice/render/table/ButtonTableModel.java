package com.avizva.plugin.runservice.render.table;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ButtonTableModel extends DefaultTableModel {
	private final String[] column = {"Name", "Debug", "Build"};

	@Override
	public String getColumnName(int col) {
		return column[col];
	}

	@Override
	public int getColumnCount() {
		return column.length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if ((columnIndex == 1) || (columnIndex == 2)) {
			return ButtonRender.class;
		}

		return String.class;
	}

	@Override
	public void addRow(Vector<?> rowData) {
		super.addRow(rowData);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
	}

}