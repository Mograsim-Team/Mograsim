package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.components.BasicComponent;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire.WireEnd;

public abstract class MultiInputGate extends BasicComponent
{
	protected WireEnd[] in;
	protected WireEnd out;
	protected final int length;
	protected Operation op;

	protected MultiInputGate(int processTime, Operation op, WireEnd out, WireEnd... in)
	{
		super(processTime);
		this.op = op;
		length = out.length();
		this.in = in.clone();
		if (in.length < 1)
			throw new IllegalArgumentException(String.format("Cannot create gate with %d wires.", in.length));
		for (WireEnd w : in)
		{
			if (w.length() != length)
				throw new IllegalArgumentException("All wires connected to the gate must be of uniform length.");
			w.addObserver(this);
		}
		this.out = out;
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(out);
	}

	@Override
	protected void compute()
	{
		Bit[] result = in[0].getValues();
		for (int i = 1; i < in.length; i++)
			result = op.execute(result, in[i].getValues());
		out.feedSignals(result);
	}

	protected interface Operation
	{
		public Bit[] execute(Bit[] a, Bit[] b);
	}
}
