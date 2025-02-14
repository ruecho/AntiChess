import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Pawn extends Piece
{

	/**
	   Constructs a new Pawn object
	   @param myRow     piece row
	   @param myCol     piece column
	   @param myColour  piece color
	 */
	public Pawn(int myRow, int myCol, int myColour)
	{
		super(myRow, myCol, myColour);
		// Select the piece's image depending on its color
		if (myColour == 1)
			image = new ImageIcon("images\\whitePawn.png").getImage();
		else
			image = new ImageIcon("images\\blackPawn.png").getImage();

	}

	/**
	   Finds all valid moves for this piece
	   @param board    the current board
	   @param isForced check for forced moves
	   @return legal move locations
	 */
	public ArrayList<Point> getMoveLocations(Piece[][] board, boolean isForced)
	{
		canTake = false;
		// If there's no piece in front of the pawn, it can move 1 space up
		int maxRow = Math.min(1, Math.min(7 - row, row));
		ArrayList<Point> moveLocations = new ArrayList<Point>();

		int addRow;
		// Handles the direction of movement depending on piece color
		if (colour == 1)
			addRow = -1;
		else
			addRow = 1;
		// If there's no piece in front of the pawn, it can move 1 square 
		if (maxRow != 0 && board[row + addRow][col] == null)
		{
			Point currentPoint = new Point(col, row + addRow);
			moveLocations.add(currentPoint);
		}

		/*
		  Pawn can move 2 squares in the first move 
		  2*addRow --> for 2 rows forward, addRow=1/-1 
		  row+2*addRow --> new row index    
		 * */
		if (row + 2 * addRow < 8 && row + 2 * addRow > -1 && !hasMoved
				&& board[row + 2 * addRow][col] == null
				&& board[row + addRow][col] == null)
		{
			Point currentPoint = new Point(col, row + 2 * addRow);
			moveLocations.add(currentPoint);
		}

		// Handles capturing diagonally
		if (col <= 6 && row != 0 && row != 7) // Checks boundaries for the move diagonal right
		{
			if (board[row + addRow][col + 1] != null) // Checks if there is a piece on diagonal right
			{	// Adds the valid capture move to the moveLocations list
				if (board[row + addRow][col + 1].getColour() != colour) 
				{
					Point currentPoint = new Point(col + 1, row + addRow);
					moveLocations.add(currentPoint);
				}
			}
		}
		
		if (col >= 1 && row != 0 && row != 7) // Checks boundaries for the move diagonal left 
		{
			if (board[row + addRow][col - 1] != null) // Checks if there is a piece on diagonal left
			{	// Adds the valid capture move to the moveLocations list
				if (board[row + addRow][col - 1].getColour() != colour) 
				{
					Point currentPoint = new Point(col - 1, row + addRow);
					moveLocations.add(currentPoint);
				}
			}
		}

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

}
