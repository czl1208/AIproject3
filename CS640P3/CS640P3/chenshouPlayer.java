import java.util.*;

public class chenshouPlayer {

	/**
	 * @author shou Artificial Intelligience P3
	 */

	// Instance variables

	 /**
	  * @param MAX, MIN Max and Min nodes defined by booleans, to make method
	  *      calls more clear
	  */
	 public static final boolean MAX = true;
	 public static final boolean MIN = false;

	/**
	 * @param maxDepth
	 */
	public static final int MAXDEPTH = 7;

	


	public static void main(String[] args) {		
		  // Initialize board using constructor that handles an appropriate String
		  State board = new State(args[0]);
		  Move bestMove = board.getMinMaxMove(MAXDEPTH, MAX);
		  System.out.print(bestMove);
	}

}