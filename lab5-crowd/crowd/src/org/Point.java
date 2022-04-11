package org;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.min;

public class Point {

	public ArrayList<Point> neighbors;
	public static Integer []types ={0,1,2,3};
	public int type;
	public int staticField;
	public boolean isPedestrian;
    public boolean blocked = false;

	public Point() {
		type=0;
		staticField = 100000;
		neighbors= new ArrayList<Point>();
	}
	
	public void clear() {
		staticField = 100000;
	}
    public void clearNeighbours() {
        neighbors.clear();
    }


	public boolean calcStaticField() {
        if (this.type != 1) {
            int smallestStaticField = 1000000000;
            for (Point n : this.neighbors)
                smallestStaticField = min(smallestStaticField, n.staticField);
            if (staticField > smallestStaticField + 1) {
                staticField = smallestStaticField + 1;
                return true;
            }
        }
        return false;
	}
    public void repulseForce(int iteration){
        if (iteration % 3 == 0){
            return;
        }
        if (this.type == 1) {
            int biggestStaticField = 0;
            for (Point n: this.neighbors) {
                if (n.type == 3) {
                    if (n.staticField > biggestStaticField) {
                        biggestStaticField = n.staticField;
                    }
                }
            }

        }

    }

	
	public void move(){
        if (this.isPedestrian && !this.blocked && this.type != 1) {
            Point smallestPoint = null;
            int smallestStaticField = 100000000;
            for (Point n : this.neighbors) {
                if (!n.isPedestrian && n.staticField < smallestStaticField && n.type != 1) {
                    smallestPoint = n;
                    smallestStaticField = n.staticField;
                }
            }
            if (Objects.isNull(smallestPoint))
                return;

            this.isPedestrian = false;
            if (smallestPoint.type != 2)
                smallestPoint.isPedestrian = true;
            smallestPoint.blocked = true;
        }
	}

	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
}