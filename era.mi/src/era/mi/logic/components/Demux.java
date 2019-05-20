package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;

/**
 * Models a multiplexer. Takes an arbitrary amount of input {@link Wire}s, one of which, as determined by select, is put through to the
 * output.
 * 
 * @author Fabian Stemmler
 *
 */
public class Demux extends BasicComponent
{
	private final WireEnd select, in;
	private final WireEnd[] outputs;
	private final int outputSize;
	private int selected = -1;

	/**
	 * Input {@link Wire}s and out must be of uniform length
	 * 
	 * @param out     Must be of uniform length with all inputs.
	 * @param select  Indexes the input array which is to be mapped to the output. Must have enough bits to index all inputs.
	 * @param outputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public Demux(int processTime, WireEnd in, WireEnd select, WireEnd... outputs)
	{
		super(processTime);
		outputSize = in.length();

		this.in = in;
		this.outputs = outputs;
		for (int i = 0; i < this.outputs.length; i++)
		{
			if (outputs[i].length() != outputSize)
				throw new IllegalArgumentException("All DEMUX wire arrays must be of uniform length!");
			this.outputs[i] = outputs[i];
		}

		this.select = select;
		select.addObserver(this);

		int maxInputs = 1 << select.length();
		if (this.outputs.length > maxInputs)
			throw new IllegalArgumentException("There are more outputs (" + this.outputs.length + ") to the DEMUX than supported by "
					+ select.length() + " select bits (" + maxInputs + ").");
		in.addObserver(this);
	}

	@Override
	public void compute()
	{
		int selectValue = select.hasNumericValue() ? (int) select.getUnsignedValue() : -1;
		if (selectValue >= outputs.length)
			selectValue = -1;

		if (selected != selectValue && selected != -1)
			outputs[selected].clearSignals();

		selected = selectValue;

		if (selectValue != -1)
			outputs[selectValue].feedSignals(in.getValues());
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(in, select));
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(outputs));
	}
}
