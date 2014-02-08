/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

	public final static int QUIT = 0;
	public final static int ADD = 1;
	public final static int STEP = 2;

	public final static int EMPTY = 2;
	public final static int WHITE = 1;
	public final static int BLACK = 0;

	Board gameBoard = new Board();

	private int machinePlayerColor;
	private int oppColor;
	private int searchDepth;

  // Creates a machine player with the given machinePlayerColor.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
		this(color, 0);
	}

  // Creates a machine player with the given machinePlayerColor and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
		this.machinePlayerColor = color;
		this.oppColor = (color + 1) % 2;
		this.searchDepth = searchDepth;
	}

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() { 
		Best bestMove = gameTreeSearch(machinePlayerColor, -2.0, 2.0, 1, searchDepth);
		Move m = bestMove.move;
		updateGameBoard(m, machinePlayerColor);
    return m;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
  	if(gameBoard.isValid(m, oppColor)){
  		updateGameBoard(m, oppColor);
  		return true;
  	}
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
  	if(gameBoard.isValid(m, machinePlayerColor)){
  		updateGameBoard(m, machinePlayerColor);
  		return true;
  	}
    return false;
  }

	public void updateGameBoard(Move m, int player) {
		
		// for debugging purposes
		if (m == null) {
			System.out.println("Found null move!");
		}
		// end
		
		System.out.println("Player: " + player + "  Move: (" + m.x1 + "," + m.x2 + ")");
	  if (player == WHITE) {
			gameBoard.addWhite(m.x1, m.y1);
			if (m.moveKind == STEP) {
				gameBoard.removeChip(m.x2, m.y2, player);
			}
		} else {
			gameBoard.addBlack(m.x1, m.y1);
			if (m.moveKind == STEP) {
				gameBoard.removeChip(m.x2, m.y2, player);
			}
		}
	}
	
	public void undoMove(Move m, int player) {
		if (player == WHITE) {
			gameBoard.removeChip(m.x1, m.y1, player);
			if (m.moveKind == STEP) {
				gameBoard.addWhite(m.x2, m.y2);
			}
		} else {
			gameBoard.removeChip(m.x1, m.y1, player);
			if (m.moveKind == STEP) {
				gameBoard.addBlack(m.x2, m.y2);
			}
		}
	}

	public Best gameTreeSearch(int currPlayer, double alpha, double beta, int depth, int maxDepth) {
		Best myBest = new Best();
		Best reply;
		
		// Base cases
		if (gameBoard.isNetwork(machinePlayerColor)) {
			System.out.println("Found isNetwork(machinePlayerColor)" );
			return new Best(null, (1.0 / (double)depth));
		} 
		if (gameBoard.isNetwork(oppColor)) {
			System.out.println("Found isNetwork(oppColor)");
			return new Best(null, (-1.0 / (double)depth));
		}
		if (depth == maxDepth) {
			System.out.println("HIT MAX DEPTH");
			return new Best(null, (gameBoard.evaluatorFcn(machinePlayerColor) / (double)depth));
		}
		
		// set worstcase scores
		if (currPlayer == machinePlayerColor) { 
			myBest.score = Integer.MIN_VALUE;
		} else {
			myBest.score = Integer.MAX_VALUE;
		}
		
		// create array of valid moves
		Move[] moves = gameBoard.validMoves(currPlayer);
		// for debugging
		int debCounter = 0;
		for (Move m: moves) {
			if (m != null) {
				debCounter++;
			}
		}
		
		// search through moves
		for (Move m: moves) {
			if (m != null) {
				updateGameBoard(m, currPlayer);
				reply = gameTreeSearch(((currPlayer + 1) % 2), alpha, beta, depth + 1, maxDepth);
				undoMove(m, currPlayer);
				
				if ((currPlayer == machinePlayerColor) && (reply.score >= myBest.score)) {
					myBest.move = m;
					myBest.score = reply.score;
					alpha = reply.score;
				} else if (( currPlayer == oppColor) && (reply.score <= myBest.score)) {
					myBest.move = m;
					myBest.score = reply.score;
					beta = reply.score;
				}
				
				// alpha beta pruning
				if (alpha >= beta) {
					return myBest;
				} 
			}
		}
		return myBest;
	}
			
	public static void main(String[] args) {
		MachinePlayer M = new MachinePlayer(WHITE);
		Move c1 = new Move(0, 0);
		Move c2 = new Move(0, 7);
		Move c3 = new Move(7, 0);
		Move c4 = new Move(7, 7);
		
		//Goals
		Move bg1 = new Move(6, 7);
		Move bg2 = new Move(1, 7);
		Move bg3 = new Move(1, 0);
		Move bg4 = new Move(6, 0);

		Move wg1 = new Move(0, 1);
		Move wg2 = new Move(0, 6);
		Move wg3 = new Move(7, 1);
		Move wg4 = new Move(7, 6);
		Move wg5 = new Move(5,5);
		Move wg6 = new Move(6, 6);
		Move wg44 = new Move(4, 4);
		
		
		//moveKind == STEP
		Move s1 = new Move(6,6,0,1);
		Move s2 = new Move(5,4, 7, 1);
		Move s3 = new Move( 4, 4, 7, 6);
		Move s4 = new Move(7, 4, 0, 6);
		
		System.out.println("Empty board: ");
		System.out.println(M.gameBoard);
		System.out.println("Force move into corners : ");
		System.out.println("Place chip in corner 1: " + M.forceMove(c1));
		System.out.println("Place chip in corner 2: " + M.forceMove(c2));
		System.out.println("Place chip in corner 3: " + M.forceMove(c3));
		System.out.println("Place chip in corner 4: " + M.forceMove(c4));
		System.out.println("Place W @ position (0, 1): " + M.forceMove(wg1));
		System.out.println("Place W @ position (0, 6): " + M.forceMove(wg2));
		System.out.println("Place W @ position (7, 1): " + M.forceMove(wg3));
		System.out.println("Place W @ position (7, 6): " + M.forceMove(wg4));
		System.out.println("Place W @ position (5, 5): " + M.forceMove(wg5));
		System.out.println(M.gameBoard);
		System.out.println("Place W @ position (6, 6): " + M.forceMove(wg6));
		System.out.println(M.gameBoard);
		System.out.println("Move W from (0,1) --> (6, 6): " + M.forceMove(s1));
		System.out.println(M.gameBoard);
		System.out.println("Move W from (7,6) --> (4, 4): " + M.forceMove(s3));		
		System.out.println(M.gameBoard);
		System.out.println("Move W from (7,1) --> (5, 4): " + M.forceMove(s2));
		System.out.println(M.gameBoard);
		
		Move wg7 = new Move(1, 1);
		System.out.println("Place W @ position (1, 1): " + M.forceMove(wg7));
		System.out.println(M.gameBoard);
		
		wg7 = new Move(1, 2);
		System.out.println("Place W @ position (1, 2): " + M.forceMove(wg7));
		System.out.println(M.gameBoard);
		
		wg7 = new Move(2, 1);
		System.out.println("Place W @ position (2, 1): " + M.forceMove(wg7));
		System.out.println(M.gameBoard);
		
		wg7 = new Move(3, 1);
		System.out.println("Place W @ position (3, 1): " + M.forceMove(wg7));
		System.out.println(M.gameBoard);
		
		wg7 = new Move(5, 7);
		System.out.println("Place W @ position (3, 1): " + M.forceMove(wg7));
		System.out.println(M.gameBoard);
		

	}
}
