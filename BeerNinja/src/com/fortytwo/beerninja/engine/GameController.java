package com.fortytwo.beerninja.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

import com.fortytwo.beerninja.gui.BeerBoatGUI;
import com.fortytwo.beerninja.gui.GameDisplay;
import com.fortytwo.beerninja.model.GameNotificationListener;
import com.fortytwo.beerninja.model.MoveStatus;
import com.fortytwo.beerninja.model.Size;
import com.fortytwo.beerninja.model.client.BeerBot;
import com.fortytwo.beerninja.model.client.GameBoard;
import com.fortytwo.beerninja.model.client.InvalidArgumentException;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Move;
import com.fortytwo.beerninja.model.client.Position;

/**
 * Controller class of the game. Main responsibility is getting the moves from
 * the playing bots and requesting the game engine.
 * 
 * @author Ratheesh Ravindran
 * 
 */
public class GameController implements GameBoard, GUINotifictaion {
	private GameEngine engine;
	private GameDisplay gameDisplay;
	private Map<String, BeerBot> bots;
	private String botOneName;
	private String botTwoName;
	private boolean gameOver;
	private GameNotificationListener listener;
	private Timer timer;

	public GameController(GameEngine engine, BeerBot botOne, BeerBot botTwo,
			GameDisplay gameDisplay, GameNotificationListener listener)
			throws InvalidArgumentException {
		this.engine = engine;
		gameOver = false;
		this.listener = listener;
		botOne.setGameBoard(this);
		botTwo.setGameBoard(this);
		bots = new HashMap<String, BeerBot>();
		botOneName = botOne.getName();
		botTwoName = botTwo.getName();
		bots.put(botOne.getName(), botOne);
		bots.put(botTwo.getName(), botTwo);
		this.engine.setBots(botOne.getName(), botTwo.getName());
		this.gameDisplay = gameDisplay;
		Set<String> botNames = this.engine.getBotNames();
		for (String botName : botNames) {
			this.gameDisplay.setBotPosition(botName,
					this.engine.getBotPosition(botName));
		}
		Map<ItemType, List<Position>> positions = this.engine
				.getItemPositions();
		this.gameDisplay.setItemPositions(positions);
	}

	public void start() {

		//
		// Call the move methods in the bots.
		for (String botName : bots.keySet()) {
			BeerBot botOne = bots.get(botName);
			Move move = null;
			try {
				move = botOne.makeMove();
			} catch (Exception e) {
				gameOver = true;
				listener.gameError("Error starting bots!");
			}
			try {
				if (move == Move.LEFT) {
					moveLeft(botOne.getName());
				} else if (move == Move.RIGHT) {
					moveRight(botOne.getName());
				} else if (move == Move.UP) {
					moveUp(botOne.getName());
				} else if (move == Move.DOWN) {
					moveDown(botOne.getName());
				} else if (move == Move.PICK) {
					pick(botOne.getName());
				} else if (move == Move.PASS) {
					pass(botOne.getName());
				}
			} catch (Exception e) {

			}
			System.out.println(botName + " : " + move.toString());
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				gameOver = true;
				listener.gameTimeout();
			}
		}, 3 * 60 * 1000);// 10*1000
	}

	public void stop() {

	}

	public void moveRight(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		if (engine.moveRight(botName) == MoveStatus.OK) {
			Position newPosition = engine.getBotPosition(botName);
			gameDisplay.moveBot(botName, oldPosition, newPosition, Move.RIGHT,
					this);
		} else {
			System.out.println("Invalid move!");
			makeMove(botName);
		}
	}

	public void moveLeft(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		if (engine.moveLeft(botName) == MoveStatus.OK) {
			Position newPosition = engine.getBotPosition(botName);
			gameDisplay.moveBot(botName, oldPosition, newPosition, Move.LEFT,
					this);
		} else {
			System.out.println("Invalid move!");
			makeMove(botName);
		}
	}

	public void moveUp(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		if (engine.moveUp(botName) == MoveStatus.OK) {
			Position newPosition = engine.getBotPosition(botName);
			gameDisplay.moveBot(botName, oldPosition, newPosition, Move.UP,
					this);
		} else {
			System.out.println("Invalid move!");
			makeMove(botName);
		}
	}

	public void moveDown(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		if (engine.moveDown(botName) == MoveStatus.OK) {
			Position newPosition = engine.getBotPosition(botName);
			gameDisplay.moveBot(botName, oldPosition, newPosition, Move.DOWN,
					this);
		} else {
			System.out.println("Invalid move!");
			makeMove(botName);
		}
	}

	public void pick(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		if (engine.pick(botName) == MoveStatus.OK) {
			gameDisplay.pick(botName, oldPosition, this,
					engine.getItemCount(botName));
		} else {
			System.out.println("Invalid move!");
			makeMove(botName);
		}
	}

	public void pass(String botName) throws InvalidArgumentException {
		Position oldPosition = engine.getBotPosition(botName);
		gameDisplay.pass(botName, oldPosition, this);
	}

	@Override
	public Size getBoardSize() {
		return engine.getBoardSize();
	}

	@Override
	public Set<String> getBotNames() {
		return engine.getBotNames();
	}

	@Override
	public Position getBotPosition(String botName)
			throws InvalidArgumentException {
		return engine.getBotPosition(botName);
	}

	@Override
	public Map<ItemType, Integer> getTotalItemCount() {
		return engine.getTotalItemCount();
	}

	@Override
	public int getAvailableItemCount(ItemType type)
			throws InvalidArgumentException {
		return engine.getAvailableItemCount(type);
	}

	@Override
	public int getItemCount(String botName, ItemType type)
			throws InvalidArgumentException {
		return engine.getItemCount(botName, type);
	}

	@Override
	public Map<ItemType, List<Position>> getItemPositions() {
		return engine.getItemPositions();
	}

	@Override
	public void animationCompleted(final String botName, Move direction) {
		try {
			String winner = null;
			if (direction == Move.PICK && ((winner = gameOver()) != null)) {
				timer.cancel();
				gameOver = true;
				listener.gameFinished(winner, "");
			} else {
				//makeMove(botName);
				Thread t= new Thread (new MoveThread(botName));
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String gameOver() throws InvalidArgumentException {
		Map<ItemType, Integer> itemCountOne = engine.getItemCount(botOneName);
		Map<ItemType, Integer> itemCountTwo = engine.getItemCount(botTwoName);
		Map<ItemType, Integer> totalItemCount = engine.getTotalItemCount();
		int botOne = 0;
		int botTwo = 0;
		for (ItemType type : totalItemCount.keySet()) {
			if (itemCountOne.get(type) > totalItemCount.get(type) / 2) {
				botOne++;
			} else if (itemCountTwo.get(type) > totalItemCount.get(type) / 2) {
				botTwo++;
			}
		}
		String botName = null;
		if (botTwo > totalItemCount.keySet().size() / 2) {
			botName = botTwoName;
		} else if (botOne > totalItemCount.keySet().size() / 2) {
			botName = botOneName;
		}
		return botName;
	}

	private void makeMove(String botName) throws InvalidArgumentException {
		if (gameOver) {
			return;
		}
		try {
			final BeerBot botOne = bots.get(botName);
			Move move = botOne.makeMove();
//			ExecutorService service = Executors.newSingleThreadExecutor();
//			Future<Move> future = service.submit(new Callable<Move>() {
//				@Override
//				public Move call() throws Exception {
//					return botOne.makeMove();
//				}
//			});
//			Move move = null;
//			move = future.get(3, TimeUnit.SECONDS);
			System.out.println("Listener " + botName + " : " + move.toString());
			if (move == Move.LEFT) {
				moveLeft(botOne.getName());
			} else if (move == Move.RIGHT) {
				moveRight(botOne.getName());
			} else if (move == Move.UP) {
				moveUp(botOne.getName());
			} else if (move == Move.DOWN) {
				moveDown(botOne.getName());
			} else if (move == Move.PICK) {
				pick(botOne.getName());
			} else if (move == Move.PASS) {
				pass(botOne.getName());
			}
		} catch (Exception e) {
			gameOver = true;
			timer.cancel();
			String winner = botOneName;
			if (botOneName.equals(botName)) {
				winner = botTwoName;
			}
			listener.gameFinished(winner, botName + " caused exception.");
		}
	}

	@Override
	public ItemType getItemAtPosition(Position position) {
		return engine.getItemAtPosition(position);
	}

	private final class MoveThread implements Runnable {

		private String botName;

		public MoveThread(String botName) {
			this.botName = botName;
		}
		
		@Override
		public void run() {
			try {
				makeMove(botName);
			} catch (InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
