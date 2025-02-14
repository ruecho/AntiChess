import java.awt.Point;

public class Player
{
        private String name;    
        protected int numPieces; // Number of pieces that player has 
        protected int colour;    // white: 1, black: 0
        static protected boolean tied;
        
        /**
           Constructs a new player object
           @param myName
           @param myColour
         */
        public Player(String myName, int myColour)
        {
                // Default number of pieces
                numPieces = 16;
                name = myName;
                colour = myColour;
                tied = false;
        }
        
        /**
           Returns the name of the player
           @return the name of this player
         */
        public String getName()
        {
                return name;
        }

        /**
           Used by the AI to move its pieces
           @param board the board to move on
           @param lastMove the last move that was made
         */
        public void makeMove(Piece[][] board, Point[] lastMove)
        {
                // Will be overwritten by AIPlayer
                
        }
}
