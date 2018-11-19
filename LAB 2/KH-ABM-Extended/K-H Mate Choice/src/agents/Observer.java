package agents;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import simulation.KHSim;

public class Observer implements Steppable {
	private Stoppable stopper;		// object used to remove the observer from the schedule
	private double sumF = 0;	// female attractiveness accumulator
	private double sumM = 0;	// male attractiveness accumulator
	private double sumMF = 0;	// attractiveness product accumulator
	private double sumFsq = 0;	// female attractiveness^2 accumulator
	private double sumMsq = 0;	// male attractiveness^2 accumulator
	private int n = 0;			// how many pairs have mated?
	
	private KHSim sim;
	
	public Observer(KHSim sim) {
		this.sim = sim;
	}
	
	public void step(SimState state) {
		printData();			// get and print current time step stats
		setDated();				// reset all agents left in the simulation so they can date on the next time step
		if (sim.acquireSpace().getAllObjects().numObjs == 0) {	// if there are no agents left in the simulation, we stop the observer so
			stopper.stop();										//     the simulation doesn't keep running just for the observer
		}
		return;
	}
	
	/**
	 * Set up observer; should be called in simulation's start method. In this case, we just print the data column headers.
	 */
	public void start() {
		System.out.println("steps\tpairs\tcorrelation\tmaleA\t\tfemaleA");
		return;
	}
	
	/**
	 * Record a mate pairing; agents can be passed in either order. In this case, we are just recording the attractiveness of both agents.
	 * @param a1 one of the agents in a new pair
	 * @param a2 other agent in a new pair
	 */
	public void recordMateChoice(Agent a1, Agent a2) {
		if (a1.isFemale()) {
			recordMateChoice(a1.getAttractiveness(), a2.getAttractiveness());
		} else {
			recordMateChoice(a2.getAttractiveness(), a1.getAttractiveness());
		}
		return;
	}
	
	/**
	 * Log the attractiveness statistics from a mated pair. Worker method for public method that agents call when mating.
	 * @param f female's attractiveness
	 * @param m male's attractiveness
	 */
	private void recordMateChoice(double f, double m) {
		sumF += f;
		sumM += m;
		sumMF += f * m;
		sumFsq += f * f;
		sumMsq += m * m;
		n++;
		return;
	}
	
	/**
	 * Calculate and return the current attractiveness correlation from the accumulator statistics.
	 * @return current attractiveness correlation
	 */
	private double correlation() {
		double num = sumMF - (sumF * sumM) / n;
		double div = Math.sqrt(sumFsq - (sumF * sumF) / n) * Math.sqrt(sumMsq - (sumM * sumM) / n);
		return num / div;
	}
	
	/**
	 * Reset all agents left in the simulation (that is, unmated) so they can go on dates in the next time step. This is probably just
	 * setting their <i>dated</i> field to <i>false</i>, though that is up to the agent class.
	 */
	private void setDated() {
		Bag agents = sim.acquireSpace().allObjects;
		for (int i = 0; i < agents.numObjs; i++) {
			Agent a = (Agent)agents.objs[i];
			a.reset();
		}
		return;
	}
	
	/**
	 * Get data for the current time step and print it to the output.
	 */
	private void printData() {
		double correlation = correlation();
		double maleAvg = sumM / n;
		double femaleAvg = sumF / n;
		long step = sim.schedule.getSteps();
		System.out.println(step + "\t" + n + "\t" + format(correlation) + "\t" + format(maleAvg) + "\t" + format(femaleAvg));
		return;
	}
	
	/**
	 * Format a double to a string as 9.54321. That is, a the number will be converted to a string with a width of 7 with 5 significant digits.
	 * @param d number to be formatted
	 * @return <i>d</i> formatted as a string
	 */
	private String format(double d) {
		return String.format("% 7.5f", d);
	}
	
	/**
	 * Set the object that will allow us to remove the observer from the schedule so it stops stepping.
	 * @param s the object with the stop method for <i>this</i> object
	 */
	public void attachStopper(Stoppable s) {
		stopper = s;
		return;
	}

}
