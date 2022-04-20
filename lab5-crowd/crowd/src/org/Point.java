package org;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.min;

public class Point {

	public ArrayList<Point> neighbors;
	public static CellTypes []types = CellTypes.values();
	public CellTypes type;
	public int staticField;
	public boolean isPedestrian;
    public boolean blocked = false;
    public boolean smoked = false;
    public boolean blockedSmoke = false;

	public Point() {
		type = CellTypes.AIR;
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
        if (this.type != CellTypes.WALL && this.type != CellTypes.FIRE) {
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

	
	public void move(){
        Random rand = new Random();
        if (this.isPedestrian && !this.blocked && this.type != CellTypes.WALL) {
            // RANDOM MOVE 10 percent
            int random = rand.nextInt(100);
            if (random < 10) {
                Point n = this.neighbors.get(random % this.neighbors.size());
                if (!n.isPedestrian && n.type != CellTypes.WALL && n.type != CellTypes.EXIT) {
                    if (n.type == CellTypes.FIRE) { // he went into fire
                        this.isPedestrian = false;
                        this.type = CellTypes.AIR;
                        n.blocked = true;
                    } else {
                        n.isPedestrian = true;
                        n.type = CellTypes.PERSON;
                        this.isPedestrian = false;
                        n.blocked = false;
                    }
                }
            } else {
                // GO TOWARDS EXIT
                Point smallestPoint = null;
                int smallestStaticField = 100000000;
                for (Point n : this.neighbors) {
                    if (!n.isPedestrian && n.staticField < smallestStaticField && n.type != CellTypes.WALL) {
                        smallestPoint = n;
                        smallestStaticField = n.staticField;
                    }
                }
                if (Objects.isNull(smallestPoint))
                    return;

                this.isPedestrian = false;
                this.type = CellTypes.AIR;
                if (smallestPoint.type != CellTypes.EXIT && smallestPoint.type != CellTypes.FIRE) {
                    smallestPoint.isPedestrian = true;
                    smallestPoint.type = CellTypes.AIR;
                }
                smallestPoint.blocked = true;
            }
        }
	}

    public void moveSmoke() {
        if (this.smoked && !this.blockedSmoke) {
            Random rand = new Random();
            for (Point n : neighbors) {
                if (n.type != CellTypes.WALL) {
                    int random = rand.nextInt(10);
                    if (random < 7) {

                        n.smoked = true;
                        n.blockedSmoke = true;
                    }
                }
            }
        }
    }

    public void moveFire() {
        if (this.type == CellTypes.FIRE && !this.blockedSmoke) {
            Random rand = new Random();
            for (Point n : neighbors) {
                int random = rand.nextInt(10);
                if (random < 7) {
                    n.type = CellTypes.FIRE;
                    n.blockedSmoke = true;
                    for (Point n1 : n.neighbors) {
                        n1.staticField += 1;
                        for (Point n2 : n1.neighbors) {
                            n2.staticField += 1;

                            for (Point n3 : n2.neighbors) {
                                n3.staticField += 1;
                            }
                        }
                    }
                    n.staticField = 1000000;
                }
            }
        }
    }

	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
};