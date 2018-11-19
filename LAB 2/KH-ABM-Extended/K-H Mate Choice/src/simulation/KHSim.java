package simulation;

import agents.Agent;
import agents.Observer;
import states.SimStateSparseGrid2D;

public class KHSim extends SimStateSparseGrid2D {
	private int gridWidth = 100;			// rendered space width and height
	private int gridHeight = 100;
	private int males = 1000;				// initial number of agents of each gender
	private int females = 1000;
	private int maxAttractiveness = 10;		// attractiveness is an integer (in this realization) from 1 to this value
	private double choosiness = 3;			// exponent in the probability equation: 0 makes everyone equally likely, 1 is linear, 3 is Kalick-Hamilton, higher values are choosier
	private int maxDates = 50;				// the number of dates at which everyone becomes 100% likely to mate with one another; set to 0 to eliminate closing-time rule
	private boolean maximize = true;		// maximize attractiveness of mate? if false, agents attempt to match attractiveness of their mate
	
	private Observer observer;

	public KHSim(long seed) {
		super(seed);
	}
	
	public void start() {
		super.start();
		makeSpace(gridWidth, gridHeight);
		makeAgents();
		observer = makeObserver();
		return;
	}

	/**
	 * Create all agents, male and female, in the number specified by this class's fields.
	 */
	public void makeAgents() {
		for (int i = 0; i < males; i++) {
			makeAgent(false);
		}
		for(int i=0; i < females; i++) {
			makeAgent(true);
		}
		return;
	}
	
	/**
	 * Make an agent and put it into space and add it to the schedule. If <i>female</i> is true, this is a female agent; otherwise, it is a
	 * male agent. The new agent is located randomly in space (no attempt is made to prevent two agents from sharing the same space at the
	 * time it is created, though movement rules may prevent this at late time steps). The agent's attractiveness is drawn from a uniform
	 * distribution between 1 and the value in this class's field <i>maxAttractiveness</i>. Agents are scheduled at the default order (0)
	 * to step repeatedly; the agent's stopper field is also set so that the agent may remove itself from the schedule at a later time.
	 * Agents are given a color depending on their gender: female agents are black and male agents are green.
	 * @param female <i>true</i> for the new agent to be female
	 * @return the new agent
	 */
	public Agent makeAgent(boolean female) {
		int x = random.nextInt(gridWidth);
		int y = random.nextInt(gridHeight);
		double attractiveness = random.nextInt(maxAttractiveness)+1;
		Agent a = new Agent(this, female, attractiveness, x, y);
		float red = 0, green = 0, blue = 0;							// default color is black
		if (!female) {												// change it to green for males
			green = 1;
		}
		// next line uses transparency ("alpha") to make less attractive agents lighter in color and more attractive agents more solid
		gui.setOvalPortrayal2DColor(a, red, green, blue, (float)(attractiveness / maxAttractiveness));
		space.setObjectLocation(a, x, y);							// put the agent in space
		a.attachStopper(schedule.scheduleRepeating(a));				// put agent on the schedule and set its stopper
		return a;
	}
	
	/**
	 * Make one new Observer agent to report statistics and model progress and to reset agents' <i>dated</i> field at the end of every time
	 * step. The agent is scheduled at order <b>10</b> so that it always steps after the dating agents (which are scheduled at ordering 0
	 * in {@link #makeAgent(boolean)}). The observer does not exist in space since it observes all agents.
	 * @return the new observer
	 */
	private Observer makeObserver() {
		Observer o = new Observer(this);
		o.attachStopper(schedule.scheduleRepeating(o, 10, 1));
		o.start();
		return o;
	}
	
	/**
	 * Get this simulation's observer.
	 * @return the observer
	 */
	public Observer acquireObserver() {
		return observer;
	}

	/**
	 * Get the width of the space. This is the number of cells that an agent can be located at from left to right, not the number of pixels
	 * used to display the space (which is set in {@link KHSimGUI} and can also be changed by the user resizing the window).
	 * @return space width
	 */
	public int getGridWidth() {
		return gridWidth;
	}

	/**
	 * Set the width of the space. This is the number of cells that an agent can be located at from left to right, not the number of pixels
	 * used to display the space (which is set in {@link KHSimGUI} and can also be changed by the user resizing the window).
	 * @param gridWidth space width
	 */
	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	/**
	 * Get the height of the space. This is the number of cells that an agent can be located at from top to bottom, not the number of
	 * pixels used to display the space (which is set in {@link KHSimGUI} and can also be changed by the user resizing the window).
	 * @return space height
	 */
	public int getGridHeight() {
		return gridHeight;
	}

	/**
	 * Set the height of the space. This is the number of cells that an agent can be located at from top to bottom, not the number of
	 * pixels used to display the space (which is set in {@link KHSimGUI} and can also be changed by the user resizing the window).
	 * @param gridHeight space height
	 */
	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	/**
	 * The number of males created at the beginning of the simulation.
	 * @return number of males
	 */
	public int getMales() {
		return males;
	}

	/**
	 * Set the number of males to be created at the beginning of the simulation.
	 * @param males number of males
	 */
	public void setMales(int males) {
		this.males = males;
	}

	/**
	 * The number of females created at the beginning of the simulation.
	 * @return number of females
	 */
	public int getFemales() {
		return females;
	}

	/**
	 * Set the number of females to be created at the beginning of the simulation.
	 * @param females number of females
	 */
	public void setFemales(int females) {
		this.females = females;
	}

	/**
	 * The maximum attractiveness for agents in this simulation. Agents can have attractiveness from 1 to this value; the {@link
	 * #makeAgent(boolean)} method draws this from a uniform distribution of integers.
	 * @return maximum agent attractiveness
	 */
	public int getMaxAttractiveness() {
		return maxAttractiveness;
	}

	/**
	 * Set the maximum attractiveness for agents in this simulation. Agents will have an attractiveness value from 1 to this value; the
	 * {@link #makeAgent(boolean)} method will draws this from a uniform distribution of integers.
	 * @param maxAttractiveness maximum agent attractiveness
	 */
	public void setMaxAttractiveness(int maxAttractiveness) {
		this.maxAttractiveness = maxAttractiveness;
	}

	/**
	 * Get the number that models how picky individual agents are about their mate choice preferences. This applies to the criterion for
	 * both maximizing and matching rules: for maximizers, the criterion is the raw attractiveness of the potential mate; for matchers,
	 * the criterion is the similarity of the potential mate's attractiveness. This number is applied as an exponent to the criterion.
	 * Thus, choosiness of 0 indicates that agents will give a 100% likelihood of matching with any agent they encounter. A choosiness of
	 * 1 provide a linear mapping between the criterion and the probability of matching (the maximum is 100%, and agents with half of the
	 * maximum are 50%). Choosiness values greater than 1 make it less likely that individuals with lower criterion values will be picked
	 * with rapidly increasing likelihood as the criterion gets closer to the maximum value; both these effects get larger as the
	 * choosiness value gets higher. Choosiness values between 0 and 1 increase the likelihood of the minimum criterion value and make the
	 * change as the criterion approaches the maximum value slow down instead of speed up -- however, it is still that case that higher
	 * criterion values are more likely to be picked than lower values. Between 0 and 1, the effects get larger as choosiness values get
	 * closer to 0 (and become closer to linear as choosiness approaches 1). 
	 * @return the choosiness exponent
	 */
	public double getChoosiness() {
		return choosiness;
	}

	/**
	 * Set the number that models how picky individual agents are about their mate choice preferences. This applies to the criterion for
	 * both maximizing and matching rules. See the getter method for this value ({@link #getChoosiness()}) for details of how this value
	 * works. 
	 * @param choosiness the choosiness exponent
	 */
	public void setChoosiness(double choosiness) {
		this.choosiness = choosiness;
	}

	/**
	 * Get the maximum number of dates for the closing time rule. That is, this is the number of time steps before agents are no longer
	 * picky about who they mate with (that is, they will accept a mating opportunity from any eligible agent they date). If this is 0, it
	 * indicates that agents are not using the "closing time" rule at all (that is, agents are equally picky for as long as the model
	 * runs).
	 * @return maximum number of dates for closing time
	 */
	public int getMaxDates() {
		return maxDates;
	}

	/**
	 * Set the maximum number of dates for the closing time rule. That is, set the number of time steps before agents are no longer picky
	 * about who they mate with (that is, when they will accept a mating opportunity from any eligible agent they date). To turn off the
	 * "closing time" rule, set this to 0 (agents will be equally picky for as long as the model runs).
	 * @param maxDates maximum number of dates for closing time
	 */
	public void setMaxDates(int maxDates) {
		this.maxDates = maxDates;
	}

	/**
	 * Are agents in this simulation using the attractiveness-maximizing rule? That is, do agents attempt to mate with the most attractive
	 * partner they can? If this is <i>false</i>, agents use the attractiveness-matching rule in which they attempt to mate with a partner
	 * that is closest in attractiveness to themselves.
	 * @return <i>true</i> if agents use the maximizing rule
	 */
	public boolean isMaximize() {
		return maximize;
	}

	/**
	 * Set if agents in this simulation should use the attractiveness-maximizing rule. If this is set to <i>true</i>, agents attempt to
	 * mate with the most attractive partner they can. If this is set to <i>false</i>, agents use the attractiveness-matching rule in which
	 * they attempt to mate with a partner that is closest in attractiveness to themselves.
	 * @param maximize <i>true</i> for agents to use the maximizing rule
	 */
	public void setMaximize(boolean maximize) {
		this.maximize = maximize;
	}

}
