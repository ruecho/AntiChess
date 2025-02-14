import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class King extends Piece
{

	/**
	Constructs a new King object
	   @param myRow     piece row
	   @param myCol     piece column
	   @param myColour  piece color
	 */
	public King(int myRow, int myCol, int myColour)
	{
		// Call super
		super(myRow, myCol, myColour);
		// Select the piece's image depending on its color
		if (myColour == 1)
			image = new ImageIcon("images\\whiteKing.png").getImage();
		else
			image = new ImageIcon("images\\blackKing.png").getImage();
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
		// Handles out of bounds
		int minRow = Math.max(-1, -row);   // min row index the king can move 
		int maxRow = Math.min(1, 7 - row); // min row index the king can move 
		int minCol = Math.max(-1, -col);   // max col index the king can move 
		int maxCol = Math.min(1, 7 - col); // min col index the king can move 

		// Searches all of the positions on the board that are around the king
		ArrayList<Point> moveLocations = new ArrayList<Point>();
		for (int addRow = minRow; addRow <= maxRow; addRow++)
		{
			for (int addCol = minCol; addCol <= maxCol; addCol++)
			{
				if (!(addCol == 0 && addRow == 0))
				{
					if (board[row + addRow][col + addCol] == null
							|| board[row + addRow][col + addCol].getColour() != colour)
					{
						Point currentPoint = new Point(col + addCol, row
								+ addRow);
						moveLocations.add(currentPoint);
					}
				}

			}
		}

		// If the piece may be forced to move, check for it
		for (Point currLoc : moveLocations)
		{
			// Check each valid location to see if it contains a piece
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
		return moveLocations;
	}
	
	public boolean isAi()
	{
		return isAi;
	}
}
