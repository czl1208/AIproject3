import java.util.ArrayList;

public class State {

	int size;
	Move lastMove;
	int[][] board;
	/**
	 * The colors defined as values
	 */
	public static final int UNCOLORED = 0;
	public static final int RED = 1;
	public static final int BLUE = 2;
	public static final int GREEN = 3;

	/**
	 * Possible scores defined as words
	 * 
	 * @param FREEMOVE
	 *            This Minimax algorithm detects if no neighboring positions are
	 *            open, and rewards the max/minimizer if either can get the
	 *            "free" move
	 */
	public static final int FREEMOVE = 5;
	public static final int LOSE = -10;

	/**
	 * Other
	 * 
	 * @param ENDGAMETACTIC
	 *            Once this many total uncolored circles remain on the playable
	 *            board, return FREEMOVE rather than examining the possible
	 *            moves
	 * 
	 * @param MAXDEPTH
	 *            The maximum search depth of the Depth Limited algorithm
	 */
	public static final int MAXDEPTH = 7;
	public static final int ENDINGTACTIC = 6;

	/**
	 * Parse of the state string including board size, last step, and int[][]
	 * board
	 * 
	 * @param string
	 */

	public State(String arg) {
		String[] rows = arg.split("]");
		this.size = rows.length - 2 - 1; // it has last play
		this.board = new int[size + 2][size + 2]; // construct a new board
		for (int i = 0; i < rows.length - 1; i++) {
			String row = rows[i];
			row = row.substring(1, row.length());
			for (int j = 0; j < row.length(); j++) {
				// construct a board from bottom to top
				if (i != rows.length - 2) { // not the top row
					board[size + 1 - i][j] = Integer.parseInt(row.substring(j, j + 1));
				} else {
					board[size + 1 - i][j + 1] = Integer.parseInt(row.substring(j, j + 1));
				}

			}
		}
		// construct lastMove
		String lMove = rows[rows.length - 1];
		int idx = lMove.indexOf('(');
		if (idx >= 0) {
			lMove = lMove.substring(idx);
			lMove = lMove.substring(1);
			lMove = lMove.substring(0, lMove.length() - 1);
			String[] steps = lMove.split(",");
			int[] step = new int[4];
			for (int i = 0; i < step.length; i++) {
				step[i] = Integer.parseInt(steps[i]);
			}
			this.lastMove = new Move(step[0], step[1], step[2], step[3]);
		} else {
			this.lastMove = null;
		}
	}

	/**
	 * Constructor for a hypothetical State, after a move has been made
	 */
	public State(State lastState, Move lastMove) {
		this.board = lastState.getBoard();
		this.size = lastState.getSize();
		if (board[lastMove.x][lastMove.y] == 0) { // movable
			board[lastMove.x][lastMove.y] = lastMove.color;
		} else {

		}
		this.lastMove = lastMove; // update lastMove record
	}

	/**
	 * Accessor methods
	 */
	public int getColor(int x, int y) {
		return this.board[x][y];
	}

	public Move getLastMove() {
		return lastMove;
	}

	/**
	 * get size of state board
	 * 
	 * @return
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Get the board of game
	 * 
	 * @return
	 */
	public int[][] getBoard() {
		return this.board;
	}

	/**
	 * Get best move according to current depth and if current step is maximizer
	 * or minimizer
	 * 
	 * @param curDepth
	 * @param ifMaxmizer
	 * @return Move
	 */
	/**
	 * Methods for the Minimax algorithm
	 */

	/**
	 * For the given State, return the Move with the best or the worst score,
	 * depending on if it is the Maximizer's or Minimizer's turn
	 */
	public Move getMinMaxMove(int curDepth, boolean ifMaxmizer) {
		if (this.lastMove == null) { // if this is the first move
			return new Move(1, 1, 1, this.size);
		}
		ArrayList<Move> neighbors = constructNeighbors();
		// If no neighbor circle is uncolored
		if (neighbors == null) {
			// If the remaining uncolored circles on the board is too high,
			// return a dummy Move with only a score to save computation
			// time
			//
			// If this is the original getMaxMinMove call, a dummy Move
			// should
			// NOT be returned, so depthCount must be less than the original
			if (cntAvailables() > ENDINGTACTIC && curDepth < MAXDEPTH) {
				// If this depth is a maximizer, then the maximizer will
				// receive the free move, and thus the score is positive
				if (ifMaxmizer) {
					return new Move(FREEMOVE);
				} else {
					// If this depth is minimizer, score is negative
					return new Move(-FREEMOVE);
				}

			} else {
				// If few enough free circles are left, consider
				// the possible "free" moves, and responses
				neighbors = cstrAvailables();
				// If this is the original method call, set a low depthCount
				// so
				// that the computation time doesn't explode from trying
				// tons
				// of Moves
				if (cntAvailables() > ENDINGTACTIC) {
					curDepth = 2;
				}
			}
		}

		// If all moves are losing moves, return a dummy Move with only a
		// score
		// that is positive for the Minimizer and negative for the
		// Maximizer, so
		// that it will be avoided
		//
		// If this is the original getMaxMinMove call, a dummy Move should
		// NOT be returned, so depthCount must be less than the original
		if (neighbors.isEmpty()) {
			if (curDepth < MAXDEPTH) {
				if (ifMaxmizer) {
					return new Move(LOSE);
				} else {
					return new Move(-LOSE);
				}
			} else {
				Move loseMove = constructLegalMove();
				return loseMove;
			}

		}

		// Look at each allowed Move, get their scores, and choose the max
		// or
		// the min value
		Move bestMove = new Move(100);
		if (ifMaxmizer) {
			bestMove = new Move(-100);
		}
		for (Move eachmove : neighbors) {
			eachmove.score = eachmove.calScore(this, curDepth, ifMaxmizer);
			if (ifMaxmizer) {
				if (eachmove.score > bestMove.score) {
					bestMove = new Move(eachmove);
				}

			} else {
				if (eachmove.score < bestMove.score) {
					bestMove = new Move(eachmove);
				}

			}

			// To randomize moves in the case that no move is preferred,
			// each
			// considered move has a 10% probability of overriding the
			// previous
			// move.
			if (bestMove.score == eachmove.score) {
				if (Math.random() < 0.1) {
					bestMove = new Move(eachmove);
				}
			}
		}
		return bestMove;
	}

	/**
	 * Returns the potential Moves a player could make that would not
	 * immediately lose
	 */
	public ArrayList<Move> constructNeighbors() {
		// a neighbor has 6 moves surrounding
		Move[] Neighbors = new Move[6];
		int x = lastMove.x;
		int y = lastMove.y;
		int z = lastMove.z;
		// neighborCircles contains the circles neighboring to the last
		// move,
		// starting with the circle to the upper right and rotating
		// clockwise.
		Neighbors[0] = new Move(board[x + 1][y], x + 1, y, z - 1);
		Neighbors[1] = new Move(board[x][y + 1], x, y + 1, z - 1);
		Neighbors[2] = new Move(board[x - 1][y + 1], x - 1, y + 1, z);
		Neighbors[3] = new Move(board[x - 1][y], x - 1, y, z + 1);
		Neighbors[4] = new Move(board[x][y - 1], x, y - 1, z + 1);
		Neighbors[5] = new Move(board[x + 1][y - 1], x + 1, y - 1, z);

		// Declare variables outside of for loop to prevent repetitive
		// declaration.
		int[] colorsSurrounding = new int[7];
		boolean[] badColors;
		int color1;
		int color2;
		ArrayList<Move> allowedMoves = new ArrayList<Move>();
		Move allowedMove;
		boolean aNeighborIsFree = false;

		for (Move aneighbor : Neighbors) {
			if (aneighbor.color == 0) {
				aNeighborIsFree = true;
				x = aneighbor.x;
				y = aneighbor.y;
				z = aneighbor.z;
				// colorsSurrounding is an int[] of the colors around
				// neighbor,
				// in the same rotation order as neighborCircles.
				// The first color is added again at the end to allow a
				// comparison of the last and first colors.
				colorsSurrounding[0] = board[x + 1][y];
				colorsSurrounding[1] = board[x][y + 1];
				colorsSurrounding[2] = board[x - 1][y + 1];
				colorsSurrounding[3] = board[x - 1][y];
				colorsSurrounding[4] = board[x][y - 1];
				colorsSurrounding[5] = board[x + 1][y - 1];
				colorsSurrounding[6] = colorsSurrounding[0];

				// badColors has a boolean element for each color, 1 2 or 3.
				// An element is set to true if setting that color in the
				// position specified by neighbor would be a losing move.
				// Booleans initialize to false.
				badColors = new boolean[3];
				for (int i = 0; i < 6; i++) {
					color1 = colorsSurrounding[i];
					color2 = colorsSurrounding[i + 1];
					if (color1 != 0 && color2 != 0 && color1 != color2) {
						badColors[6 - color1 - color2 - 1] = true; // this
																	// will
																	// generate
																	// a
																	// losing
																	// move
					}

				}

				for (int i = 0; i < 3; i++) {
					if (!badColors[i]) {
						allowedMove = new Move(i + 1, x, y, z);
						allowedMoves.add(allowedMove);
					}
				}
			}

		}

		// Catch the case when no neighbor circle is uncolored
		if (!aNeighborIsFree) {
			return null;
		}
		return allowedMoves;
	}

	/**
	 * Returns the number of uncolored circles on the playable board
	 */
	public int cntAvailables() {
		int numFreeCircles = 0;
		for (int i = size; i > 0; i--) {
			for (int j = 1; j < size - i + 2; j++) {
				if (board[i][j] == 0) {
					numFreeCircles++;
				}
			}
		}
		return numFreeCircles;
	}

	public ArrayList<Move> cstrAvailables() {
		// Declare variables outside of for loop to prevent repetitive
		// declaration.
		int[] colorsSurrounding = new int[7];
		boolean[] badColors;
		int color1;
		int color2;
		Move freeCircle;
		ArrayList<Move> freeCircles = new ArrayList<Move>();

		for (int i = size; i > 0; i--) {
			for (int j = 1; j < size - i + 2; j++) {
				if (board[i][j] == 0) {
					int x = i;
					int y = j;
					int z = size + 2 - i - j;

					// colorsSurrounding is an int[] of the colors around
					// neighbor,
					// in the same rotation order as neighborCircles.
					// The first color is added again at the end to allow a
					// comparison of the last and first colors.
					colorsSurrounding[0] = board[x + 1][y];
					colorsSurrounding[1] = board[x][y + 1];
					colorsSurrounding[2] = board[x - 1][y + 1];
					colorsSurrounding[3] = board[x - 1][y];
					colorsSurrounding[4] = board[x][y - 1];
					colorsSurrounding[5] = board[x + 1][y - 1];
					colorsSurrounding[6] = colorsSurrounding[0];
					// badColors has a boolean element for each color, 1 2
					// or 3.
					// An element is set to true if setting that color in
					// the
					// position specified by neighbor would be a losing
					// move.
					// Booleans initialize to false.
					badColors = new boolean[3];

					for (int k = 0; k < 6; k++) {
						color1 = colorsSurrounding[k];
						color2 = colorsSurrounding[k + 1];
						if (color1 != 0 && color2 != 0 && color1 != color2) {
							badColors[6 - color1 - color2 - 1] = true;
						}
					}

					for (int k = 0; k < 3; k++) {
						if (!badColors[k]) {
							freeCircle = new Move(k + 1, x, y, z);
							freeCircles.add(freeCircle);
						}
					}

				}
			}
		}
		return freeCircles;
	}

	/**
	 * Returns some legal move, i.e. any move that is adjacent to lastMove and
	 * uncolored. This method is only used when defeat is unavoidable.
	 */
	public Move constructLegalMove() {
		Move[] neighbors = new Move[6];
		int x = lastMove.x;
		int y = lastMove.y;
		int z = lastMove.z;
		// neighborCircles contains the circles neighboring to the last
		// move,
		// starting with the circle to the upper right and rotating
		// clockwise.
		neighbors[0] = new Move(board[x + 1][y], x + 1, y, z - 1);
		neighbors[1] = new Move(board[x][y + 1], x, y + 1, z - 1);
		neighbors[2] = new Move(board[x - 1][y + 1], x - 1, y + 1, z);
		neighbors[3] = new Move(board[x - 1][y], x - 1, y, z + 1);
		neighbors[4] = new Move(board[x][y - 1], x, y - 1, z + 1);
		neighbors[5] = new Move(board[x + 1][y - 1], x + 1, y - 1, z);
		for (Move aneighbor : neighbors) {
			if (aneighbor.color == 0) {
				aneighbor.setColor(1);
				return aneighbor;
			}
		}
		// If there are no uncolored neighboring circles, search for an
		// uncolored
		// circle anywhere on the board
		for (int i = size; i > 0; i--) {
			for (int j = 1; j < size - i + 2; j++) {
				if (board[i][j] == 0) {
					return new Move(1, i, j, size + 2 - i - j);
				}
			}
		}
		return null;
	}

	/**
	 * For testing
	 */

	public static void main(String[] args) {
		ArrayList<Move> moves = new ArrayList<Move>();
		System.out.println(moves);
		moves = null;
	}
}
