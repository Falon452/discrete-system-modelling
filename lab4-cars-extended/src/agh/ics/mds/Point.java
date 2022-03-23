package agh.ics.mds;

import agh.ics.mds.Enums.Driver;
import agh.ics.mds.Enums.Lane;
import agh.ics.mds.Enums.Type;

import static agh.ics.mds.Enums.Lane.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

import static agh.ics.mds.Enums.Type.*;
import static agh.ics.mds.Enums.Driver.*;


public class Point {
    public static int FASTEST_VEHICLE_SPEED = Driver.fastestCar();

    public Type type = ROAD;
    public Driver driver = None;
    public boolean moved = false;
    public Lane lane;
    public Point[] next = new Point[FASTEST_VEHICLE_SPEED];
    public Point[] before = new Point[FASTEST_VEHICLE_SPEED];
    public Point[] nextOtherLane = new Point[FASTEST_VEHICLE_SPEED + 1]; // contains also the other lane same j cell
    public Point[] beforeOtherLane = new Point[FASTEST_VEHICLE_SPEED];


    public int velocity = 0;

    public void overtaking() {
        if (type == CAR) {
            if (lane == RIGHT && velocity > 0 && !moved) {
                int beforeRightLaneDistance = 100000;
                int beforeLeftLaneDistance = 100000;
                int nextLeftLaneDistance = 100000;
                boolean isCarToOvertake = false;

                for (int i = 0; i < getDriverMaxSpeed(driver); i ++) {
                    if (next[i].type == CAR) {
                        isCarToOvertake = true;
                        break;
                    }
                }

                for (int i = 0; i < FASTEST_VEHICLE_SPEED; i++)
                    if (before[i].type == CAR) {
                        beforeRightLaneDistance = i + 1;
                        break;
                    }

                for (int i = 0; i < FASTEST_VEHICLE_SPEED; i++)
                    if (beforeOtherLane[i].type == CAR) {
                        beforeLeftLaneDistance = i + 1;
                        break;
                    }
                for (int i = 0; i <= FASTEST_VEHICLE_SPEED; i++)
                    if (nextOtherLane[i].type == CAR) {
                        nextLeftLaneDistance = i;
                        break;
                    }

                if (
                        isCarToOvertake &&
                        velocity < getDriverMaxSpeed(driver) &&
                        beforeRightLaneDistance >= FASTEST_VEHICLE_SPEED &&
                        beforeLeftLaneDistance >= FASTEST_VEHICLE_SPEED &&
                        nextLeftLaneDistance >= velocity

                ) {

                    nextOtherLane[velocity - 1].type = CAR;
                    nextOtherLane[velocity - 1].driver = driver;
                    nextOtherLane[velocity - 1].moved = true;
                    nextOtherLane[velocity - 1].velocity = velocity;

                    type = ROAD;
                    driver = None;
                    velocity = 0;
                    moved = true;
                }
            }
        }
    }

    public void returning() {
        if (type == CAR && !moved && lane == LEFT) {
            int beforeRightDistance = 10000;
            int beforeLeftDistance = 100000;
            int nextRightDistance = 10000;


            for (int i = 0; i < FASTEST_VEHICLE_SPEED; i++) {
                if (beforeOtherLane[i].type == CAR) {
                    beforeRightDistance = i + 1;
                    break;
                }
            }
            for (int i = 0; i < FASTEST_VEHICLE_SPEED; i++) {
                if (before[i].type == CAR) {
                    beforeLeftDistance = i + 1;
                    break;
                }
            }

            for (int i = 0; i < FASTEST_VEHICLE_SPEED; i++) {
                if (nextOtherLane[i].type == CAR) {
                    nextRightDistance = i;  // 0 means the are at the same position
                }
            }

            if (beforeRightDistance >= FASTEST_VEHICLE_SPEED &&
                beforeLeftDistance >= FASTEST_VEHICLE_SPEED &&  // no idea why
                nextRightDistance >= velocity &&
                velocity > 0) {
                nextOtherLane[velocity - 1].type = CAR;
                nextOtherLane[velocity - 1].driver = driver;
                nextOtherLane[velocity - 1].moved = true;
                nextOtherLane[velocity - 1].velocity = velocity;

                type = ROAD;
                driver = None;
                velocity = 0;
                moved = true;

            }
        }
    }

    public void move() {
        if (velocity > 0 && type == CAR) {
            if (next[velocity-1].type == ROAD && !moved) {
                next[velocity - 1].type = CAR;
                next[velocity - 1].driver = driver;
                next[velocity - 1].moved = true;
                next[velocity - 1].velocity = velocity;

                type = ROAD;
                moved = true;
                driver = None;
                velocity = 0;
            }
        }
    }

    public void clear() {
        if (this.type != GRASS) {
            type = ROAD;
            velocity = 0;
            driver = None;

        }
    }

    public void accelerate(){
        if (this.type == CAR) {
            velocity = min(getDriverMaxSpeed(driver), velocity + 1);
        }

    }

    public void slowing_down() {
        for (int distance = 1; distance < getDriverMaxSpeed(driver); distance++) {
            if (next[distance-1].type == CAR) {
                if (distance < velocity) {
                    velocity = distance - 1;
                }
                break;
            }
        }
    }


}

