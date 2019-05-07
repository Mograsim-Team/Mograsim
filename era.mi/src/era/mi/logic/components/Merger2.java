package era.mi.logic.components;

import era.mi.logic.WireArray;
import era.mi.logic.WireArrayObserver;

public class Merger2 implements WireArrayObserver
{
	private WireArray out;
	private WireArray[] inputs;
	private int[] beginningIndex;
	
	/**
	 * 
	 * @param union The output of merging n {@link WireArray}s into one. Must have length = a1.length() + a2.length() + ... + an.length().
	 * @param inputs The inputs to be merged into the union
	 */
	public Merger2(WireArray union, WireArray... inputs)
	{
		this.inputs = inputs;
		this.out = union;
		this.beginningIndex = new int[inputs.length];
		
		int length = 0;
		for(int i = 0; i < inputs.length; i++)
		{
			beginningIndex[i] = length;
			length += inputs[i].length();
			inputs[i].addObserver(this);
		}
			
		if(length != union.length())
			throw new IllegalArgumentException("The output of merging n WireArrays into one must have length = a1.length() + a2.length() + ... + an.length().");
	}

	public WireArray getInput(int index)
	{
		return inputs[index];
	}
	
	public WireArray getUnion()
	{
		return out;
	}
	
	@Override
	public void update(WireArray initiator)
	{
		int index = find(initiator);
		int beginning = beginningIndex[index];
		out.feedSignals(beginning, initiator.getValues());
	}
	
	private int find(WireArray w)
	{
		for(int i = 0; i < inputs.length; i++)
			if(inputs[i] == w)
				return i;
		return -1;
	}

	public WireArray getOut()
	{
		return out;
	}

	public WireArray[] getInputs()
	{
		return inputs.clone();
	}
}
