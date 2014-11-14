package com.fortytwo.beerninja.model.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fortytwo.beerninja.model.Size;

public interface GameBoard {
	/**
	 * Get the size of the game board.
	 * 
	 * If the size is 6Rows and 5 columns then top, left cell is having index (0,0)
	 * and the bottom right cell is having the index (5,4)
	 *  
	 * @return size of the game board.
	 */
	Size getBoardSize();
	
	/**
	 * Returns the names of the current bots playing.
	 * Using this api, name of the opponent bot can be found.
	 * @return Name of the two bots playing.
	 */
	Set<String> getBotNames();
	
	/**
	 * Returns the position of the bot identified by the name.
	 * Pass name of the opponent bot to find it's position.
	 * Remember, Position contains zero based index!
	 * 
	 * @param botName 
	 * @return position
	 * @throws InvalidArgumentException 
	 */
	Position getBotPosition(String botName) throws InvalidArgumentException; 
	
	/**
	 * Returns the total number of different item types in the game.
	 * This includes the items in the game board and those in players inventory.
	 * 
	 * @return
	 */
	Map<ItemType, Integer> getTotalItemCount();
	
	/**
	 * Get the number of items for a given type currently available in the game board.
	 * 
	 * @param type
	 * @return
	 * @throws InvalidArgumentException 
	 */
	int getAvailableItemCount(ItemType type) throws InvalidArgumentException;
	
	//	int getMyItemCount();
	
	/**
	 * Returns the number of items of the given type collected by the bot.
	 * @param botName Pass the name of the bot for which you want to read the item count.
	 * @param type Item type 
	 * @throws InvalidArgumentException 
	 */
	int getItemCount(String botName, ItemType type) throws InvalidArgumentException;

	/**
	 * Returns the current position of each of the items.
	 * @return
	 */
	Map<ItemType, List<Position>> getItemPositions();
	
	/**
	 * Get the item present at the given location.
	 * @param position
	 * @return Invalid if no item is available at the position otherwise the item type.
	 */
	ItemType getItemAtPosition(Position position);

}
