package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreUnidirectionalMerger extends CoreComponent implements LogicObserver
{
	private ReadWriteEnd out;
	private ReadEnd[] inputs;
	private int[] beginningIndex;

	/**
	 * 
	 * @param union  The output of merging n {@link CoreWire}s into one. Must have width = a1.width() + a2.width() + ... + an.width().
	 * @param inputs The inputs to be merged into the union
	 */
	public CoreUnidirectionalMerger(Timeline timeline, ReadWriteEnd union, ReadEnd... inputs)
	{
		super(timeline);
		this.inputs = inputs;
		this.out = union;
		this.beginningIndex = new int[inputs.length];

		int width = 0;
		for (int i = 0; i < inputs.length; i++)
		{
			beginningIndex[i] = width;
			width += inputs[i].width();
			inputs[i].registerObserver(this);
		}

		if (width != union.width())
			throw new IllegalArgumentException(
					"The output of merging n WireArrays into one must have width = a1.width() + a2.width() + ... + an.width().");
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
	public void update(LogicObservable initiator)
	{
		int index = find(initiator);
		int beginning = beginningIndex[index];
		out.feedSignals(beginning, inputs[index].getValues());
	}

	private int find(LogicObservable r)
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
