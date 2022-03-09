public class Point {

	public Point nNeighbor;
	public Point wNeighbor;
	public Point eNeighbor;
	public Point sNeighbor;
	public float nVel;
	public float eVel;
	public float wVel;
	public float sVel;
	public float pressure;
    public static Integer []types = {0, 1, 2};
    int type;
    int sinInput;

	public Point() {
        type = types[0];
        clear();
	}

	public void clicked() {
        pressure = 1;
	}
	
	public void clear() {
        nVel = 0;
        eVel = 0;
        wVel = 0;
        sVel = 0;
        pressure = 0;
        type = types[0];
        sinInput = 0;
    }

	public void updateVelocity() {
        if (type == types[0]) {
            nVel = nVel - (nNeighbor.pressure - pressure);
            eVel = eVel - (eNeighbor.pressure - pressure);
            wVel = wVel - (wNeighbor.pressure - pressure);
            sVel = sVel - (sNeighbor.pressure - pressure);
        }
	}

	public void updatePresure() {
        if (type == types[0]) {
            pressure = (float) (pressure - 0.5 * (nVel + eVel + wVel + sVel));
        }
        if (type == types[2]) {
            double radians = Math.toRadians(sinInput);
            pressure = (float) (Math.sin(radians));
        }
    }

    public void updateSininput() {
        if (sinInput == 360) {
            sinInput = 0;
        } else {
            sinInput += 1;
        }
    }


	public float getPressure() {
		return pressure;
	}
}