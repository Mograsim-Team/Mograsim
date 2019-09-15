package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public abstract class CoreComponent
{
	protected Timeline timeline;

	public CoreComponent(Timeline timeline)
	{
		this.timeline = timeline;
	}

	/**
	 * Returns immutable list of all inputs to the {@link CoreComponent} (including e.g. the select bits to a MUX). Intended for
	 * visualization in the UI.
	 */
	public abstract List<ReadEnd> getAllInputs();

	/**
	 * Returns immutable list of all outputs to the {@link CoreComponent}. Intended for visualization in the UI.
	 */
	public abstract List<ReadWriteEnd> getAllOutputs();
}
