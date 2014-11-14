package com.fortytwo.beerninja.model.client;

/**
 * Interface to be implemented by the bot classes.
 * 
 * @author Ratheesh Ravindran
 *
 */
public interface BeerBot {
	/**
	 * Unique name of the Bot.
	 * Remember to create an interesting and unique name.
	 * 
	 * @return Name of the bot.
	 */
	String getName();
	
	/**
	 * Bot should return what is the move it would like to make.
	 * Possible values are defined in the enum Move.
	 * LEFT - Move to left
	 * RIGHT - Move to right
	 * UP - Move to up
	 * DOWN - Move to down
	 * PASS - Pass
	 * Bot must return the move with in 3 seconds.
	 *  
	 * @return move.
	 */
	Move makeMove();
	
	/**
	 * Gives the bot a reference to the game board. 
	 * This can be used to read the different game parameters. 
	 * 
	 * @param gameBoard
	 */
	void setGameBoard(GameBoard gameBoard);
}
