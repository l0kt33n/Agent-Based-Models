I have provided a working Kalick-Hamilton model that does everything in the original model and, in addition, allows you to change various parameter values. Before we investigate it systematically, let's add three features to the KH-model: movement, aggregation, and replacement.

For movement, we would like to add the ability of agents to move randomly and aggregate behavior. To do this, you can add code for random movement and aggregation to the agent and simulation classes from the Walkers project. This will include variables in the simulation class to set the frequency of random movement, to turn aggregation on and off, to set the search radius for aggregation, and to set how many neighbors the agent wants to aggregate with (also known as "chuminess") with getters and setters. The agent will also have to have a way to implement those behaviors. Therefore, the agent method should include the following methods:
```
public void move()
public void aggregate(int r)
public void randomizeMovement()
```
You will also need to modify the "step" method so that the possibilities for a date (a bag of agents) that it gives to the "findDate" method can either be all of the agents in space or just the agents within a certain radius as possible dates. To do this, you will need get agents in the date search radius:

`Bag dates = state.space.getMooreNeighbors(x, y, searchRadius, mode, true);`

Again, you will also want to create variables in the simulation class to turn local dating (versus date anyone in the space) on and off and a radius to search for dates when local dating is on (with getters and setters).

Another way the KH-model is unrealistic is that there is only a finite population of male and female agents. As they pair off, the population empties out, but is this like real life? No. New people are always entering or re-entering the dating pool, so it we can improve the model by including a way to replace agents that are removed by mating.

To do this, you will need to add another variable to turn replacement on and off in the simulation class (with a getter and a setter). You will also need a method to create the new agent:

`public void replicate()`

__Hint__: This should be a lot like how an agent is created, added to space, and added to the schedule in the simulation class's "makeAgents" method, except using the gender of the current agent.

If you add this to the "remove" method in the agent (but only do it when the simulation's "replacement" variable is true), then each agent will create its replacement when it is removed from the simulation by mating. Note that this implies that the simulation will now also run until you stop it, since it never runs out of eligible agents.

__How to Extend the Kalick-Hamilton Model to Include Movement & Replacement__

__Step 1__

Download and import the Kalick-Hamilton project into Eclipse. (You need to have MASON and BasicABMs3 already in Eclipse for this to work.)


__Step 2__

Add the methods and variables discussed above to the Agent and KHSim classes. Implement the methods so that they behave as described.

__Step 3__

Show us in class that your model has the following features:

1. Random movement varies by probability
1. Aggregation on and off
1. Replacement on and off
1. Local date search or global date search

When you have completed this as a group and had it checked by a member of the instructional team, you may leave lab to go work on the written portion elsewhere, or stay in lab if you prefer.
