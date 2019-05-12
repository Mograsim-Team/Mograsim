package era.mi.logic.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

/**
 * Models a multiplexer. Takes an arbitrary amount of input {@link WireArray}s, one of which,
 * as determined by select, is put through to the output.
 * @author Fabian Stemmler
 *
 */
public class Mux extends BasicComponent
{
	private WireArray select;
	private WireArrayInput outI;
	private WireArray[] inputs;
	private final int outputSize;
	/**
	 * Input {@link WireArray}s and out must be of uniform length
	 * @param out Must be of uniform length with all inputs.
	 * @param select Indexes the input array which is to be mapped to the output. Must have enough bits
	 * to index all inputs.
	 * @param inputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public Mux(int processTime, WireArray out, WireArray select, WireArray... inputs)
	{
		super(processTime);
		outputSize = out.length;
		
		this.inputs = inputs.clone();
		for(int i = 0; i < this.inputs.length; i++)
		{
			if(inputs[i].length != outputSize)
				throw new IllegalArgumentException("All MUX wire arrays must be of uniform length!");
			inputs[i].addObserver(this);
		}
		
		this.select = select;
		select.addObserver(this);
		
		int maxInputs = 1 << select.length;
		if(this.inputs.length > maxInputs)
			throw new IllegalArgumentException("There are more inputs ("
					+ this.inputs.length + ") to the MUX than supported by "
					+ select.length + " select bits (" + maxInputs + ").");
		
		outI = out.createInput();
	}
	
	public WireArray getOut()
	{
		return outI.owner;
	}

	public WireArray getSelect()
	{
		return select;
	}

	@Override
	public void compute() {
		int selectValue;
		if(!select.hasNumericValue() || (selectValue = (int) select.getUnsignedValue()) >= inputs.length)
		{
			outI.clearSignals();
			return;
		}
		
		WireArray active = inputs[selectValue];
		outI.feedSignals(active.getValues());
	}

	@Override
	public List<WireArray> getAllInputs()
	{
		ArrayList<WireArray> wires = new ArrayList<WireArray>(Arrays.asList(inputs));
		wires.add(select);
		return Collections.unmodifiableList(wires);
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(outI.owner));
	}
}
