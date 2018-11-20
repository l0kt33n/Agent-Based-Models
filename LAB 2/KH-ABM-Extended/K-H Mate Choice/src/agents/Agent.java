package agents;

import agent.Walker;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import simulation.KHSim;

/**
 * An agent that goes on dates and makes mating decisions based on Kalick & Hamilton (1986), "The Matching Hypothesis Revisited."
 * @author Matt L. Miller
 */
public class Agent implements Steppable {

	private boolean female;				// true for female, false for male
	private double attractiveness;		// we use a double in case we want to do fancier things later
	private double dates = 0;			// number of dates this agent has been on
	private boolean dated = false;		// has the agent dated in this time step?
	private int x, y,dirx,diry;					// agent's location in space


	private Stoppable stopper;			// object used to stop this agent when it is no longer in the simulation
	private KHSim sim;
	private SparseGrid2D space;

	public Agent(KHSim sim, boolean female, double attractiveness, int x, int y) {
		this.sim = sim;
		space = sim.acquireSpace();				// make it easy to do things with space in methods
		this.attractiveness = attractiveness;
		this.female = female;
		this.x = x;
		this.y = y;
	}

	public void step(SimState state) {
		if (dated) {					// if someone has already dated this agent, we are done for this time step
			return;
		}
		if(sim.isAggregate())
			aggregate(sim.getAggregateDistance());
		if(sim.random.nextBoolean(sim.getpRandomMove())) 
			randomizeMovement();
		move();
		if(!sim.localDating)
		{
			Bag agents = space.allObjects;
			Agent a = findDate(agents);
			if (a == null) {				// if there's no one to date, we're done
				return;
			}
			date(a);
		}
		else
		{
			Bag dates = space.getMooreNeighbors(x, y, sim.getSearchRadius(), Grid2D.TOROIDAL, true);
			Agent a = findDate(dates);
			if (a != null)
				date(a);
		}
		
		return;
	}
	
	protected void move() {
		SparseGrid2D space = sim.acquireSpace();
		int tempx = space.stx(x + dirx);
		int tempy = space.sty(y + diry);
		Bag b = space.getObjectsAtLocation(tempx, tempy);
		if(b!=null) {
			dirx = -dirx;
			diry = -diry;
			tempx = space.stx(x+dirx);
			tempy = space.sty(y+diry);
		}
		x = tempx;
		y = tempy;
		space.setObjectLocation(this, x, y);
		return;
	}

	protected void aggregate(int r) {
		Bag neighbors = sim.acquireSpace().getMooreNeighbors(x, y, r, Grid2D.TOROIDAL, true);
		int threshold = (int)Math.round(sim.getChuminess()* (4 * r * r + 4 * r));
		int count = 0;
		double xs = 0, ys = 0;
		for(int i = 0; i < neighbors.numObjs; i++) {
			if(neighbors.objs[i] == null || neighbors.objs[i] == this)
				continue;
			Agent w = (Agent)neighbors.objs[i];
			count++;
			if(count >= threshold)
				return;
			
			int ox = w.getX();
			int oy = w.getY();
			double odist = Math.sqrt((x-ox)*(x-ox)+(y-oy)*(y-oy));
			
			if(ox<x) 
				xs-= 1 / odist;
			 else if (ox > x) 
				xs += 1 / odist;
			if(oy<y) 
				ys-= 1 / odist;
			 else if (oy > y) 
				ys += 1 / odist;
		}
		if(xs  <0)
			dirx = -1;
		else if (xs > 0)
			dirx = 1;
			else 
				dirx = 0;
		if(ys  <0)
			diry = -1;
		else if (ys > 0)
			diry = 1;
			else 
				diry = 0;
		return;
	}
	
	protected void randomizeMovement() {
		dirx = sim.random.nextInt(3) - 1;
		diry = sim.random.nextInt(3) - 1;
	}

	/**
	 * The (one-sided) probability of this agent permanently matching with another agent <i>a</i> when trying to maximize the
	 * attractiveness of a mate. The actual probability of two agents mating is the product of their individual probabilities for one
	 * another.
	 * @param a the other agent
	 * @return probability of accepting other agent as mate
	 */
	private double probabilityMaximizing(Agent a) {
		return Math.pow(a.attractiveness, sim.getChoosiness()) / Math.pow(sim.getMaxAttractiveness(), sim.getChoosiness());
	}

	/**
	 * The (one-sided) probability of this agent permanently matching with another agent <i>a</i> when trying to match the attractiveness
	 * of a mate. The actual probability of two agents mating is the product of their individual probabilities for one another.
	 * @param a the other agent
	 * @return probability of accepting other agent as mate
	 */
	private double probabilityMatching(Agent a) {
		return Math.pow(sim.getMaxAttractiveness() - Math.abs(this.attractiveness - a.attractiveness), sim.getChoosiness())
				/ Math.pow(sim.getMaxAttractiveness(), sim.getChoosiness());
	}

	/**
	 * Adjusts the probability of accepting another agent as a mate given the closing time rule and the current model time. If the maximum
	 * number of dates provided by the simulation class is equal to zero, the closing time rule is not applied.
	 * @param probability the unadjusted probability
	 * @return probability with closing time applied
	 */
	private double closingTime(double probability) {
		if (sim.getMaxDates() == 0) {
			return probability;
		}
		if (dates > sim.getMaxDates())
		{
			return 1;
		}
		
		return Math.pow(probability, (sim.getMaxDates() - dates) / sim.getMaxDates());
	}

	/**
	 * Selects a random, eligible date from <i>agents</i> and returns it. This works by selecting a random point in the bag to start
	 * looking for an eligible date and skipping any non-eligible agents (defined as opposite-sex, non-self agents that have not yet dated
	 * this step). Thus, the first eligible agent at or after the random starting point is selected. The search wraps around to the
	 * beginning of the list if necessary. If no eligible date is found in <i>agents</i>, <code>null</code> is returned. The idea of
	 * "eligibility" can be modified by modifying the code in this method (be careful to modify it in both the first loop (random number to
	 * end of bag) and the second loop (beginning of bag to random number-1).
	 * @param agents bag of agents to search for an eligible date
	 * @return random eligible date or null if there are none
	 */
	private Agent findDate(Bag agents) {
		if (agents == null || agents.numObjs == 0) { 	// if there's no one left in the bag, there is no option for a date
			return null;
		}
		int r = sim.random.nextInt(agents.numObjs);
		for (int i = r; i < agents.numObjs; i++) {		// we start searching at the random number -- this way we get the first ELIGIBLE
			Agent a = (Agent) agents.objs[i];			//     date even if the random number we drew was a non-eligible agent
			if (a == this) {
				continue;
			}
			if (!a.dated && female != a.female) {		// this could change if we also wanted to model non-heterosexual couples (as would 
				return a;								//     this test in the second loop)
			}
		}
		for (int i = 0; i < r; i++) {					// if we didn't find a dateable individual before the end, we loop back around to
			Agent a = (Agent) agents.objs[i];			//     the beginning
			if (a == this) {
				continue;
			}
			if (!a.dated && female != a.female) {		// this could change if we also wanted to model non-heterosexual couples (as would
				return a;								//     this test in the first loop)
			}
		}
		return null;									// if we didn't find a dateable individual in the entire list, there is no option
	}													//     for a date

	/**
	 * Effects a date between this agent and <i>other</i>. This method calculates each of the agent's probability of matching with the
	 * other agent, then draws a random yes/no (boolean) decision for each based on that probability. If both agents say "yes," they are
	 * permanently matched with each other and removed from the simulation.
	 * @param other the agent to go on a date with
	 */
	private void date(Agent other) {
		double pMe;
		double pOther;
		if (sim.isMaximize()) {							// get the probabilities for each agent accepting the other as a mate
			pMe = probabilityMaximizing(other);
			pOther = other.probabilityMaximizing(this);
		} else {										// if we're not maximizing, then we're matching
			pMe = probabilityMatching(other);
			pOther = other.probabilityMatching(this);
		}
		pMe = closingTime(pMe);							// apply the closing-time rule to adjust the probabilities for both agents
		pOther = other.closingTime(pOther);
		if (sim.random.nextBoolean(pMe) && sim.random.nextBoolean(pOther)) {	// if the date worked out ...
			sim.acquireObserver().recordMateChoice(this, other);				// ... tell the observer about the date
			this.remove();														// ... and remove both agents from the simulation
			other.remove();
		} else {																// otherwise (date did not succeed) ...
			dated = true;														// ... set both agent's so they can't date again this step
			other.dated = true;
			dates++;															// ... increment both agents' date counters 
			other.dates++;
		}
		return;
	}

	/**
	 * Remove this agent from the space and the schedule so it is no longer part of the simulation.
	 */
	private void remove() {
		space.remove(this);		// out of space
		stopper.stop();			// off the schedule
		return;
	}
	
	/**
	 * Reset the agent at the end of a step so it can date again on the next step.
	 */
	public void reset() {
		dated = false;
		return;
	}
	
	/**
	 * Set the stopper for this agent so it can remove itself from the schedule
	 * @param s stopper returned from schedule when this agent is scheduled repeating
	 */
	public void attachStopper(Stoppable s) {
		stopper = s;
		return;
	}

	/**
	 * Is this agent female?
	 * @return true if female
	 */
	public boolean isFemale() {
		return female;
	}

	/**
	 * Return this agent's attractiveness.
	 * @return attractiveness
	 */
	public double getAttractiveness() {
		return attractiveness;
	}

	/**
	 * Return the number of dates this agent has been on.
	 * @return number of dates
	 */
	public double getDates() {
		return dates;
	}

	/**
	 * Has this agent dated in the current time step?
	 * @return true if agent has already dated
	 */
	public boolean isDated() {
		return dated;
	}

	/**
	 * Get the x coordinate of this agent's location.
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y coordinate of this agent's location.
	 * @return y coordinate
	 */
	public int getY() {
		return y;
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

}
