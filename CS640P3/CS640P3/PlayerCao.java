
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/*
 * We design a data structure which contains the value of the move,
 * and the information of the circle. 
 */

class ValueCircle {
	AtroposCircle circle;
	int val;

	public ValueCircle(int val) {
		this.circle = null; // store the circle information
		this.val = val; // the value of the move
	}

	// construct the ValueCircle by the given circle and the value of the
	// circle.
	public ValueCircle(AtroposCircle circle, int val) {
		this.circle = circle;
		this.val = val;
	}

	public ValueCircle clone() { // Make a copy of the valueCircle
		AtroposCircle thiscircle = this.circle.clone();
		int thisval = this.val;
		return new ValueCircle(thiscircle, thisval);
	}
}

public class PlayerCao {

	public static final int RED = 1;
	// value of RED

	public static final int BLUE = 2;
	// value of BLUE

	public static final int GREEN = 1;
	// value of GREEN

	protected static final boolean MIN = false;
	// boolean value of minimizer layer

	protected static final boolean MAX = true;
	// boolean value of maximizer layer

	public static AtroposCircle getNextPlay(AtroposState state) {
		Vector<AtroposCircle> circles = new Vector<>();
		AtroposCircle circle;
		int randomIndex;

		for (Iterator<AtroposCircle> circleIterator = state.playableCircles(); circleIterator.hasNext();) {
			circle = (AtroposCircle) circleIterator.next();
			if (!wouldLose(state.clone(), circle.clone(), RED) || !wouldLose(state.clone(), circle.clone(), BLUE)
					|| !wouldLose(state.clone(), circle.clone(), GREEN)) {
				circles.add(circle); // get all playable circle which will not
										// lose the game.
			}
		}

		if (circles.isEmpty()) { // if the player has no safe move, then play a
									// random move
			// no moves are safe. Time to lose
			Iterator<AtroposCircle> circleIterator = state.playableCircles();
			circle = (AtroposCircle) circleIterator.next();

			randomIndex = RED;
		} else { // if there has some safe moves, we need use alpha-beta pruning
					// to choose a move which has the highest value
			int alpha = -100; // set the initial value of the alpha to negative
								// infinity, -100
			int beta = 100; // set the initial value of the alpha to negative
							// infinity, 100
			ValueCircle nextmove = new ValueCircle(-100);
			// choose a maximum value move in the safe next moves
			for (int i = 0; i < circles.size(); i++) {
				for (int j = 1; j <= 3; j++) {
					AtroposCircle TempCircle = (AtroposCircle) circles.get(i);
					AtroposCircle Circle = TempCircle.clone();
					Circle.color(j);
					ValueCircle nextcircle = alphabeta(state.clone(), MIN, Circle, 5, alpha, beta);
					if (nextcircle.val > nextmove.val) {
						nextmove = nextcircle.clone();
						alpha = nextcircle.val;
					}
				}
			}
			return nextmove.circle; // return the result
		}
		circle = circle.clone();
		circle.color(randomIndex);
		return circle; // return the random move.
	}

	/*
	 * Implementation of the alpha-beta pruning
	 */

	public static ValueCircle alphabeta(AtroposState state, boolean side, AtroposCircle circle, int depth, int alpha,
			int beta) {
		state.makePlay(circle); // get the state after make play of th move
		if (state.isFinished()) {
			if (side) {
				return new ValueCircle(circle, 99); // if we win return max
													// value
			} else {
				return new ValueCircle(circle, -99); // if we lose return the
														// min value
			}
		}
		if (depth == 0) {
			return new ValueCircle(circle, evaluation(state)); // if we reach
																// the leave, we
																// need make a
																// evaluation of
																// the move
		}
		int currentVal = 0;
		if (side == MAX) {
			currentVal = -100; // initial the value of the root, if the root is
								// a maximizer, then the initial value is
								// minimum
		} else {
			currentVal = 100; // initial the value of the root, if the root is a
								// maximizer, then the initial value is maximum
		}

		Iterator circleIterator = state.playableCircles();

		while (circleIterator.hasNext()) { // visit all the next moves of this
											// move
			AtroposCircle tempCircle1 = (AtroposCircle) circleIterator.next();
			for (int color = 1; color <= 3; color++) { // search all the color
														// of the visit move
				AtroposCircle tempCircle2 = tempCircle1.clone();
				tempCircle2.color(color);
				ValueCircle nextValueCircle = alphabeta(state.clone(), !side, tempCircle2, depth - 1, alpha, beta);
				int nextVal = nextValueCircle.val;
				if (side == MAX) {
					/*
					 * if the layer is maximizer layer, then we choose the next
					 * move with the highest value
					 */
					if (nextVal > currentVal) {
						currentVal = nextVal;
						if (currentVal > beta) { // make a pruning, if the root
													// value is bigger than
													// beta, then we can return
													// this move to higher layer
							return new ValueCircle(circle, currentVal);
						} else {
							alpha = currentVal;
						}
					}
				} else {
					/*
					 * if the layer is minimizer layer, then we choose the next
					 * move with the lowest value
					 */
					if (nextVal < currentVal) {
						currentVal = nextVal;
						if (currentVal < alpha) {
							return new ValueCircle(circle, currentVal); // make
																		// a
																		// pruning,
																		// if
																		// the
																		// root
																		// value
																		// is
																		// less
																		// than
																		// alpha,
																		// then
																		// we
																		// can
																		// return
																		// this
																		// move
																		// to
																		// higher
																		// layer
						} else {
							beta = currentVal;
						}
					}
				}
			}
		}
		return new ValueCircle(circle, currentVal); // return this move to
													// higher layer.
	}

	/*
	 * Read the state from the state string
	 */

	public static AtroposState read(String str) {
		String delimits = "\\[|\\]";
		String[] components = str.split(delimits);

		int j = 0;
		// remove spaces in string, empty elements are eliminated
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals("") == false) {
				components[j++] = components[i];
			}

		}
		String[] newArray = new String[j];
		newArray = Arrays.copyOf(components, j);
		for (String s : newArray) {

		}
		int size = newArray.length - 1;

		AtroposCircle[][] Circles = new AtroposCircle[size][size];
		/*
		 * get all circles in the state string
		 */
		for (int i = 0; i < size - 1; i++) {
			int height = size - (i + 1);
			for (int k = 0; k < newArray[i].length(); k++) {
				int color = Integer.parseInt(Character.toString(newArray[i].charAt(k))); // get
																							// the
																							// color
																							// of
																							// the
																							// circle
				int left = k, right = newArray[i].length() - (k + 1); // get the
																		// position
																		// of
																		// the
																		// circle
				AtroposCircle circle = new AtroposCircle(height, left, right); // construct
																				// the
																				// circle
				circle.color(color); // color the circle
				Circles[height][left] = circle; // add the circle in the circle
												// matrix
			}
		}
		// get the circles in the last line of the state
		for (int k = 0; k < newArray[size - 1].length(); k++) {
			int color = Integer.parseInt(Character.toString(newArray[size - 1].charAt(k))); // parse
			// System.out.println(color);
			int height = 0;
			int left = k + 1, right = newArray[size - 1].length() + 1 - (left);
			AtroposCircle circle = new AtroposCircle(0, left, right);
			circle.color(color);
			// System.out.println(circle.toString());
			Circles[height][left] = circle;
		}
		AtroposCircle lastmove = null;
		// get the last move
		if (!newArray[newArray.length - 1].equals("LastPlay:null")) {
			String delimits2 = "\\(|\\)";
			String[] components2 = newArray[newArray.length - 1].split(delimits2);
			char[] components3 = components2[components2.length - 1].toCharArray();

			// determine the length of array with comma eliminated
			int r = 0;
			for (int i = 0; i < components3.length; i++) {
				if (Character.toString(components3[i]).equals(",") == false)
					components3[r++] = components3[i];
			}

			int color = components3[0] - '0';
			int height = components3[1] - '0';
			int left = components3[2] - '0';
			int right = components3[3] - '0';
			lastmove = new AtroposCircle(height, left, right);
			lastmove.color(color);

		}

		AtroposState state = new AtroposState(Circles, lastmove); // construct
																	// the state

		return state;
	}

	private static boolean wouldLose(AtroposState state, AtroposCircle circle, int color) {
		circle.color(color);
		state.makePlay(circle);
		return state.isFinished();
	}

	public static int evaluation(AtroposState state) {
		int count = 0;
		/*
		 * count all the playable move and playable color of the opponent's next
		 * move
		 */
		Iterator iterator = state.playableCircles();
		while (iterator.hasNext()) {
			AtroposCircle circle = (AtroposCircle) iterator.next();
			AtroposCircle rcircle = circle.clone();
			rcircle.color(RED);
			AtroposCircle bcircle = circle.clone();
			bcircle.color(BLUE);
			AtroposCircle gcircle = circle.clone();
			gcircle.color(GREEN);
			AtroposState rstate = state.clone();
			AtroposState gstate = state.clone();
			AtroposState bstate = state.clone();
			rstate.makePlay(rcircle);
			bstate.makePlay(bcircle);
			gstate.makePlay(gcircle);
			if (!rstate.isFinished())
				count++;
			if (!bstate.isFinished())
				count++;
			if (!gstate.isFinished())
				count++;
		}
		return -1 * count; // return the evaluation based on the count.
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			AtroposState state = read(args[0]);
			AtroposCircle nextmove = getNextPlay(state);
			state.makePlay(nextmove);
			System.out.println("(" + nextmove.getColor() + "," + nextmove.height() + "," + nextmove.leftDistance() + ","
					+ nextmove.rightDistance() + ")");
		}
	}

}
