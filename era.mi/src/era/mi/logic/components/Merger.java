package era.mi.logic.components;

import java.util.List;

import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import era.mi.logic.wires.WireObserver;

public class Merger implements WireObserver, Component
{
	private ReadWriteEnd out;
	private ReadEnd[] inputs;
	private int[] beginningIndex;

	/**
	 * 
	 * @param union  The output of merging n {@link Wire}s into one. Must have length = a1.length() + a2.length() + ... + an.length().
	 * @param inputs The inputs to be merged into the union
	 */
	public Merger(ReadWriteEnd union, ReadEnd... inputs)
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

	public ReadEnd getInput(int index)
	{
		return inputs[index];
	}

	public ReadEnd getUnion()
	{
		return out;
	}

	@Override
	public void update(ReadEnd initiator, BitVector oldValues)
	{
		int index = find(initiator);
		int beginning = beginningIndex[index];
		out.feedSignals(beginning, inputs[index].getValues());
	}

	private int find(ReadEnd r)
	{
		for (int i = 0; i < inputs.length; i++)
			if (inputs[i] == r)
				return i;
		return -1;
	}

	public ReadEnd[] getInputs()
	{
		return inputs.clone();
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(inputs);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
