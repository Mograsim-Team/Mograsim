package era.mi.logic.components;

import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;
import era.mi.logic.wires.WireObserver;

public class Splitter implements WireObserver
{
	private WireEnd input;
	private WireEnd[] outputs;

	public Splitter(WireEnd input, WireEnd... outputs)
	{
		this.input = input;
		this.outputs = outputs;
		input.addObserver(this);
		int length = 0;
		for (WireEnd out : outputs)
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
	public void update(Wire initiator, BitVector oldValues)
	{
		compute();
	}
}
