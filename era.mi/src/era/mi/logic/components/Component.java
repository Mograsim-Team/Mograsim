package era.mi.logic.components;

import java.util.List;

import era.mi.logic.wires.Wire.WireEnd;

public interface Component
{

	/**
	 * Returns immutable list of all inputs to the {@link Component} (including e.g. the select bits to a MUX). Intended for visualization
	 * in the UI.
	 */
	public List<WireEnd> getAllInputs();

	/**
	 * Returns immutable list of all outputs to the {@link Component}. Intended for visualization in the UI.
	 */
	public List<WireEnd> getAllOutputs();
}
