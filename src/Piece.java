
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public abstract class Piece extends Rectangle
{
	protected int row;

	protected int col;

	protected int colour;

	protected Image image;

	protected boolean hasMoved;

	protected ArrayList<Point> allMoveLocations;

	protected boolean canTake;

	protected static boolean oneCanTake;

	private ArrayList<Move> myMoves;
	
	protected boolean isAi;

	/**
	 Constructs the piece object
	   @param myRow     the row the piece is on the board
	   @param myCol     the column the piece is on the board       
	   @param myColour  the color of the piece on the board           
	 */
	public Piece(int myRow, int myCol, int myColour)
	{
		row = myRow;
		col = myCol;
		colour = myColour;
		hasMoved = false;
		canTake = false;
		allMoveLocations = new ArrayList<Point>();
		oneCanTake = false;
		myMoves = new ArrayList<Move>();
	}

	public int getType() {
        if (this instanceof Pawn) return 0;
        else if (this instanceof Knight) return 1;
        else if (this instanceof Bishop) return 2;
        else if (this instanceof Rook) return 3;
        else if (this instanceof Queen) return 4;
        else if (this instanceof King) return 5;
        return -1; // Error case
    }
	
	// Draws the graphical representation of the piece
	
	public void draw(Graphics g)
	{
		g.drawImage(image, col * 71 + 25, row * 71 + 25, null);
	}

	/**
	 Finds all valid moves for this piece
	   @param isForced  checks for forced moves         
	   @return legal move locations
	   will be implemented for each piece
	 */
	public abstract ArrayList<Point> getMoveLocations(Piece[][] board,
			boolean isForced);

	/**
	   Moves the piece to the given location on the board
	   @param loc      the new location for the piece
	   @param board    the board the piece exists on            
	 */
	public void move(Point loc, Piece[][] board)
	{
		int originalCol = col;
		board[row][col] = null;
		row = loc.y;
		col = loc.x;
		board[row][col] = this;
		hasMoved = true;

		// Check for promote
		if (this instanceof Pawn)
			if (colour == 0 && row == 7 || colour == 1 && row == 0) // Ensuring pawn reaches last rank
	        {
	            promote(board);
	        }
		oneCanTake = false;
		myMoves.clear();
	}

	/*
	  filters out non-capturing moves
	  @param moveLocations all valid move locations
	 */
	public void ensureForcedTake(Piece[][] board, ArrayList<Point> moveLocations)
	{
		for (int currLoc = moveLocations.size() - 1; currLoc > -1; currLoc--)
		{
			if (board[moveLocations.get(currLoc).y][moveLocations.get(currLoc).x] == null)
			{
				moveLocations.remove(currLoc);
			}
		}
		oneCanTake = true;
	}


	public int getColour()
	{
		return colour;
	}


	public boolean getHasMoved()
	{
		return hasMoved;
	}

	/**
	  Changes the value that stores if any piece on the board can take
	  @param newValue the value to set the variable to
	 */
	public void changeCanTake(boolean newValue)
	{
		oneCanTake = newValue;
	}

	/**
	  Checks if one of the pieces is forced to take
	  @return whether or not a piece must take
	 */
	public boolean canOneTake()
	{
		return oneCanTake;
	}

	/**
	 Checks if this current piece must take
	 @return whether or not this piece must take
	 */
	public boolean forcedMove()
	{
		return canTake;
	}

	/**
	  Gets the location the piece is at on the board
	  @return the location of the piece
	 */
	public Point getLocation()
	{
		return new Point(col, row);
	}

	/**
	   Calculates the best move for this piece based on the score of each move
	   @param depthCount the number of times to look ahead for scoring
	 */
	public void lookAhead(Piece[][] board, int depthCount)
	{
		oneCanTake = false;
        // Run through moves to check for force take
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].getColour() == colour) {
                    board[row][col].getMoveLocations(board, true);
                }
            }
        }
        ArrayList<Point> myDestinations = getMoveLocations(board, true);
        myMoves.clear();
        // Create and add all possible moves
        for (int movesChecked = 0; movesChecked < myDestinations.size(); movesChecked++) {
            myMoves.add(new Move(this, myDestinations.get(movesChecked),
                    board[myDestinations.get(movesChecked).y][myDestinations.get(movesChecked).x]));
        }
	}

	/**
	   Returns the moves available to the piece
	   @return the moves for the piece
	 */
	public ArrayList<Move> getMoves()
	{
		return myMoves;
	}

	/**
	   Cloning piece
	   @return the newly created piece
	 */
	public Piece clonePiece()
	{
		Piece tempPiece;
		if (this instanceof Pawn)
			tempPiece = new Pawn(row, col, colour);
		else if (this instanceof Rook)
			tempPiece = new Rook(row, col, colour);
		else if (this instanceof Knight)
			tempPiece = new Knight(row, col, colour);
		else if (this instanceof Bishop)
			tempPiece = new Bishop(row, col, colour);
		else if (this instanceof Queen)
			tempPiece = new Queen(row, col, colour);
		else if (this instanceof King)
			tempPiece = new King(row, col, colour);
		else
			return null;
		tempPiece.changeCanTake(oneCanTake);
		return tempPiece;
	}


	/**
	   Promotes the pawn to the selected piece for Human, and find the best piece to promote for AI
	 */
	public void promote(Piece[][] board)
	{
		if (isAi) {
            Piece bestPiece = AIbestPromotion(board);
            board[row][col] = bestPiece;
        } else {
            String[] options = {"Queen", "Rook", "Bishop", "Knight", "King"};
            int response = JOptionPane.showOptionDialog(null, "Choose piece for promotion", "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            Piece newPiece;
            switch (response) {
                case 1:
                    newPiece = new Rook(row, col, colour);
                    break;
                case 2:
                    newPiece = new Bishop(row, col, colour);
                    break;
                case 3:
                    newPiece = new Knight(row, col, colour);
                    break;
                case 4:
                    newPiece = new King(row, col, colour);
                    break;
                case 5:
                    newPiece = new Queen(row, col, colour);
                    break;
                default:
                    newPiece = new Queen(row, col, colour); // Default to Queen if something goes wrong
                    break;
            }
            board[row][col] = newPiece;
        }
	}
	
	private Piece AIbestPromotion(Piece[][] board) {
        Piece[] possiblePromotions = {
            new Queen(row, col, colour),
            new Rook(row, col, colour),
            new Bishop(row, col, colour),
            new Knight(row, col, colour),
            new King(row, col, colour),
        };

        Piece bestPiece = possiblePromotions[0];
        int bestScore = Integer.MIN_VALUE;

        for (Piece piece : possiblePromotions) {
            board[row][col] = piece;
            int score = evaluateBoard(board); // Implement this method to evaluate the board
            if (score > bestScore) {
                bestScore = score;
                bestPiece = piece;
            }
        }

        return bestPiece;
    }
	
	 private int evaluateBoard(Piece[][] board) {
		// Use Move class's score calculation method
	        int score = 0;
	        for (int row = 0; row < 8; row++) {
	            for (int col = 0; col < 8; col++) {
	                if (board[row][col] != null) {
	                    ArrayList<Move> moves = board[row][col].getMoves();
	                    for (Move move : moves) {
	                        score += move.getScore();
	                    }
	                }
	            }
	        }
	        return score;
	    }

}