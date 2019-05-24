package era.mi.logic.components;

import java.util.List;

import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public interface Component
{

	/**
	 * Returns immutable list of all inputs to the {@link Component} (including e.g. the select bits to a MUX). Intended for visualization
	 * in the UI.
	 */
	public List<ReadEnd> getAllInputs();

	/**
	 * Returns immutable list of all outputs to the {@link Component}. Intended for visualization in the UI.
	 */
	public List<ReadWriteEnd> getAllOutputs();
}
