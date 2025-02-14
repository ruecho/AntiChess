
public class TranspositionEntry {
	
	 public static final int EXACT = 0;
	 public static final int LOWERBOUND = 1;
	 public static final int UPPERBOUND = 2;
	
	int score;     // The evaluation score of the board position
    int depth;     // The depth at which the board position was evaluated
    Move bestMove; // The best move found at this position
    int flag;       // Whether the entry is an exact value, a lower bound, or an upper bound

    // Constructor to initialize the entry
    public TranspositionEntry(int score, int depth, Move bestMove, int flag) {
        this.score = score;
        this.depth = depth;
        this.bestMove = bestMove;
        this.flag = flag;
    }

}


