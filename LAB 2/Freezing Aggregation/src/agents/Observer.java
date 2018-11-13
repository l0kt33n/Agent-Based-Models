package agents;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import simulation.FreezingSim;

public class Observer implements Steppable {
	
	FreezingSim sim;
	
	public Observer(FreezingSim sim) {
		this.sim = sim;	
	}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		Bag agents = sim.acquireSpace().allObjects;
		int nFrozen = 0;
		double distSum = 0;
		int nNeighborsSum = 0;
		double distFrozSum = 0;
		double distMoveSum = 0;
		
		for(Object o : agents) {
			Aggregator a = (Aggregator) o;
			if (a.isFrozen()) {
				nFrozen++;
				distFrozSum += a.getDistanceFromCenter();
			} else {
				distMoveSum += a.getDistanceFromCenter();
			}
			distSum += a.getDistanceFromCenter();
			nNeighborsSum += a.getNNeighbors();
		}
		int n = agents.numObjs;
		System.out.println(sim.schedule.getSteps()+ "\t" + n + "\t" + nFrozen + "\t" + distSum/n + 
							"\t" + distFrozSum/nFrozen + "\t" + distMoveSum/(n-nFrozen) + "\t" + 
							(double)nNeighborsSum/n);
		
	}

}
