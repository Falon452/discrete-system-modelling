import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Point {
	private ArrayList<Point> neighbors;
	private int currentState;
	private int nextState;
	private int numStates = 6;
//    private final int[] remainsAliveRules = {2, 3};
//    private final int[] deadToAliveRules = {3};
	
	public Point() {
		currentState = 0;
		nextState = 0;
		neighbors = new ArrayList<Point>();
	}

    public void drop() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);
        if (randomNum < 6)
            currentState = 6;
    }

	public void clicked() {
		currentState=(++currentState)%numStates;	
	}
	
	public int getState() {
		return currentState;
	}

	public void setState(int s) {
		currentState = s;
	}

	public void calculateNewState() {
        // Rain
        if (currentState > 0) {
            nextState = currentState - 1;
        }
        if (currentState == 0) {
            for (Point n : neighbors){
                if (n.currentState > 0) {
                    nextState = 6;
                    break;
                }
            }
        }
//        int nOfNeighbors = allActiveNeighbors();
//        boolean remainsAlive = false;
//
//        if (currentState == 0) {  // is dead
//            for (int rule : deadToAliveRules){
//                if (nOfNeighbors == rule){
//                    nextState = 1;
//                    break;
//                }
//            }
//        } else { // is alive
//            for (int rule : remainsAliveRules){
//                if (nOfNeighbors == rule) {
//                    remainsAlive = true;
//                    break;
//                }
//            }
//            if (remainsAlive)
//                nextState = 1;
//        }
	}

	public void changeState() {
		currentState = nextState;
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
	
	//TODO: write method counting all active neighbors of THIS point
    public int allActiveNeighbors(){
        int result = 0;
        for (Point n : neighbors) {
            if (n.currentState == 1)
                result += 1;
        }
        return result;
    }
}
