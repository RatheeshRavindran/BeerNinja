package com.sample.bots;

import java.util.Random;

import com.fortytwo.beerninja.model.client.BeerBot;
import com.fortytwo.beerninja.model.client.GameBoard;
import com.fortytwo.beerninja.model.client.InvalidArgumentException;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Move;
import com.fortytwo.beerninja.model.client.Position;

public class RandomBot implements BeerBot{
	private GameBoard gameBoard;
	@Override
	public String getName() {
		return "Random Bot";
	}

	@Override
	public Move makeMove() {
		try {
			Position botPosition = gameBoard.getBotPosition(getName());
			if(gameBoard.getItemAtPosition(botPosition) != ItemType.Invalid) {
				return Move.PICK;
			}
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}
		Random rand = new Random();
		int move = rand.nextInt(100)%5;
		if(move == 0) {
			return Move.LEFT;
		} else if(move == 1) {
			return Move.RIGHT;
		} else if(move == 2) {
			return Move.UP;
		} else if(move == 3) {
			return Move.DOWN;
		} 
		return Move.PASS;
	}

	@Override
	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard; 
	}

}
