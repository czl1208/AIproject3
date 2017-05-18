
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;


public class PlayerRandom {
	public static final int RED = 1;
	public static final int BLUE = 2;
	public static final int GREEN = 1;
	protected static final boolean MIN = false;  
	protected static final boolean MAX = true;

	public static AtroposCircle getNextPlay(AtroposState state) {
	    Vector<AtroposCircle> circles = new Vector<>();
	    AtroposCircle circle;
	    int randomIndex;
	    for (Iterator<AtroposCircle> circleIterator = state.playableCircles();
	         circleIterator.hasNext(); ) {
	      circle = (AtroposCircle) circleIterator.next();
	      //I think we need change here!
	      if (!wouldLose(state.clone(), circle.clone(), RED) ||
	          !wouldLose(state.clone(), circle.clone(), BLUE) ||
	          !wouldLose(state.clone(), circle.clone(), GREEN)) {
	        circles.add(circle);
	      }
	    }
	    if (circles.isEmpty()) {
	      //no moves are safe.  Time to lose
	      Iterator<AtroposCircle> circleIterator = state.playableCircles();
	      circle = (AtroposCircle) circleIterator.next();
	      
	      randomIndex = RED;
	    } else {
	    	randomIndex = (int) Math.floor(circles.size() * Math.random());
    		circle = (AtroposCircle) circles.get(randomIndex);
    		//choose a random color
    		randomIndex = (int) Math.floor(3 * Math.random()) + 1;
    		while (wouldLose(state.clone(), circle.clone(), randomIndex)) {
    			randomIndex = (int) Math.floor(3 * Math.random()) + 1;
    		}
	    	}
	    circle = circle.clone();
	    circle.color(randomIndex);
	    return circle;
	  }
	
	
//	public static ValueCircle alphabeta(AtroposState state, boolean side, AtroposCircle circle,
//			  int depth, int alpha, int beta){
//		  state.makePlay(circle);
//		  if(state.isFinished()){
//			  if(side){
//				  return new ValueCircle(circle, 99);
//			  }else{
//				  return new ValueCircle(circle, -99);
//			  }
//		  }
//		  if(depth == 0){
//			  return new ValueCircle(circle, evaluation(state));
//		  }
//		  int currentVal = 0;
//		  if(side == MAX){
//			  currentVal = -100;
//		  }else{
//			  currentVal = 100;
//		  }
//		 
//		  Iterator circleIterator = state.playableCircles();
//		  while(circleIterator.hasNext()){
//			  AtroposCircle tempCircle1 = (AtroposCircle) circleIterator.next();
//			  for(int color=1; color<=3; color++){
//				  AtroposCircle tempCircle2 = tempCircle1.clone();
//				  tempCircle2.color(color);
//				  ValueCircle nextValueCircle = alphabeta(state.clone(), !side, tempCircle2, depth-1, alpha, beta);
//				  int nextVal = nextValueCircle.val;
//				  if(side == MAX){
//					  if(nextVal>currentVal){
//						  currentVal = nextVal;
//						  if(currentVal>beta){
//							  return new ValueCircle(circle, currentVal);
//						  }else{
//							  alpha = currentVal;
//						  }
//					  }
//				  }else{
//					  if(nextVal<currentVal){
//						  currentVal = nextVal;
//						  if(currentVal<alpha){
//							  return new ValueCircle(circle, currentVal);
//						  }else{
//							  beta = currentVal;
//						  }
//					  }
//				  }
//			  }
//		  }
//		  return new ValueCircle(circle, currentVal);
//	  }
//	
	
	public static AtroposState read(String str) {
		String delimits = "\\[|\\]";
		String[] components = str.split(delimits);
		
		int j = 0;
		for(String s : components){
			
		}
		// remove spaces in string, empty elements are eliminated
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals("") == false) {
				components[j++] = components[i];
			}

		}
		String [] newArray = new String[j];
		newArray = Arrays.copyOf(components, j);
		for(String s:newArray){
			
		}
		int size = newArray.length - 1;
		// matrix construction
		// change character into matrix
		AtroposCircle[][] Circles = new AtroposCircle[size][size];
		for (int i = 0; i < size-1; i++) {
			int height = size - (i+1);
			for (int k = 0; k < newArray[i].length(); k++) {
				int color = Integer.parseInt(Character.toString(newArray[i].charAt(k))); // parse
				//System.out.println(color);
				int left = k, right = newArray[i].length() - (k+1);                         
				AtroposCircle circle = new AtroposCircle(height, left, right);
				circle.color(color);
				//System.out.println(circle.toString());
				Circles[height][left] = circle;
			}
		}
		for(int k=0; k<newArray[size-1].length(); k++){
			int color = Integer.parseInt(Character.toString(newArray[size-1].charAt(k))); // parse
			//System.out.println(color);
			int height = 0;
			int left = k+1, right = newArray[size-1].length()+1 - (left);                         
			AtroposCircle circle = new AtroposCircle(0, left, right);
			circle.color(color);
			//System.out.println(circle.toString());
			Circles[height][left] = circle;
		}
		AtroposCircle lastmove = null;
	
		if(!newArray[newArray.length-1].equals("LastPlay:null")) {
			String delimits2 = "\\(|\\)";
			String[] components2 = newArray[newArray.length - 1].split(delimits2);
			char [] components3 = components2[components2.length -1].toCharArray();
			
			// determine the length of array with comma eliminated
			int r = 0;
			for (int i=0; i < components3.length; i++) {
				if (Character.toString(components3[i]).equals(",")==false)
			          components3[r++] = components3[i];
			}
			
			
			int color = components3[0]-'0';
			int height = components3[1]-'0';
			int left = components3[2]-'0';
			int right = components3[3]-'0';
			lastmove = new AtroposCircle(height, left, right);
			lastmove.color(color);
			//System.out.println(lastmove.toString());
		} 
		// find best move through function moveGen
		
		AtroposState state = new AtroposState(Circles, lastmove);
		
		return state;
	}
	
	
	private static boolean wouldLose(AtroposState state, AtroposCircle circle, int color) {
		 circle.color(color);
		 state.makePlay(circle);
		 return state.isFinished();
	}
	
	
	public static int evaluation(AtroposState state){
		  int count = 0;
		  Iterator iterator = state.playableCircles();
		  while(iterator.hasNext()){
			  AtroposCircle circle = (AtroposCircle)iterator.next();
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
			  if(!rstate.isFinished()) count++;
			  if(!bstate.isFinished()) count++;
			  if(!gstate.isFinished()) count++;	  
		  }
		  return -1*count;
	  }
	
	public static void main(String[] args) {
		if(args.length>0){
			AtroposState state = read(args[0]);
			AtroposCircle nextmove = getNextPlay(state);
			state.makePlay(nextmove);
			System.out.println("("+nextmove.getColor()+","+nextmove.height()+","+nextmove.leftDistance()+","+nextmove.rightDistance()+")");
		}
	}
	

}
