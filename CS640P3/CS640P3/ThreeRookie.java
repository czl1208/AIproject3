import java.util.Arrays;

public class ThreeRookie {
	public static void main(String[] args) {
		String first = args[0];
		String delimits = "\\[|\\]";
		
		String[] components = first.split(delimits);
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
		if (size % 2 == 0) {
			State board = new State(first);
			Move bestMove = board.getMinMaxMove(7, true);
			System.out.println(bestMove);
		} else {
			PlayerCao cPlayer = new PlayerCao();
			AtroposState state = cPlayer.read(args[0]);
			AtroposCircle nextmove = cPlayer.getNextPlay(state);
			state.makePlay(nextmove);
			System.out.println("("+nextmove.getColor()+","+nextmove.height()+","+nextmove.leftDistance()+","+nextmove.rightDistance()+")");
		}
	}

}
