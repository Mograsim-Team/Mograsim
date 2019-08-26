package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class Splitter extends Component implements LogicObserver
{
	private ReadEnd input;
	private ReadWriteEnd[] outputs;

	public Splitter(Timeline timeline, ReadEnd input, ReadWriteEnd... outputs)
	{
		super(timeline);
		this.input = input;
		this.outputs = outputs;
		input.registerObserver(this);
		int width = 0;
		for (ReadEnd out : outputs)
			width += out.width();

		if (input.width() != width)
			throw new IllegalArgumentException(
					"The input of splitting one into n WireArrays must have width = a1.width() + a2.width() + ... + an.width().");
	}

	protected void compute()
	{
		BitVector inputBits = input.getValues();
		int startIndex = 0;
		for (int i = 0; i < outputs.length; i++)
		{
			outputs[i].feedSignals(inputBits.subVector(startIndex, startIndex + outputs[i].width()));
			startIndex += outputs[i].width();
		}
	}

	@Override
	public void update(LogicObservable initiator)
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
