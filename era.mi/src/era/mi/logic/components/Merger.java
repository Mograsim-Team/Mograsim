package era.mi.logic.components;

import era.mi.logic.Util;
import era.mi.logic.Bit;
import era.mi.logic.WireArray;
import era.mi.logic.WireArrayObserver;

@Deprecated
public class Merger implements WireArrayObserver
{
	private WireArray out;
	private WireArray[] inputs;
	
	//TODO: General problem with this concept; New inputs coming in at the same time override each other
	
	/**
	 * 
	 * @param union The output of merging n {@link WireArray}s into one. Must have length = a1.length() + a2.length() + ... + an.length().
	 * @param inputs The inputs to be merged into the union
	 */
	public Merger(WireArray union, WireArray... inputs)
	{
		this.inputs = inputs;
		this.out = union;
		
		int length = 0;
		for(WireArray input : inputs)
		{
			length += input.length();
			input.addObserver(this);
		}
			
		if(length != union.length())
			throw new IllegalArgumentException("The output of merging n WireArrays into one must have length = a1.length() + a2.length() + ... + an.length().");
	}

	protected void compute()
	{
		Bit[][] bits = new Bit[inputs.length][];
		for(int i = 0; i < inputs.length; i++)
			bits[i] = inputs[i].getValues();
		Bit[] newOut = Util.concat(bits);
		out.feedSignals(newOut);
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
		compute(); //No inner delay
	}
}
