import java.util.Arrays;
import java.util.Random;

import com.sun.media.jfxmedia.logging.Logger;

import sun.rmi.runtime.Log;

// jas player implementing alpha beta pruning algorithm
public class jasPlayer {

	private static final int maxpositive = 1000;
	private static final int minnegative = -1000;
	private static final int maxDepth = 20;
	public static boolean AIFIRST = true;
	public static void main(String[] args) {
		System.out.println(read(args[0]));
	}
	
	public static boolean isLegalMove(int[] Move, int[][]board) {
		if (Move[0] > 4 || Move[0] < 0 || Move[1] >= board.length || Move[1] <= 0 || Move[2] >= board.length || Move[2] <= 0 || Move[3] >= board.length || Move[3] <= 0) {
			return false;
		}
		return true;
	}
	
	
	
	public static String read(String str) {
		String delimits = "\\[|\\]";
		
		String[] components = str.split(delimits);
		int j = 0;
		// remove spaces in string, empty elements are eliminated
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals("") == false) {
				components[j++] = components[i];
			}

		}
		String [] newArray = new String[j];
		newArray = Arrays.copyOf(components, j);
		int size = newArray.length - 1;
		// matrix construction
		int[][] matrix = new int[size][size];
		// change character into matrix
		for (int i = 0; i < size; i++) {
			for (int k = 0; k < newArray[i].length(); k++) {
				matrix[i][k] = Integer.parseInt(Character.toString(newArray[i].charAt(k))); // parse
																							// newArray
																							// string
																							// into
																							// integer
																							// into
																							// matrix
																							// construction.
			}
		}
		for (int i=0; i < size; i++) {
			for (int k=0 ; k < size - newArray[i].length(); k++) {
				matrix[i][k + newArray[i].length()] = 4;
			}
		}
		
		int[] playerMove = new int[4]; // setup a player move of last step
		// check if it's first move
		if (newArray[newArray.length-1].equals("LastPlay:null")) {
			// set each playerMove element as -1
			for (int i= 0; i < 3; i++) {
				playerMove[i] = 1; // for the first move
			}
			playerMove[3] = size;
		} else {
			// sample string : Last Play: (2, 2, 3, 2)
			String delimits2 = "\\(|\\)";
			String[] components2 = newArray[newArray.length - 1].split(delimits2);
			char [] components3 = components2[components2.length -1].toCharArray();
			// determine the length of array with comma eliminated
			int r = 0;
			for (int i=0; i < components3.length; i++) {
				if (Character.toString(components3[i]).equals(",")==false)
			          components3[r++] = components3[i];
			}
			char [] newArray2 = new char[r];
			System.arraycopy( components3, 0, newArray2, 0, r);
			// parse string array to integer of player steps
		      for (int i =0; i<playerMove.length; i++){
		          playerMove[i]=Integer.parseInt(Character.toString((newArray2[i])));  
		      }
		}
		// find best move through function moveGen
		String bestMove = moveGen(matrix, playerMove);
		return bestMove;
	}

	public static String moveGen(int[][] board, int[] lastMove) {
		/*
		 * First initialize the move as (0,0,0,0), find the possible available
		 * moves accrod to lastMove, initialize the val as minnegative
		 */
		int bestMove[] = {0,0,0,0}; // ? will this be taken as illegal?
		int[][] availables = availableMoves(board, lastMove);
		int max = minnegative; // assume I'm maxmizer, and AI is the minimizer,
								// so we first set up max as negative most.
		// iterate through each options and colors, check if it's a win or lose
		int value = max;
		for (int i = 0; i < availables[0].length; i++) {
			for (int c = 1; c <= 3; c++) { // iterate through each color
				// paint the board with cuurent color and store the position of
				// this move
				if (availables[0][i] < 99 || availables[1][i] < 99) { // means
																		// this
																		// position
																		// is
																		// available
					// paint the color now
					board[availables[0][i]][availables[1][i]] = c; // setup the
																	// color
					// now check if it's a loss or not
					int[] tempt = {c, (board.length - availables[0][i] - 1),(availables[1][i]), (board.length - (board.length - availables[0][i] - 1)  - (availables[1][i])) };
					if (isMovable(board)) { // check if the current board with
											// changed color is movable
						int x = board.length - availables[0][i] - 1;
						int y = availables[1][i];
						int z = board.length - x - y;
						int[] nextMove = { c, x, y, z }; // set up next move;
						// update the value of my nextMove
						value = Math.max(value, minMax(board, nextMove,1, minnegative, maxpositive));

						// check if I win
						if (elv(board, nextMove) == maxpositive) {
							bestMove[0] = c;
							bestMove[1] = x;
							bestMove[2] = y;
							bestMove[3] = z;
							// remove the color on that circle
							board[availables[0][i]][availables[1][i]] = 0;
						}

						// if value beyond max setup max as value
						if (value > max) {
							bestMove[0] = c;
							bestMove[1] = x;
							bestMove[2] = y;
							bestMove[3] = z;
							max = value;
						}
					}
					// reset the board of that position to 0
					board[availables[0][i]][availables[1][i]] = 0;
				}
			}
		}

		// convert bestMove to string format
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < bestMove.length; i++) {
			sb.append(bestMove[i] + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	/*
	 * Reference:
	 * http://stackoverflow.com/questions/27527090/finding-the-best-move-using-
	 * minmax-with-alpha-beta-pruning
	 */
	public static int minMax(int[][] board, int[] nextMove, int depth, int alpha, int beta) {
		// base condition to check if it has reached the bottom, or I win or
		// lose cannot move any longer
		if (depth == maxDepth || elv(board, nextMove) == minnegative || elv(board, nextMove) == maxpositive) {
			return elv(board, nextMove);
		}else if (depth % 2 == 0) { // it's AI's turn
				int[][] availables = availableMoves(board, nextMove);
				int value = minnegative; // first assume loss
				for (int i = 0; i < availables[0].length - 1; i++) { // for each
																		// option
					for (int c = 1; c <= 3; c++) { // for each color
						if (availables[0][i] < 100 || availables[1][i] < 100) { // if
																				// this
																				// position
																				// is
																				// valid
							board[availables[0][i]][availables[1][i]] = c;
							int[] tempt = {c, (board.length - availables[0][i] - 1),(availables[1][i]), (board.length - (board.length - availables[0][i] - 1)  - (availables[1][i])) };
							if (isMovable(board)) {
								// same as in move function, obtain x, y, z
								// coordinates
								int x = board.length - availables[0][i] - 1;
								int y = availables[1][i];
								int z = board.length - x - y;
								int[] followingMove = { c, x, y, z }; // setup the
																		// next
																		// possible
																		// move
								value = Math.max(value, minMax(board, followingMove, depth + 1, alpha, beta));
								// update alpha
								alpha = Math.max(alpha, value);
								// if alpha and beta overlaps we stop this routine
								if (alpha > beta) {
									// stop this subordinate
									board[availables[0][i]][availables[1][i]] = 0;
									return alpha;
								}
							}
							board[availables[0][i]][availables[1][i]] = 0; // reset
																			// the
																			// coordinate
																			// as 0
						}
					}
					return alpha;
				}
			} else { // my turn
				// now it's my turn
				int[][] availables = availableMoves(board, nextMove);
				int value = maxpositive; // first assume loss
				for (int i = 0; i < availables[0].length; i++) { // for each
																		// option
					for (int c = 1; c <= 3; c++) { // for each color
						if (availables[0][i] < 99 || availables[1][i] < 99) { // if
																				// this
																				// position
																				// is
																				// valid
							board[availables[0][i]][availables[1][i]] = c;
							int[] tempt = {c, (board.length - availables[0][i] - 1),(availables[1][i]), (board.length - (board.length - availables[0][i] - 1)  - (availables[1][i])) };
							if (isMovable(board)) {
								// same as in move function, obtain x, y, z
								// coordinates
								int x = board.length - availables[0][i] - 1;
								int y = availables[1][i];
								int z = board.length - x - y;
								int[] followingMove = { c, x, y, z }; // setup the
																		// next
																		// possible
																		// move
								value = Math.min(value, minMax(board, followingMove, depth + 1, alpha, beta));
								// update beta
								beta = Math.min(beta, value);
								// if alpha and beta overlaps we stop this routine
								if (alpha > beta) {
									// stop this subordinate
									board[availables[0][i]][availables[1][i]] = 0;
									return beta;
								}
							}
							board[availables[0][i]][availables[1][i]] = 0; // reset
																			// the
																			// coordinate
																			// as 0
						}
					}
					return beta;
				}
			}
		return -1; // if neither works
}

	/*
	 * This function checks if we lose per last move, first we will check the
	 * left diag, right diag if left diagnol is line is 2, or right diagnal is 1
	 * or the bottom line is 3. if any of those happens, we lose immediately
	 */
	public static int elv(int[][] board, int[] lastMove) {
		int counter = 1;
		// check first the right diagnol
		for (int i = 1; i < board.length - 1; i++) {
			if (board[i][counter] == 1) {
				return minnegative;
			}
			counter++;
		}
		// check second the left diagonol
		for (int i = 1; i < board.length - 1; i++) {
			if (board[i][1] == 2) {
				return minnegative;
			}
		}
		// check the bottom line cannot be 3
		for (int j = 1; j < board[board.length - 2].length; j++) {
			if (board[board.length - 2][j] == 3) {
				return minnegative;
			}
		}

		/*
		 * Scan the board to check if you lose, setup a variable to adjust the
		 * column index so we can scan around different direction of current
		 * board[i][j], using right top, right bottom, left top, left bottom
		 * four directions.
		 */
		int changingCol = 3; // limit of this need to change simultaneously
								// according to row index
		for (int i = 1; i < board.length - 1; i++) {
			for (int j = 1; j < changingCol-1; j++) {
				for (int k = 1; k < 4; k++) {
					int x = 0;
					int y = 0;
					if (k == 1) { // here need to setup the current board[i][j]
									// color and its surrounding color so as to
									// check lose or not
						x = 2;
						y = 3;
					} else if (k == 2) {
						x = 1;
						y = 3;
					} else if (k == 3) {
						x = 1;
						y = 2;
					}

					// check around four directions
					if (board[i][j] == k) {
						// checks around the right top corner
						if ((board[i - 1][j] == x && board[i][j + 1] == y)
								|| (board[i - 1][j] == y && board[i][j + 1] == x)) {
							return minnegative;
						}
						// checks around the right bottom corner
						if ((board[i][j + 1] == x && board[i + 1][j + 1] == y)
								|| (board[i][j + 1] == y && board[i + 1][j + 1] == x)) {
							return minnegative;
						}
						// checks around the left top corner
						if ((board[i - 1][j - 1] == x && board[i][j - 1] == y)
								|| (board[i - 1][j - 1] == y && board[i][j - 1] == x)) {
							return minnegative;
						}
						// checks around the left bottom corner
						if ((board[i][j - 1] == x && board[i + 1][j] == y)
								|| (board[i][j - 1] == y && board[i + 1][j] == x)) {
							return minnegative;
						}
						// checks around the bottom corner
						if ((board[i + 1][j + 1] == x && board[i + 1][j] == y)
								|| (board[i + 1][j + 1] == y && board[i + 1][j] == x)) {
							return minnegative;
						}
						
						//Check the bottom right corner. 
			            if ((board[i+1][j+1]==x && board[i][j+1]==y) || (board[i+1][j+1]==y && board[i][j+1]==x)  ){
			              return minnegative;
			            }

					}

				}

			}
			changingCol++;

		}

		// get position of last move, notice that the index are based on defined
		// rule
		int lastX = board.length - lastMove[1] - 1; // convert it to top to
													// bottom coordinate, left
													// to right coordinate
		int lastY = lastMove[2];
		int score = 0;
		// evaluate the score based on options it can fill, higher the score
		// when the options are greater
		if (lastY != -1) {
			// above one check
			if (board[lastX + 1][lastY] != 0) {
				score++;
			}
			// below one check
			if (board[lastX - 1][lastY] != 0) {
				score++;
			}
			// right one check
			if (board[lastX][lastY + 1] != 0) {
				score++;
			}
			// left one check
			if (board[lastX][lastY - 1] != 0) {
				score++;
			}
			// right below check
			if (board[lastX + 1][lastY + 1] != 0) {
				score++;
			}
			// left above check
			if (board[lastX - 1][lastY - 1] != 0) {
				score++;
			}

		}
		return score;
	}

	/*
	 * According to lastMove find all possible moves on board, and check the
	 * surrounding to check if they are available, if not please try to check
	 * the next spot
	 */
	public static int[][] availableMoves(int[][] board, int[] lastMove) {

		if (lastMove[2] != -1) { // check if the lastMove has previous move
			// here record 0: right top, 1: left top, 2: right, 3: left, 4: bot
			// right, 5: bot left
			int[][] available = new int[2][6]; // represents int[0,1 represents
												// X
			// and Y directional index][the
			// surrounding neighbors], if not
			// available set them as 100
			int lastX = board.length - lastMove[1] - 1;
			int lastY = lastMove[2];
			if (board[lastX + 1][lastY] == 0) { // if right top is available
				available[0][0] = lastX + 1;
				available[1][0] = lastY;
			} else { // if right top is not available
				available[0][0] = 100;
				available[1][0] = 100;
			}

			if (board[lastX - 1][lastY - 1] == 0) { // if left top is available
				available[0][1] = lastX - 1;
				available[1][1] = lastY - 1;
			} else {
				available[0][1] = 100;
				available[1][1] = 100;
			}

			if (board[lastX][lastY + 1] == 0) { // if right is available
				available[0][2] = lastX;
				available[1][2] = lastY + 1;
			} else {
				available[0][2] = 100;
				available[1][2] = 100;
			}

			if (board[lastX][lastY - 1] == 0) { // if left is available
				available[0][3] = lastX;
				available[1][3] = lastY - 1;
			} else {
				available[0][3] = 100;
				available[1][3] = 100;
			}

			if (board[lastX + 1][lastY + 1] == 0) { // if bottom right is
													// available
				available[0][4] = lastX + 1;
				available[1][4] = lastY + 1;
			} else {
				available[0][4] = 100;
				available[1][4] = 100;
			}

			if (board[lastX - 1][lastY] == 0) { // if bottom left is available
				available[0][5] = lastX - 1;
				available[1][5] = lastY;
			} else {
				available[0][5] = 100;
				available[1][5] = 100;
			}
			// if none of neighboring is available, then try to find another
			// available spot
			if (available[0][0] == 100 && available[0][1] == 100 && available[0][2] == 100 && available[0][3] == 100
					&& available[0][4] == 100 && available[0][5] == 100) {
				// none available, then check another
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[i].length; j++) {
						if (board[i][j] == 0) {
							// record the index of move
							available[0][0] = i;
							available[1][0] = j;
						}
					}

				}

			}
			return available;
		} else {
			// if lastMove is a first move, or there is a invalid move
			Random r = new Random();
			int[][] available = new int[2][1];
			boolean check = false;
			while (!check) {
				available[0][0] = r.nextInt((board.length - 1));
				available[1][0] = r.nextInt((board.length - 1));
				if (board[available[0][0]][available[1][0]] == 0) { // randomly pick up a selectable slot
					check = true;
				}
			}
			return available;
		}

	}

	// check if the board is movable
	public static boolean isMovable(int[][] board) {
		int[] test = { -1, -1, -1, -1 };
		if (elv(board, test) == minnegative) {
			return false;
		}
		return true;
	}

}
