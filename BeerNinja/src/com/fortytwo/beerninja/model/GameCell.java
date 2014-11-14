package com.fortytwo.beerninja.model;

import com.fortytwo.beerninja.model.client.ItemType;

/**
 * Represents each cell of the game.
 * @author Ratheesh Ravindran
 *
 */
public class GameCell {
	private ItemType itemType = ItemType.Invalid;

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	
}
