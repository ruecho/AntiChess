import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Rook extends Piece
{

	/**
	  Constructs a new Rook object.
		   @param myRow   the row of the piece
		   @param	 	  myCol    the column of the piece
		   @param         myColour the color of the piece
	 */

	public Rook(int myRow, int myCol, int myColour)
	{
		// Call super Piece class
		super(myRow, myCol, myColour);
		// Select the piece image depending on its color
		if (myColour == 1)
			image = new ImageIcon("images\\whiteRook.png").getImage();
		else
			image = new ImageIcon("images\\blackRook.png").getImage();
	}

	/**
	   Finds all valid moves for this piece 
		   @param board     the current board
		   @param isForced  check for forced moves    
		   @return 		    legal move locations
	 */
	public ArrayList<Point> getMoveLocations(Piece[][] board, boolean isForced)
	{

		canTake = false;
		ArrayList<Point> moveLocations = new ArrayList<Point>();
		// Add moves in four directions - Up Down Right Left
		moveLocations.addAll(findMoveLocations(board, -1, 0));  
		moveLocations.addAll(findMoveLocations(board, 1, 0));  		
		moveLocations.addAll(findMoveLocations(board, 0, 1));  
		moveLocations.addAll(findMoveLocations(board, 0, -1)); 

		// Check for forced moves
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
