package com.fortytwo.beerninja.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.fortytwo.beerninja.model.GameCell;
import com.fortytwo.beerninja.model.MoveStatus;
import com.fortytwo.beerninja.model.Size;
import com.fortytwo.beerninja.model.client.InvalidArgumentException;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Position;
/**
 * This is the main engine class of the game.
 * The information about the different game cells is contained here.
 * 
 * @author Ratheesh.Ravindran
 *
 */
public class BeerBotEngine implements GameEngine {
	private Size size;
	private GameCell[][] gameCells = null;
	private Map<ItemType, Integer> itemTypes;
	private Map<ItemType, Integer> currentItemTypes;
	private Map<String, Position> botPositions;
	private Map<String, Map<ItemType, Integer>> botItemCounts;
	
	public BeerBotEngine(Size size) {
		this.size = size;
		gameCells = new GameCell[size.getRowCount()][size.getColumnCount()];
		itemTypes = new HashMap<ItemType, Integer>();
		currentItemTypes = new HashMap<ItemType, Integer>();
		botPositions = new HashMap<String, Position>(2);
		botItemCounts = new HashMap<String, Map<ItemType,Integer>>();
		initialize();
	}
	
	@Override
	public void setBots(String botOne, String botTwo) throws InvalidArgumentException {
		if(botOne.trim().toLowerCase().equalsIgnoreCase(botTwo.trim().toLowerCase())) {
			throw new InvalidArgumentException("Bot names must be unique.");
		}
		initializeBotPositions(botOne, botTwo);
		initializeBotCounts(botOne, botTwo);
	}

	@Override
	public Size getBoardSize() {
		return size;
	}


	public static void main(String[] args) {
		new BeerBotEngine(new Size(8, 8));
	}

	@Override
	public Set<String> getBotNames() {
		return Collections.unmodifiableSet(botPositions.keySet());
	}

	@Override
	public Position getBotPosition(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		return botPositions.get(botName);
	}

	@Override
	public Map<ItemType, Integer> getTotalItemCount() {
		return Collections.unmodifiableMap(itemTypes);
	}

	@Override
	public int getAvailableItemCount(ItemType type) throws InvalidArgumentException  {
		if(!currentItemTypes.containsKey(type)) {
			throw new InvalidArgumentException("Invalid Type.");
		}
		return currentItemTypes.get(type);
	}

	@Override
	public int getItemCount(String botName, ItemType type) throws InvalidArgumentException {
		if(!botItemCounts.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Map<ItemType, Integer> itemCount = botItemCounts.get(botName);
		if(!itemCount.containsKey(type)) {
			throw new InvalidArgumentException("Invalid Type.");
		}
		return itemCount.get(type);
	}
	
	@Override
	public Map<ItemType, Integer> getItemCount(String botName) throws InvalidArgumentException {
		if(!botItemCounts.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		return Collections.unmodifiableMap(botItemCounts.get(botName));
	}

	public MoveStatus moveLeft(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Position currentPosition = botPositions.get(botName);
		if(currentPosition.getColumnPosition() == 0) {
			return MoveStatus.NOT_OK;
		}
		Position newPosition = new Position(currentPosition.getRowPosition(), currentPosition.getColumnPosition()-1);
		botPositions.put(botName, newPosition);
		return MoveStatus.OK;
	}
	
	public MoveStatus moveRight(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Position currentPosition = botPositions.get(botName);
		if(currentPosition.getColumnPosition() == size.getColumnCount()-1) {
			return MoveStatus.NOT_OK;
		}
		Position newPosition = new Position(currentPosition.getRowPosition(), currentPosition.getColumnPosition()+1);
		botPositions.put(botName, newPosition);
		return MoveStatus.OK;
	}

	public MoveStatus moveUp(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Position currentPosition = botPositions.get(botName);
		if(currentPosition.getRowPosition() == 0) {
			return MoveStatus.NOT_OK;
		}
		Position newPosition = new Position(currentPosition.getRowPosition()-1, currentPosition.getColumnPosition());
		botPositions.put(botName, newPosition);
		return MoveStatus.OK;
	}

	public MoveStatus moveDown(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Position currentPosition = botPositions.get(botName);
		if(currentPosition.getRowPosition() == size.getRowCount()-1) {
			return MoveStatus.NOT_OK;
		}
		Position newPosition = new Position(currentPosition.getRowPosition()+1, currentPosition.getColumnPosition());
		botPositions.put(botName, newPosition);
		return MoveStatus.OK;
	}

	@Override
	public MoveStatus pick(String botName) throws InvalidArgumentException {
		if(!botPositions.containsKey(botName)) {
			throw new InvalidArgumentException("Invalid Bot name.");
		}
		Position currentPosition = botPositions.get(botName);
		ItemType type = gameCells[currentPosition.getRowPosition()][currentPosition.getColumnPosition()].getItemType(); 
		if(type == ItemType.Invalid) {
			return MoveStatus.NOT_OK;
		}
		gameCells[currentPosition.getRowPosition()][currentPosition.getColumnPosition()].setItemType(ItemType.Invalid);
		Map<ItemType, Integer> itemCounts = botItemCounts.get(botName);
		itemCounts.put(type, itemCounts.get(type)+1);
		botItemCounts.put(botName, itemCounts);
		return MoveStatus.OK;
	}

	public Map<ItemType, List<Position>> getItemPositions() {
		Map<ItemType, List<Position>> positions = new HashMap<ItemType, List<Position>>(); 
		for(int row = 0; row < size.getRowCount(); row++) {
			for(int column = 0; column < size.getColumnCount(); column++){
				if(gameCells[row][column].getItemType() != ItemType.Invalid) {
					if(!positions.containsKey(gameCells[row][column].getItemType())) {
						List<Position> itemPositions = new ArrayList<Position>();
						positions.put(gameCells[row][column].getItemType(), itemPositions);
					}
					positions.get(gameCells[row][column].getItemType()).add(new Position(row, column));
				}
			}
		}
		return positions;
	}
	
	private void initializeBotPositions(String botOne, String botTwo) {
		int row = 0;
		int col = 0;
		Random rand = new Random();
		do {
			row = rand.nextInt(size.getRowCount()-1);
			col = rand.nextInt(size.getColumnCount()-1);
		}while (gameCells[row][col].getItemType() != ItemType.Invalid);
		botPositions.put(botOne, new Position(row, col));
		botPositions.put(botTwo, new Position(row, col));
	}

	private void initializeBotCounts(String botOne, String botTwo) {
		Map<ItemType,Integer> itemCountBotOne = new HashMap<ItemType, Integer>();
		Map<ItemType,Integer> itemCountBotTwo = new HashMap<ItemType, Integer>();
		initItemBotCount(itemCountBotOne);
		initItemBotCount(itemCountBotTwo);
		botItemCounts.put(botOne, itemCountBotOne);
		botItemCounts.put(botTwo, itemCountBotTwo);
	}

	private void initItemBotCount(Map<ItemType, Integer> itemCountBotOne) {
		Set<ItemType> types = itemTypes.keySet();
		for (ItemType type: types) {
			itemCountBotOne.put(type, 0);
		}
	}
	
	private void initialize() {
		initGameCells();
//		itemTypes.put(ItemType.Type1, (MIN_ITEMS + 2*(int)(Math.random()*((MAX_ITEMS-MIN_ITEMS)/2+1))));
//		itemTypes.put(ItemType.Type2, (MIN_ITEMS + 2*(int)(Math.random()*((MAX_ITEMS-MIN_ITEMS)/2+1))));
//		itemTypes.put(ItemType.Type3, (MIN_ITEMS + 2*(int)(Math.random()*((MAX_ITEMS-MIN_ITEMS)/2+1))));
		itemTypes.put(ItemType.Karhu, 1);
		itemTypes.put(ItemType.Karjala, 3);
		itemTypes.put(ItemType.Koff, 5);
		
		currentItemTypes.put(ItemType.Karhu, itemTypes.get(ItemType.Karhu));
		currentItemTypes.put(ItemType.Karjala, itemTypes.get(ItemType.Karjala));
		currentItemTypes.put(ItemType.Koff, itemTypes.get(ItemType.Koff));

		populateType(ItemType.Karhu, itemTypes.get(ItemType.Karhu));
		populateType(ItemType.Karjala, itemTypes.get(ItemType.Karjala));
		populateType(ItemType.Koff, itemTypes.get(ItemType.Koff));
	}
	
	private void initGameCells() {
		for(int row = 0; row < size.getRowCount(); row++) {
			for(int column = 0; column < size.getColumnCount(); column++){
				gameCells[row][column] = new GameCell();
			}
		}
	}

	private void populateType(ItemType type, Integer numberOfItems) {
		int row = 0;
		int col = 0;
		Random rand = new Random();
		for (int count=0; count < numberOfItems; count++) {
			do {
				row = rand.nextInt(size.getRowCount()-1);
				col = rand.nextInt(size.getColumnCount()-1);
			}while (gameCells[row][col].getItemType() != ItemType.Invalid);
			gameCells[row][col].setItemType(type);
		}
	}

	@Override
	public ItemType getItemAtPosition(Position position) {
		int row = position.getRowPosition();
		int col = position.getColumnPosition();
		if(row >= size.getRowCount() || row < 0 ||
				col >= size.getColumnCount() || col < 0) {
			return ItemType.Invalid;
		}
		return gameCells[row][col].getItemType();
	}

}
