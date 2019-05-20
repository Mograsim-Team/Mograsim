package era.mi.logic.components;

import java.util.List;

import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;
import era.mi.logic.wires.WireObserver;

public class Merger implements WireObserver, Component
{
	private WireEnd out;
	private WireEnd[] inputs;
	private int[] beginningIndex;

	/**
	 * 
	 * @param union  The output of merging n {@link Wire}s into one. Must have length = a1.length() + a2.length() + ... + an.length().
	 * @param inputs The inputs to be merged into the union
	 */
	public Merger(WireEnd union, WireEnd... inputs)
	{
		this.inputs = inputs;
		this.out = union;
		this.beginningIndex = new int[inputs.length];

		int length = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			beginningIndex[i] = length;
			length += inputs[i].length();
			inputs[i].addObserver(this);
		}

		if (length != union.length())
			throw new IllegalArgumentException(
					"The output of merging n WireArrays into one must have length = a1.length() + a2.length() + ... + an.length().");
	}

	public WireEnd getInput(int index)
	{
		return inputs[index];
	}

	public WireEnd getUnion()
	{
		return out;
	}

	@Override
	public void update(Wire initiator, BitVector oldValues)
	{
		int index = find(initiator);
		int beginning = beginningIndex[index];
		out.feedSignals(beginning, inputs[index].getValues());
	}

	private int find(Wire w)
	{
		for (int i = 0; i < inputs.length; i++)
			if (inputs[i].getWire() == w)
				return i;
		return -1;
	}

	public WireEnd[] getInputs()
	{
		return inputs.clone();
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return List.of(inputs);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
