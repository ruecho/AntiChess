import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AntiChessMain extends JFrame implements ActionListener
{

	private JMenuItem aboutOption; // Menu item for About

	public Board myBoard; // Panel for the game board

	public ChessPanel chessArea; // Panel for the main menu

	/**
	   Constructor to set up the game window and components
	 */
	public AntiChessMain()
	{
		super("AntiChess");
		setResizable(true);

		// Position in the middle of the window
		setLocation(0, 0);

		// Add in an Icon - Black King
		setIconImage(new ImageIcon("images\\blackKing.png").getImage());

		// initialize the right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(new Color(32, 178, 170));
		rightPanel.setLayout(new BorderLayout());

		setLayout(new BorderLayout());

		// Set up the game board and main menu panels
		myBoard = new Board(this);
		
		add(myBoard, BorderLayout.WEST);
		myBoard.setVisible(false);

		chessArea = new ChessPanel(this);
		add(chessArea, BorderLayout.CENTER);
		
		

		// Add the menu bar
		addMenus();
	}

	/**
	   Adds in all the menus at the top of the game: -Game -Help -About
	 */
	private void addMenus()
	{
		// Set up all the menus
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');

		JMenu helpMenu = new JMenu("Help");

		aboutOption = new JMenuItem("About");
		aboutOption.addActionListener(this);
		helpMenu.add(aboutOption);
		helpMenu.setMnemonic('H');

		JMenuItem newOption = new JMenuItem("New (vs Computer)");
		newOption.addActionListener(new ActionListener() {
			/**
			   Responds to the New Menu choice
			   @param event    The event that selected this menu option    
			 */
			public void actionPerformed(ActionEvent event)
			{
				myBoard.newGame(true); // Starts a new game with AI 
			}
		});
		JMenuItem newOption2 = new JMenuItem("New (vs Player)");
		newOption2.addActionListener(new ActionListener() {
			/**
			 * Responds to the New Menu choice
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				myBoard.newGame(false);
			}
		});
		// Add menu tabs to menu
		menuBar.add(gameMenu);
		gameMenu.add(newOption);
		gameMenu.add(newOption2);
		gameMenu.add(aboutOption);

		setJMenuBar(menuBar);
	}

	/**
	   Set up the frame and make it visible to the user
	 */

	public static void main(String[] args)
	{
		AntiChessMain frame = new AntiChessMain();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * 
	 * Based of which tab the user clicked it will determine which information
	 * to display or if to create a new game
	 * 
	 * @param event
	 *            The action which is performed by the user
	 */
	public void actionPerformed(ActionEvent event)
	{

		// Displays the about tab on the screen
		if (event.getSource() == aboutOption)
		{
			JOptionPane
		    .showMessageDialog(
		        myBoard,
		        "Antichess Engine"
		            + "\n\u00a9 2024"
		            + "\nResources "
		            + "\nhttps://www.antichess.org/antichess-basics/"
		            + "\nhttps://lichess.org/", 
		        "About Antichess",
		        JOptionPane.INFORMATION_MESSAGE);

		}

	}
}
