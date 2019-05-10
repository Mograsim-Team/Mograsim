package era.mi.logic.components;

import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;
import era.mi.logic.wires.WireArrayObserver;

/**
 * Models a Multiplexer. A is selected when select bit is 1, B when select bit is 0. Outputs X otherwise.
 * @author Fabian Stemmler
 *
 */
public class Mux2 implements WireArrayObserver
{
	private WireArray select;
	private WireArrayInput outI;
	private WireArrayInput[] inputs;
	private final int size;
	private final int processTime;
	private int selected;
	
	/**
	 * {@link WireArray}s a, b and out must be of uniform length, select
	 * @param out Must be of uniform length with a and b.
	 * @param select Indexes the input array which is to be mapped to the output
	 * @param inputs One of these inputs is mapped to the output, depending on the select wires 
	 */
	public Mux2(int processTime, WireArray out, WireArray select, WireArray... inputs)
	{
		this.processTime = processTime;
		size = out.length;
		
		this.inputs = new WireArrayInput[inputs.length];
		for(int i = 0; i < this.inputs.length; i++)
		{
			if(inputs[i].length != size)
				throw new IllegalArgumentException("All MUX wire arrays must be of uniform length!");
			this.inputs[i] = inputs[i].createInput();
			inputs[i].addObserver(this);
		}
		
		this.select = select;
		select.addObserver(this);
		selected = -1;
		
		int maxInputs = 1 << select.length;
		if(this.inputs.length > maxInputs)
			throw new IllegalArgumentException("There are more inputs ("
					+ this.inputs.length + ") to the MUX than supported by "
					+ select.length + " select bits (" + maxInputs + ").");
		
		outI = out.createInput();
		out.addObserver(this);
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
	public void update(WireArray initiator) {
		int selectValue;
		if(!select.hasNumericValue() || (selectValue = (int) select.getUnsignedValue()) > size)
		{
			if(initiator == select)
			{
				Simulation.TIMELINE.addEvent((e) -> {
					if(selected != -1)
					{
						inputs[selected].clearSignals();
						selected = -1;
						outI.clearSignals();
					}
				}, processTime);
			}
			return;
		}
		
		WireArrayInput active = inputs[selectValue];
		Simulation.TIMELINE.addEvent((e) -> {
			if(initiator == select)
			{
				if(selected != -1)
					inputs[selected].clearSignals();
				selected = selectValue;
				active.feedSignals(outI.owner.getValues());
				outI.feedSignals(active.owner.getValues());
			}
			else if(initiator == outI.owner)
				active.feedSignals(outI.owner.getValues());
			else if(initiator == active.owner)
				outI.feedSignals(active.owner.getValues());
		}, processTime);
	}
}
