package era.mi.logic.components;

import java.util.List;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public abstract class Component
{
	protected Timeline timeline;

	public Component(Timeline timeline)
	{
		this.timeline = timeline;
	}

	/**
	 * Returns immutable list of all inputs to the {@link Component} (including e.g. the select bits to a MUX). Intended for visualization
	 * in the UI.
	 */
	public abstract List<ReadEnd> getAllInputs();

	/**
	 * Returns immutable list of all outputs to the {@link Component}. Intended for visualization in the UI.
	 */
	public abstract List<ReadWriteEnd> getAllOutputs();
}
