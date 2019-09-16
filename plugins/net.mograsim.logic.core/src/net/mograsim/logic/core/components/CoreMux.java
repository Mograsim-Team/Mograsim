package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class CoreMux extends BasicCoreComponent
{
	private ReadEnd select;
	private ReadWriteEnd out;
	private ReadEnd[] inputs;
	private final int outputSize;

	/**
	 * Input {@link CoreWire}s and out must be of uniform width
	 * 
	 * @param out    Must be of uniform width with all inputs.
	 * @param select Indexes the input array which is to be mapped to the output. Must have enough bits to index all inputs.
	 * @param inputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public CoreMux(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd select, ReadEnd... inputs)
	{
		super(timeline, processTime);
		outputSize = out.width();

		this.inputs = inputs.clone();
		for (int i = 0; i < this.inputs.length; i++)
		{
			if (inputs[i].width() != outputSize)
				throw new IllegalArgumentException("All MUX wire arrays must be of uniform width!");
			inputs[i].registerObserver(this);
		}

		this.select = select;
		select.registerObserver(this);

		int maxInputs = 1 << select.width();
		if (this.inputs.length > maxInputs)
			throw new IllegalArgumentException("There are more inputs (" + this.inputs.length + ") to the MUX than supported by "
					+ select.width() + " select bits (" + maxInputs + ").");

		this.out = out;
	}

	public ReadEnd getOut()
	{
		return out;
	}

	public ReadEnd getSelect()
	{
		return select;
	}

	@Override
	public TimelineEventHandler compute()
	{
		int selectValue;
		if (!select.getValues().isBinary() || (selectValue = (int) select.getValues().getUnsignedValueLong()) >= inputs.length)
		{
			return e -> out.clearSignals();
		}

		BitVector activeValues = inputs[selectValue].getValues();
		return e -> out.feedSignals(activeValues);
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		ArrayList<ReadEnd> wires = new ArrayList<>(Arrays.asList(inputs));
		wires.add(select);
		return Collections.unmodifiableList(wires);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
