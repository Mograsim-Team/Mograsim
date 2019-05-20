package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.components.BasicComponent;
import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.types.MutationOperation;
import era.mi.logic.wires.Wire.WireEnd;

public abstract class MultiInputGate extends BasicComponent
{
	protected WireEnd[] in;
	protected WireEnd out;
	protected final int length;
	protected MutationOperation op;

	protected MultiInputGate(int processTime, MutationOperation op, WireEnd out, WireEnd... in)
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
		BitVectorMutator mutator = BitVectorMutator.empty();
		for (WireEnd w : in)
			op.apply(mutator, w.getValues());
		out.feedSignals(mutator.get());
	}
}
