package com.fortytwo.beerninja.model.client;

/**
 * Class representing the position in game board.
 * note: This is zero based index.
 * 
 * @author Ratheesh Ravindran
 *
 */
public class Position {
	private int rowPosition;
	private int columnPosition;
	
	public Position(int rowPosition, int columnPosition) {
		this.rowPosition = rowPosition;
		this.columnPosition = columnPosition;
	}
	
	public int getRowPosition() {
		return rowPosition;
	}

	public int getColumnPosition() {
		return columnPosition;
	}

	
}
