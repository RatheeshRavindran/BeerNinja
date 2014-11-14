package com.fortytwo.beerninja.model;

public class Size {
	int rowCount;
	int columnCount;
	
	public Size(int rows, int columns) {
		this.rowCount = rows;
		this.columnCount = columns;
	}
	
	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

}
