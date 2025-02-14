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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
   The main menu 
 */
public class ChessPanel extends JPanel
{

	// Declaring needed variables such as images and frames
	private Image menuImage;

	private Image main, instructions1, selectedMenu;

	private Image chessPiece;

	private Point mousePoint;

	private String selectedMenuName;

	private Point firstPoint;

	private AntiChessMain parentFrame;

	/**
	   Creates the main menu with options to start a new game, read instructions, or quit
	   @param parent   the frame it is using        
	 */
	public ChessPanel(AntiChessMain parent)
	{

		// Setting up variables and making this Panel the one the user can use
		parentFrame = parent;

		setPreferredSize(new Dimension(1024, 740));
		setBackground(new Color(0, 0, 0));
		setFocusable(true);
		requestFocusInWindow();
		repaint();
		this.parentFrame = parent;

		// Setting up variables for the menus and the cursor
		menuImage = new ImageIcon("images/MainMenu.png").getImage();

		instructions1 = new ImageIcon("images/Instructions1.png").getImage();

		chessPiece = new ImageIcon("images/cursor.png").getImage();

		selectedMenu = main;
		selectedMenuName = "main";
		firstPoint = new Point();
		mousePoint = new Point();

		// Transparent 16 x 16 pixel cursor image
		BufferedImage cursorImg = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame
		parent.getContentPane().setCursor(blankCursor);

		// Implementing mouse usage in the program
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());

	}

	/**
	   Draws all images and menus in the game
	   @param g   the Graphics object used for drawing        
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draws the menus
		g.drawImage(menuImage, 0, 0, this);
		g.drawImage(selectedMenu, 0, 0, this);

		// Draws the custom cursor wherever it goes
		g.drawImage(chessPiece, mousePoint.x - chessPiece.getWidth(this) / 3,
				mousePoint.y - chessPiece.getHeight(this) / 3, this);

	}

	/**
	   Determines which menu the user clicked and navigates to the appropriate page
	   @param x   The last clicked X position of the mouse     
	   @param y   The last clicked Y position of the mouse          
	   @param x2  The current X Position of the mouse           
	   @param y2  The current Y position of the mouse           
	 */
	private void decideMenu(int x, int y, int x2, int y2)
	{

		// Determine the menu to navigate to based on where the user clicked on the image
		if (selectedMenuName.equals("main"))
		{

			// Navigate to the instructions1 page if the click falls within the specified coordinates
			if (x > 19 && y > 216 && x2 < 307 && y2 < 254)
			{

				selectedMenu = instructions1;
				selectedMenuName = "instructions1";

				repaint();
			}

			// Quits if the exit button is pressed
			if (x > 20 && y > 390 && x2 < 105 && y2 < 432)
			{
				System.exit(0);
			}
		}

	
		// Returns to the menu
		else if (selectedMenuName.equals("instructions1"))
		{

			if (x > 865 && y > 585 && x2 < 956 && y2 < 653)
			{
				selectedMenu = main;
				selectedMenuName = "main";
				repaint();

			}

		}

	}

	/**
	   Redraws the cursor at the mouse's current position
	   @param event   The movement of the mouse by the user           
	 */
	public void mouseMoved(MouseEvent event)
	{

		// Sets the current point every time the mouse is moved
		Point currentPoint = event.getPoint();

		mousePoint = currentPoint;
		repaint();

	}

	/**
	   Handles mouse movements and clicks
	 */
	private class MouseHandler extends MouseAdapter
	{

		/**
		   Handles mouse press events
		   @param event  The mouse press event         
		 */

		public void mousePressed(MouseEvent event)
		{

			// Update the position of the mouse for the custom cursor
			Point currentPoint = event.getPoint();

			firstPoint = currentPoint;
			repaint();

		}

		/**
		   Handles mouse release events
		   @param event  The release of the click            
		 */
		public void mouseReleased(MouseEvent event)
		{

			// Update the position of the mouse
			Point currentPoint = event.getPoint();

			// Determine the menu to display based on the mouse position
			decideMenu(firstPoint.x, firstPoint.y, currentPoint.x,
					currentPoint.y);

			// If the new game button is clicked, switch to the game board
			if (firstPoint.x > 20 && firstPoint.y > 149 && currentPoint.x < 288
					&& currentPoint.y < 191)

			{
				parentFrame.chessArea.setVisible(false);

				parentFrame.myBoard.setVisible(true);

				repaint();

			}

		}
	}

	/**
	   Handles mouse movement
	 */
	class MouseMotionHandler implements MouseMotionListener
	{
		/**
		   Redraws the cursor based on mouse movement
		   @param event   The mouse movement event         
		 */
		public void mouseMoved(MouseEvent event)
		{

			// Update the position of the mouse
			Point currentPoint = event.getPoint();

			mousePoint = currentPoint;
			repaint();
		}

		@Override
		/**
		   Updates the cursor position when the mouse is dragged
		   @param event   The mouse drag event
		 */
		public void mouseDragged(MouseEvent event)
		{
			// Update the position of the mouse
			Point currentPoint = event.getPoint();

			mousePoint = currentPoint;
			repaint();

		}

	}

}
