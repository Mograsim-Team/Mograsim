package era.mi.logic.components;

import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;
import era.mi.logic.wires.WireArrayObserver;

public class Mapper implements WireArrayObserver
{
    private int[] inBeginningIndex, outBeginningIndex;
    private WireArray[] inputs;
    private WireArrayInput[] outputsI;

    public Mapper(WireArray[] inputs, WireArray[] outputs)
    {
	this.inputs = inputs.clone();

	int beginningIndex = 0;
	for (int i = 0; i < inputs.length; i++)
	{
	    inBeginningIndex[i] = beginningIndex;
	    beginningIndex += inputs[i].length;
	    inputs[i].addObserver(this);
	}
	int inputsLength = beginningIndex;
	beginningIndex = 0;
	for (int i = 0; i < outputs.length; i++)
	{
	    outputsI[i] = outputs[i].createInput();
	    outBeginningIndex[i] = beginningIndex;
	    beginningIndex += outputs[i].length;
	}
	if (inputsLength != beginningIndex)
	    throw new IllegalArgumentException("Mapper inputs must add up to the same length as outputs. ("
		    + inputsLength + ", " + beginningIndex + ").");
    }

    @Override
    public void update(WireArray initiator)
    {
	for (int i = 0; i < inputs.length; i++)
	{
	    if (inputs[i] == initiator)
	    {
		int inB = inBeginningIndex[i];
//		int outB = outBeginningIndex TODO
	    }
	}
    }

    // binary search
    private int findLower(int[] sorted, int value)
    {
	int a = 0, b = sorted.length;
	while (a < (b - 1))
	{
	    int inspect = (a + b) >> 1;
	    if (sorted[inspect] == value)
		return inspect;
	    if (sorted[inspect] > value)
		b = inspect;
	    else
		a = inspect;
	}
	return a;
    }
}
