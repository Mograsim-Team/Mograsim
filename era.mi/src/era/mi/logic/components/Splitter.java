package era.mi.logic.components;

import era.mi.logic.Bit;
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
		int startIndex = 0;
		Bit[] inputBits = input.getValues();
		for (int i = 0; i < outputs.length; i++)
		{
			Bit[] outputBits = new Bit[outputs[i].length()];
			System.arraycopy(inputBits, startIndex, outputBits, 0, outputs[i].length());
			outputs[i].feedSignals(outputBits);
			startIndex += outputs[i].length();
		}
	}

	@Override
	public void update(Wire initiator, Bit[] oldValues)
	{
		compute();
	}
}
