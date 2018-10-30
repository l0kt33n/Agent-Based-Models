package simulation;

import agent.Aggregator;
import states.SimStateSparseGrid2D;

public class FreezingSim extends SimStateSparseGrid2D {

    int gridWidth = 100;
    int gridHeight = 100;
    int n = 500;
    int x0 = gridWidth / 2;
    int y0 = gridHeight / 2;
    double p = 0.5;
    boolean uniqueLocation = true;
    boolean toroidal = true;

    public FreezingSim(long seed) {
        super(seed);
    }

    public void makeAgents() {
        Aggregator a = new Aggregator(x0, y0, 0, 0, true, this);
        space.setObjectLocation(a, x0, y0);
        //schedule.scheduleRepeating(a);
        for (int i = 0; i < n - 1; i++) {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);
            int xdir = random.nextInt(3) - 1;
            int ydir = random.nextInt(3) - 1;
            a = new Aggregator(x, y, xdir, ydir, false, this);
            space.setObjectLocation(a, x, y);
            //schedule.scheduleRepeating(a);
        }
        return;
    }

    public void start() {
        super.start();
        makeAgents();
        makeSpace(gridWidth, gridHeight);
        return;
    }

	public int getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getX0() {
		return x0;
	}

	public void setX0(int x0) {
		this.x0 = x0;
	}

	public int getY0() {
		return y0;
	}

	public void setY0(int y0) {
		this.y0 = y0;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public boolean isUniqueLocation() {
		return uniqueLocation;
	}

	public void setUniqueLocation(boolean uniqueLocation) {
		this.uniqueLocation = uniqueLocation;
	}

	public boolean isToroidal() {
		return toroidal;
	}

	public void setToroidal(boolean toroidal) {
		this.toroidal = toroidal;
	}
}


