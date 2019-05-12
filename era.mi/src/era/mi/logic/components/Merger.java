package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;
import era.mi.logic.wires.WireArrayObserver;

public class Merger implements WireArrayObserver, Component
{
    private WireArrayInput outI;
    private WireArray[] inputs;
    private int[] beginningIndex;

    /**
     * 
     * @param union  The output of merging n {@link WireArray}s into one. Must have
     *               length = a1.length() + a2.length() + ... + an.length().
     * @param inputs The inputs to be merged into the union
     */
    public Merger(WireArray union, WireArray... inputs)
    {
	this.inputs = inputs;
	this.outI = union.createInput();
	this.beginningIndex = new int[inputs.length];

	int length = 0;
	for (int i = 0; i < inputs.length; i++)
	{
	    beginningIndex[i] = length;
	    length += inputs[i].length;
	    inputs[i].addObserver(this);
	}

	if (length != union.length)
	    throw new IllegalArgumentException(
		    "The output of merging n WireArrays into one must have length = a1.length() + a2.length() + ... + an.length().");
    }

    public WireArray getInput(int index)
    {
	return inputs[index];
    }

    public WireArray getUnion()
    {
	return outI.owner;
    }

    @Override
    public void update(WireArray initiator, Bit[] oldValues)
    {
	int index = find(initiator);
	int beginning = beginningIndex[index];
	outI.feedSignals(beginning, initiator.getValues());
    }

    private int find(WireArray w)
    {
	for (int i = 0; i < inputs.length; i++)
	    if (inputs[i] == w)
		return i;
	return -1;
    }

    public WireArray[] getInputs()
    {
	return inputs.clone();
    }

	@Override
	public List<WireArray> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(inputs));
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(outI.owner));
	}
}
