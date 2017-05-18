
public class Move {
		int x;
		int y;
		int z;
		int color;
		int score;

		/**
		 * 
		 * @param int
		 *            x
		 * @param int
		 *            y
		 * @param int
		 *            z
		 * @param int
		 *            score, positive for maximizer, negative for minimizer,
		 *            neural as 0
		 */
		/**
		 * @param int
		 *            x, int y, int z, and Constructor for a non-node Move, with
		 *            only color and coordinates
		 */
		public Move(int color, int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.color = color;
		}

		/**
		 * @param int
		 *            score dummy node for only very first move
		 */
		public Move(int score) {
			this.score = score;
		}

		/**
		 * Move clone
		 * 
		 * @param Move
		 *            m, clone of last move
		 */
		public Move(Move m) {
			this.color = getColor(m);
			this.x = getX(m);
			this.y = getY(m);
			this.z = getZ(m);
			this.score = getScore(m);
		}

		/**
		 * Calculates the score of this Move by calling getMaxMinMove and taking
		 * on the best score found. If this is a frontier node (the maximum
		 * search depth has been reached), then score is assigned 0 to indicate
		 * that not enough information was found to assign a meaningful score.
		 * 
		 * @param State
		 *            board
		 * @param int
		 *            curDepth
		 * @param boolean
		 *            isMax
		 * @return score of current Move this
		 */
		public int calScore(State board, int curDepth, boolean ifMaximizer) {
			if (curDepth > 0) {
				State newBoard = new State(board, this); // construct a new
															// state
				Move bestMove = newBoard.getMinMaxMove(curDepth-1, ifMaximizer);
				return bestMove.score;
			} else {
				return 0;
			}

		}

		/**
		 * 
		 * @param m
		 * @return
		 */
		public int getX(Move m) {
			return m.x;

		}

		/**
		 * 
		 * @param m
		 * @return
		 */
		public int getY(Move m) {
			return m.y;
		}

		/**
		 * 
		 * @param m
		 * @return
		 */
		public int getZ(Move m) {
			return m.z;
		}

		/**
		 * 
		 * @param m
		 * @return
		 */
		public int getScore(Move m) {
			return m.score;
		}

		/**
		 * 
		 * @param m
		 * @return
		 */
		public int getColor(Move m) {
			return m.color;
		}

		public void setColor(int c) {
			this.color = c;
		}

		/**
		 * toString of this move
		 * 
		 * @return String string representation of this move , e.g. (a,b,c,d)
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder("(");
			sb.append(String.valueOf(this.color) + "," + String.valueOf(this.x) + "," + String.valueOf(this.y) + ","
					+ String.valueOf(this.z));
			sb.append(")");
			return sb.toString();
		}

	}