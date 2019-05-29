package mograsim.logic.core.components;

import java.util.List;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;

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
