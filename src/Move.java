
import java.awt.Point;
import java.util.ArrayList;


public class Move {

	private Piece piece;

	private Point moveTo;

	private Piece capturedPiece;

	/**
	   Constructs the Move object 
	   @param myPiece      the piece making the move
	   @param location     the location piece is moving to
	   @param otherPiece   the piece (if any) that exist at the target location       
	 */
	public Move(Piece myPiece, Point location, Piece otherPiece)
	{
		piece = myPiece;
		moveTo = location;
		capturedPiece = otherPiece;
	}
	
	 public Piece getCapturedPiece() { 
	        return capturedPiece;
	    }

	/**
	   Gets the destination of the piece
	   @return the destination of the piece
	 */
	public Point getDestination()
	{
		return moveTo;
	}

	/**
	   Gets the piece that's making the move
	   @return the piece that's going to move
	 */
	public Piece getPiece()
	{
		return piece;
	}

	
	/**
	   Gives the score for the piece that is being taken
	   @param  toTake the piece that is being captured
	   @return the score of the captured piece
	 */
	public int takeScore(Piece toTake)
	{
		 int takeScore = 0; // This variable will hold the score value of the piece being taken
	        // Check if piece is being taken
	        if (toTake != null) {
	            // Check score for taking piece
	            // Higher scores are better
	            if (toTake instanceof Pawn) {
	                takeScore = -3;
	            } else if (toTake instanceof Knight || toTake instanceof Bishop) {
	                takeScore = 2;
	            } else if (toTake instanceof Queen) {
	                takeScore = -2;
	            } else if (toTake instanceof Rook) {
	                takeScore = 3;
	            } else if (toTake instanceof King) {
	                takeScore = 4;
	            }
	        }
	        return takeScore;
	    }
	
	public int getScore() {
		return takeScore(this.capturedPiece);
	}

}
