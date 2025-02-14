import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JPanel // JPanel to create a custom panel for the game board
{
	private Piece[][] board; // 2D array representing the game board
	private int turn; // Keeps track of whose turn it is (1 for player one, 2 for player two)

	private Point[] lastMove; // Stores the last move made

	private static Image boardBackground = new ImageIcon(
			"images\\chessboardPlainBig.png").getImage();

	private static Image panelBackground = new ImageIcon("images/boardMenu.png")
			.getImage();

	// p = pawn, r = rook, n = knight, b = bishop, k = king, q = queen
	private static final char[] pieceSet = { 'p', 'p', 'p', 'p', 'p', 'p', 'p',
			'p', 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' };

	private Piece selectedPiece; // Currently selected piece

	private Player[] players; // Array of players

	private boolean[] areAI; // Array indicating if the players are AI

	private boolean gameOver; // Flag to check if the game is over

	private Image menuImage;

	private Image main, instructions1, instructions2, instructions3,
			instructions4, selectedMenu;

	private Image chessPiece;

	private Point firstPoint; // Point for mouse handling

	private Point mousePoint; // Point for mouse handling

	private String selectedMenuName; // Name of the currently selected menu

	private AntiChessMain parentFrame; // Reference to the main frame

	private boolean aiOn = false; // Flag to check if AI is on

	public boolean instructionsCalled = false; // Flag to check if instructions are called

	/**
	   Constructs a new board object to manage the game
	   @param parent    the frame the board exists in      
	 */
	public Board(AntiChessMain parent)
	{
		setPreferredSize(new Dimension(1024, 740));
		requestFocusInWindow();

		// Declares the frames to utilize the Main Class
		parentFrame = parent;
		this.parentFrame = parent;

		// Declaring images
		instructions1 = new ImageIcon("images/Instructions1.png").getImage();
		instructions2 = new ImageIcon("images/Instructions2.png").getImage();
		instructions3 = new ImageIcon("images/Instructions3.png").getImage();
		instructions4 = new ImageIcon("images/Instructions4(inGame).png")
				.getImage();

		// Setting the cursor image
		chessPiece = new ImageIcon("images/cursor.png").getImage();

		// Setting the menu names to show which menus to draw
		selectedMenu = main;
		selectedMenuName = "main";

		// Keeps track of the mouse position
		firstPoint = new Point();
		mousePoint = new Point();

		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		parent.getContentPane().setCursor(blankCursor);

		newGame(!aiOn);
		// Add mouse listeners
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());

		aiOn = false;
	}

	/**
	   Resets all variables that change over the course of the game
	   @param whether   an AI is playing or not        
	 */
	public void newGame(boolean aiOn)
	{
		// Set up all required variables for a new game
		gameOver = false; // Sets the gameOver flag to false, game is ongoing
		board = new Piece[8][8]; // Initializes an 8x8 board array to hold the pieces 
		areAI = new boolean[2];  // Initializes an array to track whether each player is an AI or not

		// Check if AI is playing
		if (aiOn)
		{
			areAI[0] = false; // Player 1 is Human
			areAI[1] = true;  // Player 2 is AI
		}
		else
		{
			areAI[0] = false; // Player 1 is Human
			areAI[1] = false; // Player 2 is Human
		}
		lastMove = new Point[1]; // 1 point array (x,y) to store the last move
		turn = 1; // Sets the turn to player 1
		selectedPiece = null; // Resets the selected piece
		instructionsCalled = false;
		// Set up players
		players = new Player[2]; // Array to hold two players 
		// Check if players are AI
		if (aiOn)
		{
			if (areAI[0])
				players[0] = new AiPlayer("White (You)", 1, 1, board); // First player (white) is AI, AI difficulty is 1  
			else
				players[0] = new Player("White (You)", 1); // First player (white) is Human
			
			if (areAI[1])
				players[1] = new AiPlayer("Computer", 2, 1, board); // Second player (black) is AI, AI difficulty is 1
			else
				players[1] = new Player("Computer", 2); // Second player (black) is Human
		}
		else
		{
			players[0] = new Player("White Player", 1); // First human player (white) 
			players[1] = new Player("Black Player", 2); // Second human player (black) 
		}
		// Set up black pieces
		int pieceCount = 0; // Initialize piece count
		for (int row = 1; row > -1; row--) // Starting from the second row for the black pieces 
		{
			for (int col = 0; col < 8; col++) // Loops trough each column 
			{
				if (pieceSet[pieceCount] == 'p') // Place black pawn
				{
					board[row][col] = new Pawn(row, col, 2);
				}
				else if (pieceSet[pieceCount] == 'r') // Place black rook
				{
					board[row][col] = new Rook(row, col, 2);
				}
				else if (pieceSet[pieceCount] == 'n') // Place black knight
				{
					board[row][col] = new Knight(row, col, 2);
				}
				else if (pieceSet[pieceCount] == 'b') // Place black bishop
				{
					board[row][col] = new Bishop(row, col, 2);
				}
				else if (pieceSet[pieceCount] == 'k') // Place black king
				{
					board[row][col] = new King(row, col, 2);
				}
				else   // Place black queen
				{
					board[row][col] = new Queen(row, col, 2);
				}
				pieceCount++;
			}
		}
		pieceCount = 0;
		// Set up white pieces
		for (int row = 6; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (pieceSet[pieceCount] == 'p')
				{
					board[row][col] = new Pawn(row, col, 1);
				}
				else if (pieceSet[pieceCount] == 'r')
				{
					board[row][col] = new Rook(row, col, 1);
				}
				else if (pieceSet[pieceCount] == 'n')
				{
					board[row][col] = new Knight(row, col, 1);
				}
				else if (pieceSet[pieceCount] == 'b')
				{
					board[row][col] = new Bishop(row, col, 1);
				}
				else if (pieceSet[pieceCount] == 'k')
				{
					board[row][col] = new King(row, col, 1);
				}
				else
				{
					board[row][col] = new Queen(row, col, 1);
				}
				pieceCount++;
			}
		}
		repaint();
	}
	// method to handle stalemate   *** Need to be checked 
	public boolean isStalemate(int playerColour) {
	    for (int row = 0; row < 8; row++) {
	        for (int col = 0; col < 8; col++) {
	            if (board[row][col] != null && board[row][col].getColour() == playerColour) {
	                ArrayList<Point> moves = board[row][col].getMoveLocations(board, false);
	                if (!moves.isEmpty()) {
	                    return false; // Player has at least one legal move
	                }
	            }
	        }
	    }
	    return true; // No legal moves found, it's a stalemate
	}


	/**
	   Changes the current player and makes the AI move
	 */
	public void changePlayer() {
	    selectedPiece = null; // Reset the selected piece to null
	    // Switch turn to the other player
	    if (turn == 1)
	        turn++;
	    else
	        turn--;

	    paintImmediately(new Rectangle(0, 0, 600, 600)); // Refresh the board display
	    // Initialize counters for each player's pieces
	    int[] pieceCounter = new int[3];
	    pieceCounter[1] = 0;
	    pieceCounter[2] = 0;
	    boolean hasMoves = false; // Initialize a flag to check if the current player has any available moves

	    for (int row = 0; row < 8; row++) {
	        for (int col = 0; col < 8; col++) {
	            if (board[row][col] != null) {  // If there's a piece at the current position
	                if (board[row][col].getColour() == turn) {  // If the piece belongs to the current player
	                    pieceCounter[turn]++;   // Increment the count for the current player's pieces
	                    ArrayList<Point> tempArray = board[row][col].getMoveLocations(board, true); // Get the possible moves
	                    if (tempArray.size() > 0)
	                        hasMoves = true; // If there are available moves, set hasMoves to true
	                } else {  // If the piece belongs to the opponent
	                    if (turn == 1) // Player 1 turn 
	                        pieceCounter[2]++; // Increase other player 
	                    else // Player 2 turn
	                        pieceCounter[1]++; // Increase other player 
	                }
	            }
	        }
	    }

	    if (pieceCounter[1] == 0) // Player 1 is the winner, counter has no moves
	        win(0);
	    else if (pieceCounter[2] == 0) // Player 2 is the winner 
	        win(1);
	    else if (!hasMoves && isStalemate(turn)) { // Both has no legal moves 
	        // Declare stalemated player as the winner
	        win(turn - 1);
	    } else if (areAI[turn - 1] && !gameOver) {
	        players[turn - 1].makeMove(board, lastMove);
	        changePlayer();
	    }
	}


	/**
	   The paint component that draws the graphics on the screen
	   @param g  the Graphics to draw in         
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(panelBackground, 0, 0, null);
		g.drawImage(boardBackground, 0, 0, null);
		g.setFont(new Font("BIG", Font.BOLD, 19));

		// Draw box to display who's turn it is
		if (turn == 1)
		{
			g.setColor(Color.BLACK); // Set the color to black
			g.fillRect(700, 80, 225, 25); // Draw a filled rectangle for the background of the text
			g.setColor(Color.WHITE); // Set the color to white for the text
		}
		else
		{
			g.setColor(Color.BLACK); 
			g.fillRect(700, 80, 225, 25); 
			g.setColor(Color.WHITE);
		}
		g.drawString("Current Player: " + players[turn - 1].getName(), 700, 100); 
		// Highlight force takes
		// Run through board and highlight all pieces forced to move
		g.setColor(Color.RED); // Set the color to red for the highlights
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (board[row][col] != null
						&& board[row][col].getColour() == turn
						&& board[row][col].forcedMove())
				{
					g.fillRect(board[row][col].getLocation().x * 71 + 15,
							board[row][col].getLocation().y * 71 + 19, 70, 70);
				}
			}
		}
		// Draw pink highlights to display last move
		if (lastMove[0] != null)
		{
			g.setColor(Color.pink);
			g.fillRect(lastMove[0].x * 71 + 15, lastMove[0].y * 71 + 19, 70, 70);
		}
		// Show valid moves for selected piece
		if (selectedPiece != null)
		{
			showMoves(g);
		}

		// Draw pieces
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (board[row][col] != null)
					board[row][col].draw(g);
			}
		}
		// If they want to see the instructions it will draw and make everything
		// else unable to be clicked
		if (instructionsCalled == true)
		{

			g.drawImage(menuImage, 0, 0, this);
			g.drawImage(selectedMenu, 0, 0, this);

		}
		// Draws the cursor based on where the mouse is
		g.drawImage(chessPiece, mousePoint.x - chessPiece.getWidth(this) / 3,
				mousePoint.y - chessPiece.getHeight(this) / 3, this);
	}

	/**
	   Highlights in yellow the valid moves for the current piece
	   @param g   the Graphics to draw in          
	 */
	public void showMoves(Graphics g)
	{
		// Get the valid squares to move to
		ArrayList<Point> validLocations = selectedPiece.getMoveLocations(board,
				true);
		if (validLocations == null) // If there are no valid locations, exit the method
			return;
		// Highlight all valid squares
		for (Point currPoint : validLocations)
		{
			g.setColor(Color.YELLOW); // Set the color to yellow
			g.fillRect(currPoint.x * 71 + 15, currPoint.y * 71 + 19, 70, 70);
		}
		// Fill in square where selected piece is
		g.setColor(Color.ORANGE);  // Set the color to orange
		Point selectedLoc = selectedPiece.getLocation(); // Get the location of the selected piece
		g.fillRect(selectedLoc.x * 71 + 15, selectedLoc.y * 71 + 19, 70, 70);

	}

	/**
	   Displays tie game message and options to quit or restart
	 */
	public void tie()
	{
		// Display tie message
		int reply = JOptionPane.showConfirmDialog(null, "It's a tie!"
				+ "\nWould you like to play again?", "Game Over!",
				JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION)  // If the player chooses to play again
		{
			// Ask if they want to play with AI again
			int secondReply = JOptionPane.showConfirmDialog(null,
					"Would you like to play with AI on?", "New Game",
					JOptionPane.YES_NO_OPTION);
			if (secondReply == JOptionPane.YES_OPTION) // Set the aiOn flag based on the player's choice
			{
				aiOn = true;
			}
			else
				aiOn = false;
			newGame(aiOn); // Start a new game with the chosen AI setting
		}

		else
		{
			System.exit(0);  // If the player chooses not to play again, exit the game
		}
	}

	/**
	   Displays winning message (AI or player)
	   @param winner    the player/AI that won      
	 */
	public void win(int winner)
	{
		// Display win message
		int reply = JOptionPane.showConfirmDialog(null,
				"The winner is: " + players[winner].getName()
						+ "\nWould you like to play again?", "Game Over!",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION)
		{

			// Ask if they want to play with AI again
			int secondReply = JOptionPane.showConfirmDialog(null,
					"Would you like to play with AI on?", "New Game",
					JOptionPane.YES_NO_OPTION);
			if (secondReply == JOptionPane.YES_OPTION)
			{
				aiOn = true;
			}
			else
				aiOn = false;
			newGame(aiOn);
		}
		else
		{
			System.exit(0);
		}
	}

	// Inner class to handle mouse events
	private class MouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent event) 
		{
			Point currentPoint = event.getPoint(); // Get the point where the mouse is pressed

			firstPoint = currentPoint; // Store this point as the first point
			repaint(); // Refresh the board to reflect any changes
		}

		public void mouseReleased(MouseEvent event)
		{
			Point selectedPoint = event.getPoint(); // Get the point where the mouse is released

			// Deciding which menu to send the user to based on where they clicked on the image
			if (selectedPoint.getX() > 660 && selectedPoint.getY() > 415
					&& selectedPoint.getX() < 780 && selectedPoint.getY() < 482
					&& instructionsCalled == false)

			{
				// Sets the correct JPanel visible for the user to see based on what they clicked
				parentFrame.chessArea.setVisible(true);

				parentFrame.myBoard.setVisible(false);
				newGame(aiOn);
				repaint();

			}

			// Exits if the player wants to quit
			else if (selectedPoint.getX() > 870 && selectedPoint.getY() > 509
					&& selectedPoint.getX() < 994 && selectedPoint.getY() < 569
					&& instructionsCalled == false)
			{
				System.exit(0);
			}
			// Links all the instruction pages together using the selected menu variables
			else if (selectedPoint.getX() > 660 && selectedPoint.getY() > 506
					&& selectedPoint.getX() < 782 && selectedPoint.getY() < 569)
			{
				selectedMenu = instructions1; // Set the selected menu to instructions1
				selectedMenuName = "instructions1"; // Set the selected menu name
				instructionsCalled = true; // Set the instructions called flag to true

				repaint(); // Refresh the board
			}
			if (selectedMenuName.equals("instructions1"))
			{

				if (selectedPoint.getX() > 865 && selectedPoint.getY() > 585
						&& selectedPoint.getX() < 956
						&& selectedPoint.getY() < 653)
				{
					selectedMenu = instructions2; // Set the selected menu to instructions2
					selectedMenuName = "instructions2"; // Set the selected menu name
					repaint(); // Refresh the board

				}

			}

			else if (selectedMenuName.equals("instructions2"))
			{

				if (selectedPoint.getX() > 865 && selectedPoint.getY() > 585
						&& selectedPoint.getX() < 956
						&& selectedPoint.getY() < 653)
				{
					selectedMenu = instructions3;
					selectedMenuName = "instructions3";
					repaint();

				}

			}

			else if (selectedMenuName.equals("instructions3"))
			{

				if (selectedPoint.getX() > 865 && selectedPoint.getY() > 585
						&& selectedPoint.getX() < 956
						&& selectedPoint.getY() < 653)
				{
					selectedMenu = instructions4;
					selectedMenuName = "instructions4(inGame)";
					repaint();

				}

			}
			else if (selectedMenuName.equals("instructions4(inGame)"))
			{

				if (selectedPoint.getX() > 865 && selectedPoint.getY() > 585
						&& selectedPoint.getX() < 956
						&& selectedPoint.getY() < 653)
				{
					selectedMenu = main;
					parentFrame.myBoard.setVisible(true);
					instructionsCalled = false;
					repaint();

				}

			}
			// If they resign, it asks if they want to play again
			else if (selectedPoint.getX() > 865 && selectedPoint.getY() > 421
					&& selectedPoint.getX() < 987 && selectedPoint.getY() < 487
					&& instructionsCalled == false && aiOn)
			{

				int reply = JOptionPane.showConfirmDialog(null,
						"The winner is: The Computer!"
								+ "\nWould you like to play again?",
						"Game Over!", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION)
				{
					// Ask if they want to play with AI again
					int secondReply = JOptionPane.showConfirmDialog(null,
							"Would you like to play with AI on?", "New Game",
							JOptionPane.YES_NO_OPTION);
					if (secondReply == JOptionPane.YES_OPTION)
					{
						aiOn = true;
					}
					else
						aiOn = false;
					newGame(aiOn);
				}
				else
				{
					System.exit(0);
				}
			}
			// Resign for player vs player
			else if (selectedPoint.getX() > 865 && selectedPoint.getY() > 421
					&& selectedPoint.getX() < 987 && selectedPoint.getY() < 487
					&& instructionsCalled == false && !aiOn)
			{
				int reply = JOptionPane.showConfirmDialog(null,
						"The winner is: " + players[(turn) % 2].getName()
								+ "\nWould you like to play again?",
						"Game Over!", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION)
				{
					// Ask if they want to play with AI again
					int secondReply = JOptionPane.showConfirmDialog(null,
							"Would you like to play with AI on?", "New Game",
							JOptionPane.YES_NO_OPTION);
					if (secondReply == JOptionPane.YES_OPTION)
					{
						aiOn = true;
					}
					else
						aiOn = false;
					newGame(aiOn);
				}
				else
				{
					System.exit(0);
				}
			}
			// Make sure point is on board and it's not AI's turn and only if
			// the instructions aren't showing
			if (selectedPoint.getX() < 600 && selectedPoint.getY() < 600
					&& !(players[turn - 1] instanceof AiPlayer)
					&& instructionsCalled == false)
			{

				selectedPoint.x /= 75;
				selectedPoint.y /= 75;
				// No piece currently selected or not on the area
				if (selectedPiece == null
						|| selectedPiece.getMoveLocations(board, true) != null
						&& !selectedPiece.getMoveLocations(board, true)
								.contains(selectedPoint))
				{
					// Figure out which square/piece was clicked on
					selectedPiece = board[selectedPoint.y][selectedPoint.x];
					// Selected opponents piece
					if (selectedPiece != null
							&& selectedPiece.getColour() != turn)
						selectedPiece = null;
				}
				else
				{
					// Deduct number of pieces left for player
					if (board[selectedPoint.y][selectedPoint.x] != null)
					{
						selectedPiece.move(selectedPoint, board);
						
						// Handle promotion for human players
	                    if (selectedPiece instanceof Pawn) {
	                        if (selectedPiece.getColour() == 1 && selectedPiece.getLocation().y == 0 ||
	                            selectedPiece.getColour() == 2 && selectedPiece.getLocation().y == 7) {
	                            selectedPiece.promote(board);
	                        }
	                    }

						// Other player's turn
						changePlayer();
					}
					else
					{
						selectedPiece.move(selectedPoint, board);
						
						 // Handle promotion for human players
	                    if (selectedPiece instanceof Pawn) {
	                        if (selectedPiece.getColour() == 1 && selectedPiece.getLocation().y == 0 ||
	                            selectedPiece.getColour() == 2 && selectedPiece.getLocation().y == 7) {
	                            selectedPiece.promote(board);
	                        }
	                    }
	                    
						lastMove[0] = selectedPoint;
						// Other player's turn
						changePlayer();
					}

				}
			}
			repaint();
		}
	}

	// Inner Class to handle mouse movements
	private class MouseMotionHandler implements MouseMotionListener
	{
		public void mouseMoved(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			mousePoint = currentPoint;
			repaint();

		}

		public void mouseDragged(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			mousePoint = currentPoint;
			repaint();
		}
	}
}
