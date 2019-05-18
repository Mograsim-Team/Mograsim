package era.mi.logic.components;

import era.mi.logic.Bit;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;
import era.mi.logic.wires.WireArrayObserver;

public class Splitter implements WireArrayObserver
{
	private WireArray input;
	private WireArrayEnd[] outputs;

	public Splitter(WireArray input, WireArray... outputs)
	{
		this.input = input;
		this.outputs = WireArray.extractInputs(outputs);
		input.addObserver(this);
		int length = 0;
		for (WireArray out : outputs)
			length += out.length;

		if (input.length != length)
			throw new IllegalArgumentException(
					"The input of splitting one into n WireArrays must have length = a1.length() + a2.length() + ... + an.length().");
	}

	protected void compute()
	{
		int startIndex = 0;
		Bit[] inputBits = input.getValues();
		for (int i = 0; i < outputs.length; i++)
		{
			Bit[] outputBits = new Bit[outputs[i].owner.length];
			System.arraycopy(inputBits, startIndex, outputBits, 0, outputs[i].owner.length);
			outputs[i].feedSignals(outputBits);
			startIndex += outputs[i].owner.length;
		}
	}

	@Override
	public void update(WireArray initiator, Bit[] oldValues)
	{
		compute();
	}
}
