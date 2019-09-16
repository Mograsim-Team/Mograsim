package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

/**
 * Models a multiplexer. Takes an arbitrary amount of input {@link CoreWire}s, one of which, as determined by select, is put through to the
 * output.
 * 
 * @author Fabian Stemmler
 *
 */
public class CoreDemux extends BasicCoreComponent
{
	private final ReadEnd select, in;
	private final ReadWriteEnd[] outputs;
	private final int outputSize;
	private int selected = -1;

	/**
	 * Output {@link CoreWire}s and in must be of uniform width
	 * 
	 * @param in      Must be of uniform width with all outputs.
	 * @param select  Indexes the output array to which the input is mapped. Must have enough bits to index all outputs.
	 * @param outputs One of these outputs receives the input signal, depending on the select bits
	 */
	public CoreDemux(Timeline timeline, int processTime, ReadEnd in, ReadEnd select, ReadWriteEnd... outputs)
	{
		super(timeline, processTime);
		outputSize = in.width();

		this.in = in;
		this.outputs = outputs;
		for (int i = 0; i < this.outputs.length; i++)
		{
			if (outputs[i].width() != outputSize)
				throw new IllegalArgumentException("All DEMUX wire arrays must be of uniform width!");
			this.outputs[i] = outputs[i];
		}

		this.select = select;
		select.registerObserver(this);

		int maxInputs = 1 << select.width();
		if (this.outputs.length > maxInputs)
			throw new IllegalArgumentException("There are more outputs (" + this.outputs.length + ") to the DEMUX than supported by "
					+ select.width() + " select bits (" + maxInputs + ").");
		in.registerObserver(this);
	}

	@Override
	public TimelineEventHandler compute()
	{
		int selectValue = select.getValues().isBinary() ? (int) select.getValues().getUnsignedValueLong() : -1;
		if (selectValue >= outputs.length)
			selectValue = -1;

		boolean hasOldSelection = selected != selectValue && selected != -1;
		int oldSelection = selected;
		boolean hasNewSelection = selectValue != -1;
		int newSelection = selectValue;
		BitVector inputValues = in.getValues();

		selected = selectValue;

		return e ->
		{
			if (hasOldSelection)
				outputs[oldSelection].clearSignals();
			if (hasNewSelection)
				outputs[newSelection].feedSignals(inputValues);
		};
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in, select);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(outputs);
	}
}
