package agents;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import simulation.FreezingSim;
import java.lang.Math;

public class Aggregator implements Steppable {

	protected int x,y,dirx,diry;
	protected boolean frozen;
	protected boolean atEdge;
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
		randomizeMovement();
		move();
		return;
	}
	protected void move() {
		SparseGrid2D space = sim.acquireSpace();
		int tempx = x + dirx;
		int tempy = y + diry;
		//Bag b = space.getObjectsAtLocation(tempx, tempy);
		
			if(this.edgeCheck(sim, tempx, tempy))
			{
				if(sim.isBounded())
				{
					tempx = x-dirx;
					tempy = y-dirx;
					dirx = -dirx;
					diry = -diry;
				}
				else
				{
					tempx = space.stx(x+ dirx);
					tempy = space.sty(y+ diry);
				}
				x=tempx;
				y=tempy;
				space.setObjectLocation(this, x, y);
			}
			
		
		
		if(this.aggregateCheck(space, tempx, tempy)) {
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
	
	public boolean aggregateCheck(SparseGrid2D space, int tempx, int tempy){
		Bag b = space.getObjectsAtLocation(tempx, tempy);
		if(b!=null) {
			Aggregator nextObj = (Aggregator) b.objs[0];
			if(nextObj.frozen==true)
				return true;
		}
			return false;
	}
	
	public boolean edgeCheck(FreezingSim sim, int tempx, int tempy)
	{
		if(tempx==0||tempy==0)
		{
			return true;
		}
		else if(tempx==sim.getGridHeight()-1||tempy==sim.getGridWidth()-1)
		{
			return true;
		}
		
		return false;
	}
	
	
	protected Bag returnBag(int r)
	{
		Bag neighbhors = sim.acquireSpace().getMooreNeighbors(x, y, r, Grid2D.BOUNDED, true);
		return neighbhors; 
	}
	protected void randomizeMovement() {
		if(this.frozen == false) {
			if (sim.random.nextInt(100) < (sim.getP() * 100)){

				dirx = sim.random.nextInt(3) - 1;

				diry = sim.random.nextInt(3) - 1;

			}
		}
	}
	public double getDistanceFromCenter() {
		double dx = (double)x - sim.getGridWidth()/2;
		double dy = (double)y - sim.getGridHeight()/2;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public int getNNeighbors() {
		SparseGrid2D sp = sim.acquireSpace();
		int mode = SparseGrid2D.BOUNDED;
		if(sim.isBounded()) {
			mode = SparseGrid2D.TOROIDAL;
		}
		Bag b = sp.getMooreNeighbors(x, y, 1, mode, true);
		if (b==null) {
			return 0;
		}
		return b.numObjs;
		}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDirx() {
		return dirx;
	}

	public void setDirx(int dirx) {
		this.dirx = dirx;
	}

	public int getDiry() {
		return diry;
	}

	public void setDiry(int diry) {
		this.diry = diry;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public FreezingSim getSim() {
		return sim;
	}

	public void setSim(FreezingSim sim) {
		this.sim = sim;
	}
}

// writeup: change grid size, N, toroidal vs. bounded, probabliltiy