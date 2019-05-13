package era.mi.logic.components.gates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

public abstract class MultiInputGate extends BasicComponent
{
	protected WireArray[] in;
	protected WireArray out;
	protected WireArrayInput outI;
	protected final int length;
	protected Operation op;
	
	protected MultiInputGate(int processTime, Operation op, WireArray out, WireArray... in)
	{
		super(processTime);
		this.op = op;
		length = out.length;
		this.in = in.clone();
		if(in.length < 1)
			throw new IllegalArgumentException(String.format("Cannot create gate with %d wires.", in.length));
		for(WireArray w : in)
		{
			if(w.length != length)
				throw new IllegalArgumentException("All wires connected to the gate must be of uniform length.");
			w.addObserver(this);
		}
		this.out = out;
		outI = out.createInput();
	}


	@Override
	public List<WireArray> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(in));
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(out));
	}
	
	protected void compute()
	{
		Bit[] result = in[0].getValues();
		for(int i = 1; i < in.length; i++)
			result = op.execute(result, in[i].getValues());
		outI.feedSignals(result);
	}
	
	protected interface Operation
	{
		public Bit[] execute(Bit[] a, Bit[] b);
	}
}
