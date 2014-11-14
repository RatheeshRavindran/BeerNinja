package com.fortytwo.beerninja.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fortytwo.beerninja.model.MoveStatus;
import com.fortytwo.beerninja.model.Size;
import com.fortytwo.beerninja.model.client.InvalidArgumentException;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Position;

public interface GameEngine {
	
	public void setBots(String botOne, String botTwo) throws InvalidArgumentException; 
	
	public Size getBoardSize();

	public Set<String> getBotNames();

	public Position getBotPosition(String botName) throws InvalidArgumentException;

	public Map<ItemType, Integer> getTotalItemCount();
	
	public int getAvailableItemCount(ItemType type) throws InvalidArgumentException;

	public int getItemCount(String botName, ItemType type) throws InvalidArgumentException;
	
	public MoveStatus moveLeft(String botName) throws InvalidArgumentException;
	
	public MoveStatus moveRight(String botName) throws InvalidArgumentException;

	public MoveStatus moveUp(String botName) throws InvalidArgumentException;

	public MoveStatus moveDown(String botName) throws InvalidArgumentException;

	public Map<ItemType, List<Position>> getItemPositions();
	
	public MoveStatus pick(String botName) throws InvalidArgumentException;

	public Map<ItemType, Integer> getItemCount(String botName)
			throws InvalidArgumentException;
	
	public ItemType getItemAtPosition(Position position);
	
}
