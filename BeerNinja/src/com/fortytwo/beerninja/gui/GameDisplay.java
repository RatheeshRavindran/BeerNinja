package com.fortytwo.beerninja.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.fortytwo.beerninja.engine.GUINotifictaion;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Move;
import com.fortytwo.beerninja.model.client.Position;

/**
 * Class for holding the main GUI elements of the game.
 * @author Ratheesh Ravindran
 *
 */
public class GameDisplay {
	public static final int ROWS = 6;
	public static final int COLUMNS = 6;
	private static final Color DARK_COLOR = new Color(0, 100, 0);
	private static final Color LIGHT_COLOR = new Color(200, 200, 200);
	private static final int SQR_WIDTH = 80;
	public static final int PIECE_WIDTH = 60;
	private static final Dimension SQR_SIZE = new Dimension(SQR_WIDTH,
			SQR_WIDTH);

	private JLayeredPane mainLayeredPane = new JLayeredPane();
	private JPanel board = new JPanel(new GridLayout(ROWS, COLUMNS));
	private GameCellGUI[][] jPanelSquareGrid = new GameCellGUI[ROWS][COLUMNS];

	private JLabel piece;
	private GameCellGUI targetcell;
	private Map<ItemType, String> typeImages;
	private DefaultTableModel scoreTable;
	private Map<ItemType, Integer> columnPositions;
	private String botOneName;
	private String botTwoName;

	public GameDisplay(DefaultTableModel scoreTable, Map<ItemType, Integer> columnPositions) {
		this.scoreTable = scoreTable;
		this.columnPositions = columnPositions;
		for (int rank = 0; rank < ROWS; rank++) {
			for (int file = 0; file < COLUMNS; file++) {
				Color bkgd = DARK_COLOR;
				if (rank % 2 == file % 2) {
					bkgd = LIGHT_COLOR;
				}
				jPanelSquareGrid[rank][file] = new GameCellGUI(rank, file, bkgd);
				jPanelSquareGrid[rank][file].setPreferredSize(SQR_SIZE);
				board.add(jPanelSquareGrid[rank][file]);
			}
		}
		board.setSize(board.getPreferredSize());
		board.setLocation(0, 0);
		mainLayeredPane.add(board, JLayeredPane.DEFAULT_LAYER);
		mainLayeredPane.setPreferredSize(board.getPreferredSize());

		typeImages = new HashMap<ItemType, String>();

		typeImages.put(ItemType.Karhu, "karhu.png");
		typeImages.put(ItemType.Karjala, "karjala.png");
		typeImages.put(ItemType.Koff, "koff.png");
	}

	public void reset() {
		for (int rank = 0; rank < ROWS; rank++) {
			for (int file = 0; file < COLUMNS; file++) {
				jPanelSquareGrid[rank][file].reset();
			}
		}
	}
	
	public JComponent getMainComponent() {
		return mainLayeredPane;
	}

	synchronized public void setBotPosition(String botName, Position botPosition) {
		String imageName;
		if(botTwoName.equals(botName)) {
			System.out.println("green for " + botName);
			imageName = "ninja_green.png";
		} else {
			imageName = "ninja_red.png";
			System.out.println("red for " + botName);
		}
		ImageIcon icon = new ImageIcon(IconImageUtility.createImage(this
				.getClass().getResourceAsStream(imageName), PIECE_WIDTH));
		JLabel botOne = new JLabel(icon, SwingConstants.CENTER);
		botOne.setName(botName);
		jPanelSquareGrid[botPosition.getRowPosition()][botPosition
				.getColumnPosition()].add(botOne);
	}

	synchronized public void moveBot(final String botName, Position oldPosition,
			Position newPosition, final Move direction,final GUINotifictaion listener) {
		int rank = oldPosition.getRowPosition();
		int file = oldPosition.getColumnPosition();
		GameCellGUI currentCell = jPanelSquareGrid[rank][file];
		if (currentCell.getChessPiece(botName) != null) {
			piece = currentCell.getChessPiece(botName);
			Point diffLocation = piece.getLocation();
			currentCell.remove(piece);
			mainLayeredPane.add(piece, JLayeredPane.DRAG_LAYER);
			Point pieceLocation = new Point(currentCell.getLocation().x
					+ diffLocation.x, currentCell.getLocation().y
					+ diffLocation.y);
			piece.setLocation(pieceLocation);
			//refresh();
			mainLayeredPane.revalidate();
			mainLayeredPane.repaint();
			
			targetcell = jPanelSquareGrid[newPosition.getRowPosition()][newPosition
					.getColumnPosition()];
			Point targetPosition = new Point(targetcell.getLocation().x
					+ (SQR_WIDTH-PIECE_WIDTH)/2, targetcell.getLocation().y
					+ (SQR_WIDTH-PIECE_WIDTH)/2);

			refresh();

			final BotAnimation task = new BotAnimation(pieceLocation, targetPosition, direction, piece, botName, jPanelSquareGrid[newPosition.getRowPosition()][newPosition
			                                                                                                                                					.getColumnPosition()]);
			task.addPropertyChangeListener(new PropertyChangeListener() {
			      public void propertyChange(PropertyChangeEvent evt) {
			        if ("progress".equals(evt.getPropertyName())) {
			        	listener.animationCompleted(botName, direction);
			        }
			      }
			    });
			task.execute();
			return;
		}
	}

	public void pick(String botName, Position oldPosition,GUINotifictaion listener, Map<ItemType, Integer> map) {
		int rank = oldPosition.getRowPosition();
		int file = oldPosition.getColumnPosition();
		GameCellGUI currentCell = jPanelSquareGrid[rank][file];
		JLabel component = currentCell.getItem();
		if (component != null) {
			currentCell.remove(component);
		}
		int scoreRow = 0;
		if(botName.equals(scoreTable.getValueAt(0, 0))) {
			scoreRow = 0;
		} else {
			scoreRow = 1;
		}
		for (ItemType type: map.keySet()) {
			scoreTable.setValueAt(map.get(type), scoreRow, columnPositions.get(type));
		}
		refresh();
		listener.animationCompleted(botName, Move.PICK);
	}

	public void pass(String botName, Position oldPosition,GUINotifictaion listener) {
		//
		// Do nothing.
		refresh();
		listener.animationCompleted(botName, Move.PASS);
	}

	public void refresh() {
		board.revalidate();
		mainLayeredPane.repaint();
	}

	public void setItemPositions(Map<ItemType, List<Position>> positions) {
		for (ItemType type : positions.keySet()) {
			for (Position position : positions.get(type)) {
				ImageIcon icon = new ImageIcon(IconImageUtility.createImage(
						this.getClass().getResourceAsStream(
								typeImages.get(type)), PIECE_WIDTH));
				JLabel item = new JLabel(icon, SwingConstants.CENTER);
				item.setName(type.toString());
				jPanelSquareGrid[position.getRowPosition()][position
						.getColumnPosition()].addItem(item);
			}
		}
	}

	
	private class BotAnimation extends SwingWorker<Integer, Integer> {
		private int currentX;
		private int destinationX;
		private int currentY;
		private int destinationY;
		private Move direction;
		private JLabel piece;
		private String botName;
		private GameCellGUI target;
		
		public BotAnimation(Point source, Point destination, Move direction,
				JLabel piece, String botName, GameCellGUI target) {
			currentX = source.x;
			currentY = source.y;
			destinationX = destination.x;
			destinationY = destination.y;
			this.direction = direction;
			this.piece = piece;
			this.botName = botName;
			this.target = target;
		}

		@Override
		protected Integer doInBackground() throws Exception {
			// on each update:
			double amountToMoveX = 0;
			double amountToMoveY = 0;

			if (direction == Move.RIGHT) {
				amountToMoveX = 5;
			} else if (direction == Move.LEFT) {
				amountToMoveX = -5;
			} else if (direction == Move.UP) {
				amountToMoveY = -5;
			} else if (direction == Move.DOWN) {
				amountToMoveY = 5;
			}
			
			while (true) {
				currentX += amountToMoveX;
				currentY += amountToMoveY;
				piece.setLocation(currentX, currentY);
				refresh();
				if (reachedDesitination()) {
					mainLayeredPane.remove(piece);
					//targetcell.add(piece);
					target.add(piece);
					refresh();
					//listener.animationCompleted(botName);
					break;
				} 
				Thread.sleep(20);
			}
			setProgress(100);
			return 42;
		}
		
		private boolean reachedDesitination() {
			boolean reached = true;
			if (direction == Move.RIGHT && currentX < destinationX) {
				reached = false;
			} else if (direction == Move.LEFT && currentX > destinationX) {
				reached = false;
			} else if (direction == Move.UP && currentY > destinationY) {
				reached = false;
			} else if (direction == Move.DOWN && currentY < destinationY) {
				reached = false;
			}
			return reached;
		}
		
//		  protected void process(List<Integer> messages) {
//		      System.out.println(messages);
//		  }
//		  @Override
//		  protected void done() {
//		  }

	}

	public void setBotNames(String botOneName, String botTwoName) {
		this.botOneName = botOneName;
		this.botTwoName = botTwoName;
	}

}

@SuppressWarnings("serial")
class GameCellGUI extends JPanel {
	private int rank;
	private int file;
	Map<String, JLabel> bots = null;
	JLabel item = null;

	public GameCellGUI(int rank, int file, Color bkgrnd) {
		this.rank = rank;
		this.file = file;
		bots = new HashMap<String, JLabel>(2);
		setBackground(bkgrnd);
		setLayout(new GridBagLayout());
	}

	public int getRank() {
		return rank;
	}

	public int getFile() {
		return file;
	}

	@Override
	public Component add(Component c) {
		bots.put(c.getName(), (JLabel) c);
		return super.add(c);
	}

	public void addItem(Component c) {
		//
		// There will be only one item in a cell
		item = (JLabel) c;
		add(item);
	}

	@Override
	public void remove(Component comp) {
		if (bots.containsKey(comp.getName())) {
			bots.remove(comp.getName());
		}
		super.remove(comp);
	}

	public JLabel getChessPiece(String botName) {
		return bots.get(botName);
	}

	public JLabel getItem() {
		return item;
	}
	
	public void reset() {
		if(bots != null) {
			for (JLabel component : bots.values()) {
				super.remove(component);
			}
		}
		bots.clear();
		if(item != null) {
			super.remove(item);
		}
	}
}

class IconImageUtility {

	public static BufferedImage createImage(InputStream is, int size) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Created");
		return img;
	}

}
