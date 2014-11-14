package com.fortytwo.beerninja.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;
import javax.swing.Box.Filler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.fortytwo.beerninja.engine.BeerBotEngine;
import com.fortytwo.beerninja.engine.GameController;
import com.fortytwo.beerninja.engine.GameEngine;
import com.fortytwo.beerninja.model.GameNotificationListener;
import com.fortytwo.beerninja.model.Size;
import com.fortytwo.beerninja.model.client.BeerBot;
import com.fortytwo.beerninja.model.client.GameBoard;
import com.fortytwo.beerninja.model.client.InvalidArgumentException;
import com.fortytwo.beerninja.model.client.ItemType;
import com.fortytwo.beerninja.model.client.Position;

/**
 * The main GUI class
 * 
 */
public class BeerBoatGUI extends JFrame implements GameNotificationListener {
	// Variables declaration
	private JPanel contentPane;
	// -----
	private JButton start;
	private JButton left;
	private JPanel controlsPanel;
	// -----
	private JPanel gamePanel;
	// -----
	// End of variables declaration
	private GameEngine gameEngine;
	private GameController gameController;
	private Size size;
	private GameDisplay gameDisplay;
	private JComboBox botOneName;
	private JComboBox botTwoName;
	private DefaultTableModel scoreTable;
	private java.util.List<BeerBot> beerBots;
	private BeerBot botOne;
	private BeerBot botTwo;
	private Map<ItemType, Integer> columnPositions;

	public BeerBoatGUI() {
		super();
		try {
			String[] columnNames = { " ", "Karhu", "Karjala", "Koff" };
			columnPositions = new ConcurrentHashMap<ItemType, Integer>();//new HashMap<ItemType, Integer>();
			columnPositions.put(ItemType.Karhu, 1);
			columnPositions.put(ItemType.Karjala, 2);
			columnPositions.put(ItemType.Koff, 3);

			scoreTable = new DefaultTableModel(2, 4);
			scoreTable.setColumnIdentifiers(columnNames);
			beerBots = new ArrayList<BeerBot>();
			size = new Size(GameDisplay.ROWS, GameDisplay.COLUMNS);
			gameDisplay = new GameDisplay(scoreTable, columnPositions);
			initializeComponent();
			this.setVisible(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initializeComponent() {
		contentPane = (JPanel) this.getContentPane();
		// -----
		start = new JButton();
		left = new JButton();
		controlsPanel = new JPanel();
		botOneName = new JComboBox();
		botTwoName = new JComboBox();
		// -----
		gamePanel = new JPanel();
		// -----

		//
		// contentPane
		//
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(controlsPanel, BorderLayout.EAST);
		contentPane.add(gamePanel, BorderLayout.CENTER);

		gamePanel.add(gameDisplay.getMainComponent());
		gamePanel.setBorder(BorderFactory.createEtchedBorder());
		//
		// jButton2
		//
		start.setText("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					startGame(e);
					start.setEnabled(false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		//
		// jButton3
		//
		left.setText("Left");
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// gameController.start();
				try {
					gameController.moveLeft("one");
				} catch (InvalidArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		botOneName.addItem("Please select bot one");
		botTwoName.addItem("Please select bot two");
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().setUrls(ClasspathHelper
						.forClassLoader()));
		Set<Class<? extends BeerBot>> subTypes = reflections
				.getSubTypesOf(BeerBot.class);
		for (Class<? extends BeerBot> bot : subTypes) {
			try {
				BeerBot botInstance = (BeerBot) Class.forName(bot.getName())
						.newInstance();
				beerBots.add(botInstance);
				botOneName.addItem(botInstance.getName());
				botTwoName.addItem(botInstance.getName());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		botOneName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (botOneName.getSelectedIndex() > 0) {
					scoreTable.setValueAt(botOneName.getSelectedItem()
							.toString(), 0, 0);
				}
			}
		});

		botTwoName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (botTwoName.getSelectedIndex() > 0) {
					scoreTable.setValueAt(botTwoName.getSelectedItem()
							.toString(), 1, 0);
				}
			}
		});

		//
		// jPanel2
		//
		BoxLayout boxLayout = new BoxLayout(controlsPanel, BoxLayout.Y_AXIS);
		controlsPanel.setLayout(boxLayout);
		// GridLayout experimentLayout = new GridLayout(0,1);
		// experimentLayout.setHgap(10);
		// experimentLayout.setVgap(5);
		// controlsPanel.setLayout(experimentLayout);
		// controlsPanel.setLayout(new BorderLayout(31, 20));
		// jPanel.setLayout(null);

		controlsPanel.add(Box.createRigidArea(new Dimension(200, 25)));
		controlsPanel.add(botOneName);
		controlsPanel.add(Box.createRigidArea(new Dimension(200, 25)));
		controlsPanel.add(botTwoName);
		controlsPanel.add(Box.createRigidArea(new Dimension(200, 25)));
		// left.add(Box.createVerticalGlue());
		controlsPanel.add(start, BorderLayout.CENTER);
		controlsPanel.add(Box.createRigidArea(new Dimension(200, 25)));
		// addComponent(jPanel, botOneName, 5,57,137,22);
		botOneName.setMaximumSize(new Dimension(200, 50));
		botTwoName.setMaximumSize(new Dimension(200, 50));
		botTwoName.setAlignmentX(CENTER_ALIGNMENT);
		botOneName.setAlignmentX(CENTER_ALIGNMENT);
		start.setAlignmentX(CENTER_ALIGNMENT);

		JTable table = new JTable(scoreTable);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0)
				.setCellRenderer(new ImageRenderer());
		controlsPanel.add(table.getTableHeader());
		controlsPanel.add(table);
		Box.Filler glue = (Filler) Box.createVerticalGlue();
		glue.changeShape(glue.getMinimumSize(), new Dimension(0,
				Short.MAX_VALUE), // make glue greedy
				glue.getMaximumSize());

		// controlsPanel.add(glue);

		//
		// jPanel3
		//
		gamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		//
		// BeerBoatGUI
		//
		this.setTitle("BeerBots");
		this.setLocation(new Point(0, 0));
		// this.setSize(new Dimension(760, 710));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void startGame(ActionEvent e) throws InvalidArgumentException {
		if (botOneName.getSelectedIndex() <= 0
				|| botTwoName.getSelectedIndex() <= 0
				|| (botOneName.getSelectedIndex() == botTwoName
						.getSelectedIndex())) {
			JOptionPane.showMessageDialog(null, "Please select different bots",
					"BeerBots", JOptionPane.WARNING_MESSAGE);
			return;
		}
		botOne = beerBots.get(botOneName.getSelectedIndex() - 1);
		botTwo = beerBots.get(botTwoName.getSelectedIndex() - 1);
		
		scoreTable.setValueAt(botOne.getName(), 0, 0);
		for (int col = 1; col < 4; col++) {
			scoreTable.setValueAt(0, 0, col);
		}
		scoreTable.setValueAt(botTwo.getName(), 1, 0);
		for (int col = 1; col < 4; col++) {
			scoreTable.setValueAt(0, 1, col);
		}
		BeerBotEngine engine = new BeerBotEngine(size);
		gameDisplay.reset();
		gameDisplay.setBotNames(botOneName.getSelectedItem().toString(), botTwoName.getSelectedItem().toString());
		gameController = new GameController(engine, botOne, botTwo, gameDisplay, this);
		gameDisplay.refresh();
		gameController.start();
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		new BeerBoatGUI();
	}

	private class ImageRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel label = new JLabel();
			if (value != null) {
				label.setHorizontalAlignment(JLabel.CENTER);
				// value is parameter which filled by byteOfImage
				//
				String image;
				if(row == 0) {
					image = "ninja_red.png";
				} else {
					image = "ninja_green.png";
				}
//				ImageIcon icon = new ImageIcon(IconImageUtility.createImage(
//						this.getClass().getResourceAsStream(image),
//						GameDisplay.PIECE_WIDTH, null));
//				// JLabel botOne = new JLabel(icon, SwingConstants.CENTER);
//				label.setIcon(icon);
				label.setText(value.toString());
				//table.setRowHeight(icon.getIconHeight() + 3);
			}

			return label;
		}
	}

	@Override
	public void gameFinished(String botName, String details) {
		JOptionPane.showMessageDialog(null, botName + " won the game." + "\n" + details,
				"Game Over", JOptionPane.INFORMATION_MESSAGE);
		start.setEnabled(true);
	}

	@Override
	public void gameError(String error) {
		JOptionPane.showMessageDialog(null, error,
				"Game Over", JOptionPane.ERROR_MESSAGE);
		start.setEnabled(true);
	}

	@Override
	public void gameTimeout() {
		JOptionPane.showMessageDialog(null, "Time out! No winner.",
				"Game Over", JOptionPane.ERROR_MESSAGE);
		start.setEnabled(true);
	}

}
