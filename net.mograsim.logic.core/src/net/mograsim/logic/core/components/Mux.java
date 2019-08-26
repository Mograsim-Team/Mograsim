package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

/**
 * Models a multiplexer. Takes an arbitrary amount of input {@link Wire}s, one of which, as determined by select, is put through to the
 * output.
 * 
 * @author Fabian Stemmler
 *
 */
public class Mux extends BasicComponent
{
	private ReadEnd select;
	private ReadWriteEnd out;
	private ReadEnd[] inputs;
	private final int outputSize;

	/**
	 * Input {@link Wire}s and out must be of uniform width
	 * 
	 * @param out    Must be of uniform width with all inputs.
	 * @param select Indexes the input array which is to be mapped to the output. Must have enough bits to index all inputs.
	 * @param inputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public Mux(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd select, ReadEnd... inputs)
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
	public void compute()
	{
		int selectValue;
		if (!select.hasNumericValue() || (selectValue = (int) select.getUnsignedValue()) >= inputs.length)
		{
			out.clearSignals();
			return;
		}

		ReadEnd active = inputs[selectValue];
		out.feedSignals(active.getValues());
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
