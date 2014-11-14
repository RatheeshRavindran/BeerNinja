package com.sample.bots;

import java.util.Random;

import com.fortytwo.beerninja.model.client.BeerBot;
import com.fortytwo.beerninja.model.client.GameBoard;
import com.fortytwo.beerninja.model.client.Move;

public class NotThatGreat implements BeerBot{
	private GameBoard gameBoard;
	@Override
	public String getName() {
		return "Not That Great";
	}

	@Override
	public Move makeMove() {
		Random rand = new Random();
		int move =  rand.nextInt(100)%5;
		if(move == 0) {
			return Move.LEFT;
		} else if(move == 1) {
			return Move.RIGHT;
		} else if(move == 2) {
			return Move.UP;
		} else if(move == 3) {
			return Move.DOWN;
		} 
		return Move.PICK;
	}

	@Override
	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard; 
	}

}
