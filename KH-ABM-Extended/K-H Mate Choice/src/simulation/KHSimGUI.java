package simulation;

import java.awt.Color;

import states.GUIStateSparseGrid2D;
import states.SimStateSparseGrid2D;

public class KHSimGUI extends GUIStateSparseGrid2D {

	public KHSimGUI(SimStateSparseGrid2D state, int gridWidth, int gridHeight, Color backdrop, Color agentDefaultColor,
			boolean agentPortrayal) {
		super(state, gridWidth, gridHeight, backdrop, agentDefaultColor, agentPortrayal);
	}

	public static void main(String[] args) {
		KHSimGUI.initialize(KHSim.class, KHSimGUI.class, 600, 600, Color.WHITE, Color.RED, false);
	}

}
