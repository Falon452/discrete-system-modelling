package agh.ics.mds;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.max;
import static java.lang.Math.min;

import static agh.ics.mds.Type.*;


public class Point {
    static final int MAX_VELOCITY = 5;

    public Type type = AIR;
    public boolean moved = false;
    public Point[] next = new Point[MAX_VELOCITY];

    private int velocity = 0;

    public void move() {
        if (velocity > 0 && type == CAR) {

            if (next[velocity-1].type == AIR && !moved) {
                type = AIR;
                next[velocity - 1].type = CAR;
                moved = true;
                next[velocity - 1].moved = true;
            }
        }
    }

    public void clicked() {
        type = CAR;
        velocity = 1;
    }

    public void clear() {
        type = AIR;
        velocity = 0;
    }

    public void randomness(float p) {
        if (velocity > 0) {
            int m_p = (int) (p * 100);
            int randomNum = ThreadLocalRandom.current().nextInt(0, 100);
            if (randomNum < m_p) {
                if (randomNum % 2 == 0)
                    velocity = max(0, velocity - 1);
                else
                    velocity = min(MAX_VELOCITY, velocity + 1);
            }
        }
    }

    public void accelerate(){
        velocity = min(MAX_VELOCITY, velocity + 1);
    }

    public void slowing_down() {
        for (int distance = 0; distance < Point.MAX_VELOCITY; distance++) {
            if (next[distance].type == CAR) {
                if (distance < velocity) {
                    velocity = distance - 1;
                }
            }
        }
    }


}

