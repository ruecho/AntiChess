import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JOptionPane;

public class AiPlayer extends Player {
    private int difficulty;
    private static final int INFINITY = 999999;
    private Map<Long, TranspositionEntry> transpositionTable = new HashMap<>(); // Transposition table to store evaluated positions
    private long[][] zobristTable; // Zobrist table for hashing board positions
    private Map<Long, Integer> positionCount = new HashMap<>(); // Tracks board position occurrences
    private long zobristHash; // Zobrist hash for the current board position

    private int fiftyMoveCounter = 0; // Counts moves since the last pawn move or capture
    private boolean gameOver = false;


    /**
     * Constructs the AiPlayer object
     * @param myName the name of the AI
     * @param myColour the color of the AI
     * @param myDifficulty the AI's difficulty
     */
    public AiPlayer(String myName, int myColour, int myDifficulty, Piece[][] board) {
        super(myName, myColour); // Initialize the superclass
        difficulty = myDifficulty; // Set the difficulty level
        initializeZobristTable(); // Initialize the Zobrist hashing table
        zobristHash = computeZobristHash(board); // Initialize with the actual board state

    }

    /**
     * Initializes the Zobrist table with random bitstrings.
     * This table will be used to generate unique hash values for board positions.
     */
    private void initializeZobristTable() {
        zobristTable = new long[64][12]; // 64 squares on the board, 12 piece types (6 pieces for each color)
        Random rand = new Random(); // Random number generator
        
        // Fill the Zobrist table with random bitstrings
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 12; j++) {
                zobristTable[i][j] = rand.nextLong(); // Assign a random 64-bit integer to each position-piece combination
            }
        }
    }

    /**
     * Generates a unique Zobrist hash for the current board position.
     * @param board the current board state
     * @return the Zobrist hash value for the board
     */
    private long computeZobristHash(Piece[][] board) {
        long hash = 0L; // Initialize the hash value

        // Iterate over the board and XOR the corresponding Zobrist values
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    int pieceIndex = getPieceIndex(piece); // Get the index of the piece type and color
                    hash ^= zobristTable[row * 8 + col][pieceIndex]; // XOR the hash with the Zobrist value
                }
            }
        }

        return hash; // Return the computed hash
    }

    /**
     * Maps a piece to a unique index based on its type and color.
     * @param piece the piece to be indexed
     * @return an index between 0 and 11
     */
    private int getPieceIndex(Piece piece) {
        // Map piece type and color to an index from 0 to 11
        // Example: white pawn=0, black pawn=6, white knight=1, black knight=7, etc.
        int typeIndex = piece.getType(); // Assume getType() returns an index for the piece type
        return typeIndex + (piece.getColour() == 1 ? 0 : 6); // Offset for black pieces
    }

    /**
     * Makes the best move found for this turn
     * @param board the current board
     * @param lastMove the last move made
     */
    public void makeMove(Piece[][] board, Point[] lastMove) {
    	if (gameOver) return;
        Move bestMove = selectMove(board); // Select the best move using alpha-beta pruning and transposition table
        if (bestMove == null) {
            return;
        }
        Point loc = bestMove.getDestination();
        bestMove.getPiece().isAi = true; // Set the AI flag for the piece
        bestMove.getPiece().move(loc, board); // Make the move on the board
        
        // Update the Zobrist hash for the move
        zobristHash ^= zobristTable[bestMove.getPiece().getLocation().y * 8 + bestMove.getPiece().getLocation().x][getPieceIndex(bestMove.getPiece())]; // Remove piece from original square
        if (board[loc.y][loc.x] != null) {
            zobristHash ^= zobristTable[loc.y * 8 + loc.x][getPieceIndex(board[loc.y][loc.x])]; // Remove captured piece
        }
        zobristHash ^= zobristTable[loc.y * 8 + loc.x][getPieceIndex(bestMove.getPiece())]; // Place piece in new square
        
        // Check for pawn promotion
        if (bestMove.getPiece() instanceof Pawn && (loc.y == 0 || loc.y == 7)) {
            bestMove.getPiece().promote(board);
        }
        
        boolean isPawnMove = bestMove.getPiece() instanceof Pawn;
        boolean isCapture = board[loc.y][loc.x] != null;
        if (isPawnMove || isCapture) {
            fiftyMoveCounter = 0; // Reset if a pawn move or capture occurs
        } else {
            fiftyMoveCounter++;
        }

        // Update position count for threefold repetition
        long currentHash = computeZobristHash(board);
        positionCount.put(currentHash, positionCount.getOrDefault(currentHash, 0) + 1);

        // Check for draw conditions
        if (isThreefoldRepetition(board) || isFiftyMoveDraw(board)) {
            JOptionPane.showMessageDialog(null, "Draw by threefold repetition or fifty-move rule.", "Game Draw", JOptionPane.INFORMATION_MESSAGE);
            gameOver = true; // Set the gameOver flag to true
            return;
        }
        
        lastMove[0] = loc; // Update the last move
    }

    /**
     * Looks through the possible moves for this turn and selects the best one
     * @param board the current board
     * @return the best move for the AI to make
     */
    public Move selectMove(Piece[][] board) {
    	if (gameOver) return null;
        ArrayList<Move> allMoves = getAllValidMoves(board); // Get all valid moves
        if (allMoves.isEmpty()) {
            tied = true; // Set the tied flag if no moves are available
            return null;
        }

        int overallBestScore = -INFINITY;
        Move overallBestMove = allMoves.get(0);

        // Evaluate each move using alpha-beta pruning
        for (Move move : allMoves) { // Iterate trough all possible moves
            Point originalLocation = move.getPiece().getLocation(); // Stores the original position of the piece that's being moved
            Piece capturedPiece = board[move.getDestination().y][move.getDestination().x]; // If there is an opponent's piece at the destination, stores it
            board[originalLocation.y][originalLocation.x] = null; // Removes the piece from its original square on the board by setting that square to null
            board[move.getDestination().y][move.getDestination().x] = move.getPiece(); // Dropping the piece at its new location
            move.getPiece().setLocation(move.getDestination().x, move.getDestination().y); // Updates the piece's internal position to reflect its new location after the move

         // Check for draw conditions after making the move
            if (isThreefoldRepetition(board) || isFiftyMoveDraw(board)) {
                JOptionPane.showMessageDialog(null, "Draw by threefold repetition or fifty-move rule.", "Game Draw", JOptionPane.INFORMATION_MESSAGE);
                gameOver = true; // Set the gameOver flag to true
                return null; // Return null if a draw condition is met
            }

            
            int score = -alphabeta(board, difficulty, -INFINITY, INFINITY, false); // score returned by alphabeta represents how favorable this position is for the AI

            // Undo the move
            board[move.getDestination().y][move.getDestination().x] = capturedPiece; // Restores the piece that was at the destination square before the move was made
            board[originalLocation.y][originalLocation.x] = move.getPiece(); // Returns the piece to its original square on the board
            move.getPiece().setLocation(originalLocation.x, originalLocation.y); // Updates the piece's internal position to reflect its original location
            
            // Update the Zobrist hash
            zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(move.getPiece())]; // Remove piece from destination square
            if (capturedPiece != null) {
                zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(capturedPiece)]; // Restore captured piece
            }
            zobristHash ^= zobristTable[originalLocation.y * 8 + originalLocation.x][getPieceIndex(move.getPiece())]; // Add piece back to original square
         

            if (score > overallBestScore) { // Checks if the score obtained from evaluating this move is better than the best score 
                overallBestScore = score; // Updates the best score
                overallBestMove = move; // Updates the best move
            }
        }

        return overallBestMove; // Return the best move found
    }
    
    private boolean isThreefoldRepetition(Piece[][] board) {
        long currentHash = computeZobristHash(board);
        return positionCount.getOrDefault(currentHash, 0) >= 3;
    }

    private boolean isFiftyMoveDraw(Piece[][] board) {
        return fiftyMoveCounter >= 50;
    }

    /**
     * Minimax algorithm with alpha-beta pruning and transposition table lookup
     * @param board the current board
     * @param depth the current depth of the search
     * @param alpha the alpha value
     * @param beta the beta value
     * @param isMaximizingPlayer whether the current player is maximizing or not
     * @return the score of the board
     */
    private int alphabeta(Piece[][] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (gameOver) return 0; // If the game is over, return a neutral score
        zobristHash = computeZobristHash(board); // Compute the hash for the current board position

        // Check if the position is already evaluated in the transposition table
        if (transpositionTable.containsKey(zobristHash)) {
            TranspositionEntry entry = transpositionTable.get(zobristHash);
            if (entry.depth >= depth) { // Use the stored score if the depth is sufficient
                if (entry.flag == TranspositionEntry.EXACT) {
                    return entry.score; // Exact score
                } else if (entry.flag == TranspositionEntry.LOWERBOUND) {
                    alpha = Math.max(alpha, entry.score);
                } else if (entry.flag == TranspositionEntry.UPPERBOUND) {
                    beta = Math.min(beta, entry.score);
                }
                if (alpha >= beta) {
                    return entry.score;
                }
            }
        }

        if (depth == 0 || gameOver(board)) { // Base case: maximum depth reached or game over
            int score = evaluateBoard(board);
            storeTranspositionEntry(zobristHash, score, depth, alpha, beta, null); // Store the result
            return score;
        }

        ArrayList<Move> validMoves = getAllValidMoves(board); // Get all valid moves for the current player

        int bestScore;
        Move bestMove = null; // Track the best move found
        if (isMaximizingPlayer) { // AI player
            bestScore = -INFINITY;
            for (Move move : validMoves) {
                Point originalLocation = move.getPiece().getLocation();
                Piece capturedPiece = board[move.getDestination().y][move.getDestination().x];
                board[originalLocation.y][originalLocation.x] = null;
                board[move.getDestination().y][move.getDestination().x] = move.getPiece();
                move.getPiece().setLocation(move.getDestination().x, move.getDestination().y);

                int eval = alphabeta(board, depth - 1, alpha, beta, false);

                // Undo the move
                board[move.getDestination().y][move.getDestination().x] = capturedPiece;
                board[originalLocation.y][originalLocation.x] = move.getPiece();
                move.getPiece().setLocation(originalLocation.x, originalLocation.y);
                
                // Update the Zobrist hash
                zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(move.getPiece())]; // Remove piece from destination square
                if (capturedPiece != null) {
                    zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(capturedPiece)]; // Restore captured piece
                }
                zobristHash ^= zobristTable[originalLocation.y * 8 + originalLocation.x][getPieceIndex(move.getPiece())]; // Add piece back to original square


                if (eval > bestScore) {
                    bestScore = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
        } else { // Opponent player
            bestScore = INFINITY;
            for (Move move : validMoves) {
                Point originalLocation = move.getPiece().getLocation();
                Piece capturedPiece = board[move.getDestination().y][move.getDestination().x];
                board[originalLocation.y][originalLocation.x] = null;
                board[move.getDestination().y][move.getDestination().x] = move.getPiece();
                move.getPiece().setLocation(move.getDestination().x, move.getDestination().y);

                int eval = alphabeta(board, depth - 1, alpha, beta, true);

                // Undo the move
                board[move.getDestination().y][move.getDestination().x] = capturedPiece;
                board[originalLocation.y][originalLocation.x] = move.getPiece();
                move.getPiece().setLocation(originalLocation.x, originalLocation.y);
                
                // Update the Zobrist hash
                zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(move.getPiece())]; // Remove piece from destination square
                if (capturedPiece != null) {
                    zobristHash ^= zobristTable[move.getDestination().y * 8 + move.getDestination().x][getPieceIndex(capturedPiece)]; // Restore captured piece
                }
                zobristHash ^= zobristTable[originalLocation.y * 8 + originalLocation.x][getPieceIndex(move.getPiece())]; // Add piece back to original square

                if (eval < bestScore) {
                    bestScore = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
        }

        storeTranspositionEntry(zobristHash, bestScore, depth, alpha, beta, bestMove); // Store the result in the table
        return bestScore;
    }
    
    // Helper method to store entries in the transposition table
    private void storeTranspositionEntry(long zobristHash, int score, int depth, int alpha, int beta, Move bestMove) {
        int flag;
        if (score <= alpha) flag = TranspositionEntry.UPPERBOUND;
        else if (score >= beta) flag = TranspositionEntry.LOWERBOUND;
        else flag = TranspositionEntry.EXACT;
        transpositionTable.put(zobristHash, new TranspositionEntry(score, depth, bestMove, flag));
    }


    private ArrayList<Move> getAllValidMoves(Piece[][] board) {
        ArrayList<Move> allMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].getColour() == colour) {
                    board[row][col].lookAhead(board, difficulty * 4);
                    allMoves.addAll(board[row][col].getMoves());
                }
            }
        }
        return allMoves;
    }

    public boolean gameOver(Piece[][] board) {
    	if (gameOver) {
            return true; // If the game is over, return true immediately
        }
        int whitePieces = 0;
        int blackPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null) {
                    if (board[row][col].getColour() == 1) {
                        whitePieces++;
                    } else {
                        blackPieces++;
                    }
                }
            }
        }

        // Check if either player has no pieces left
        if (whitePieces == 0 || blackPieces == 0) {
            return true;
        }

        // Check for stalemate
        boolean whiteHasMoves = hasLegalMoves(board, 1);
        boolean blackHasMoves = hasLegalMoves(board, 2);

        if (!whiteHasMoves || !blackHasMoves) {
            return true;
        }
        
        // Check for draw conditions
        if (isThreefoldRepetition(board) || isFiftyMoveDraw(board)) {
            gameOver = true;
            return true;
        }


        return false;
    }

    private boolean hasLegalMoves(Piece[][] board, int playerColour) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null && board[row][col].getColour() == playerColour) {
                    ArrayList<Point> moves = board[row][col].getMoveLocations(board, false);
                    if (!moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
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
