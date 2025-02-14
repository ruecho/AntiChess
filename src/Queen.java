//Queen.java

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Queen extends Piece
{

	/**
 	 Constructs a new Queen object
		 @param myRow     the row the piece is on            
	  	 @param myCol     the column the piece is on           
	     @param myColour  the color of the piece       
	 */
	public Queen(int myRow, int myCol, int myColour)
	{
		// Call super (Piece)
		super(myRow, myCol, myColour);
		// Select the piece's image depending on its colour
		if (myColour == 1)
			image = new ImageIcon("images\\whiteQueen.png").getImage();
		else
			image = new ImageIcon("images\\blackQueen.png").getImage();
	}

	/**
	   Finds all valid moves for this piece.
	   @param board    the current board
	   @param isForced check for forced moves
	   @return legal move locations
	 */
	public ArrayList<Point> getMoveLocations(Piece[][] board, boolean isForced)
	{

		ArrayList<Point> moveLocations = new ArrayList<Point>();

		// Add moves in all directions
		moveLocations.addAll(findMoveLocations(board, -1, 0));  // Up
		moveLocations.addAll(findMoveLocations(board, 1, 0));   // Down
		moveLocations.addAll(findMoveLocations(board, 0, 1));   // Right    
		moveLocations.addAll(findMoveLocations(board, 0, -1));  // Left
		moveLocations.addAll(findMoveLocations(board, -1, -1)); // D Left-up
		moveLocations.addAll(findMoveLocations(board, -1, 1));  // D Right-up
		moveLocations.addAll(findMoveLocations(board, 1, -1));  // D Left-down
		moveLocations.addAll(findMoveLocations(board, 1, 1));   // D Right-down

		// If the piece may be forced to move, check for it
		if (isForced)
		{
			// Check each valid location to see if it contains a piece
			for (Point currLoc : moveLocations)
			{
				if (board[currLoc.y][currLoc.x] != null)
				{
					// Modify valid moves
					this.canTake = true;
					ensureForcedTake(board, moveLocations);
					return moveLocations;
				}
			}
			// Check if another piece must take
			if (oneCanTake)
				moveLocations.clear();
		}
		return moveLocations;
	}

	/**
	   Finds all valid moves in one direction
	 	   @param board     the board to check in          
	  	   @param addToRow  the y direction to search in         
	       @param addToCol  the x direction to search in           
	       @return          the valid locations for the piece to move to
	 */
	public ArrayList<Point> findMoveLocations(Piece[][] board, int addToRow,
			int addToCol)
	{
		canTake = false;
		// Handles out of bounds
		int stopRow = 0;
		if (addToRow < 0)
			stopRow = -row;
		else if (addToRow > 0)
			stopRow = 7 - row;

		int stopCol = 0;
		if (addToCol < 0)
			stopCol = -col;
		else if (addToCol > 0)
			stopCol = 7 - col;

		// Check valid moves in the given directions
		ArrayList<Point> directionLocations = new ArrayList<Point>();
		int addCol = addToCol;
		int addRow = addToRow;
		boolean blocking = false;
		while (!blocking
				&& (Math.abs(addCol) <= Math.abs(stopCol) && Math.abs(addRow) <= Math
						.abs(stopRow)))
		{
			if (board[row + addRow][col + addCol] == null
					|| board[row + addRow][col + addCol].getColour() != colour)
			{
				Point currentPoint = new Point(col + addCol, row + addRow);
				directionLocations.add(currentPoint);
			}
			if (board[row + addRow][col + addCol] != null)
			{
				blocking = true;
			}
			addCol += addToCol;
			addRow += addToRow;
		}
		return directionLocations;
	}
}
