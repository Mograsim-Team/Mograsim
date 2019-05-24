package era.mi.logic.components;

import java.util.List;

import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import era.mi.logic.wires.WireObserver;

public class Splitter implements WireObserver, Component
{
	private ReadEnd input;
	private ReadWriteEnd[] outputs;

	public Splitter(ReadEnd input, ReadWriteEnd... outputs)
	{
		this.input = input;
		this.outputs = outputs;
		input.addObserver(this);
		int length = 0;
		for (ReadEnd out : outputs)
			length += out.length();

		if (input.length() != length)
			throw new IllegalArgumentException(
					"The input of splitting one into n WireArrays must have length = a1.length() + a2.length() + ... + an.length().");
	}

	protected void compute()
	{
		BitVector inputBits = input.getValues();
		int startIndex = 0;
		for (int i = 0; i < outputs.length; i++)
		{
			outputs[i].feedSignals(inputBits.subVector(startIndex, startIndex + outputs[i].length()));
			startIndex += outputs[i].length();
		}
	}

	@Override
	public void update(ReadEnd initiator, BitVector oldValues)
	{
		compute();
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(input);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(outputs);
	}
}
