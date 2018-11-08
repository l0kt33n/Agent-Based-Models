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
					dirx = -dirx;
					diry = -diry;
					tempx = x+dirx;
					tempy = y+dirx;
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
}

// writeup: change grid size, N, toroidal vs. bounded, probabliltiy