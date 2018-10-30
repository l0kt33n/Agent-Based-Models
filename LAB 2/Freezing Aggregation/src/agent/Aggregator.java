package agent;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import simulation.FreezingSim;
import java.lang.Math;

public class Aggregator implements Steppable {

	protected int x,y,dirx,diry;
	protected boolean frozen;
	protected FreezingSim sim;
	
	
	public Aggregator(int x, int y, int dirx, int diry, boolean frozen, FreezingSim sim) {
		this.x = x;
		this.y = y;
		this.dirx = dirx;
		this.diry = diry;
		this.frozen = frozen;
		this.sim = sim;
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub

	}
	protected void move() {
		SparseGrid2D space = sim.acquireSpace();
		int tempx = space.stx(x + dirx);
		int tempy = space.stx(y + diry);
		Bag b = space.getObjectsAtLocation(tempx, tempy);
		if(aggregate(space, tempx, tempy)) {
			frozen = true;
			dirx = 0;
			diry = 0;
		}
		else {
		x = tempx;
		y = tempy;
		space.setObjectLocation(this, x, y);
		}
		
		return;
	}
	
	public boolean aggregate(SparseGrid2D space, int tempx, int tempy){
		Bag b = space.getObjectsAtLocation(tempx, tempy);
		if(b!=null)
			return true;
		else
			return false;
	}
	
	protected void randomizeMovement() {
		if(this.frozen == false) {
		if (sim.random.nextInt(100) > (sim.getP() * 100)){

			dirx = sim.random.nextInt(3) - 1;

			diry = sim.random.nextInt(3) - 1;

			}
		}
	}
}
