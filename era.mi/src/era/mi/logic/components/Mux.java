package era.mi.logic.components;

import java.util.ArrayList;
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
public class Mux extends BasicComponent
{
	private WireEnd select;
	private WireEnd out;
	private WireEnd[] inputs;
	private final int outputSize;

	/**
	 * Input {@link Wire}s and out must be of uniform length
	 * 
	 * @param out    Must be of uniform length with all inputs.
	 * @param select Indexes the input array which is to be mapped to the output. Must have enough bits to index all inputs.
	 * @param inputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public Mux(int processTime, WireEnd out, WireEnd select, WireEnd... inputs)
	{
		super(processTime);
		outputSize = out.length();

		this.inputs = inputs.clone();
		for (int i = 0; i < this.inputs.length; i++)
		{
			if (inputs[i].length() != outputSize)
				throw new IllegalArgumentException("All MUX wire arrays must be of uniform length!");
			inputs[i].addObserver(this);
		}

		this.select = select;
		select.addObserver(this);

		int maxInputs = 1 << select.length();
		if (this.inputs.length > maxInputs)
			throw new IllegalArgumentException("There are more inputs (" + this.inputs.length + ") to the MUX than supported by "
					+ select.length() + " select bits (" + maxInputs + ").");

		this.out = out;
	}

	public WireEnd getOut()
	{
		return out;
	}

	public WireEnd getSelect()
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

		WireEnd active = inputs[selectValue];
		out.feedSignals(active.getValues());
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		ArrayList<WireEnd> wires = new ArrayList<WireEnd>(Arrays.asList(inputs));
		wires.add(select);
		return Collections.unmodifiableList(wires);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
