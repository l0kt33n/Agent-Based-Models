package simulation;

import java.awt.Color;

import states.GUIStateSparseGrid2D;
import states.SimStateSparseGrid2D;

public class FreezingSimGUI extends GUIStateSparseGrid2D {

	public static void main(String[] args) {
		initialize(FreezingSim.class, FreezingSimGUI.class,450,450,Color.BLACK,
					Color.GREEN,true);
		return;
	}

	public FreezingSimGUI(SimStateSparseGrid2D state, int gridWidth, int gridHeight, Color backdrop,
			Color agentDefaultColor, boolean defaultPortrayal) {
		super(state, gridWidth, gridHeight, backdrop, agentDefaultColor, defaultPortrayal);
	}

}