package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.components.BasicComponent;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.types.MutationOperation;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public abstract class MultiInputGate extends BasicComponent
{
	protected ReadEnd[] in;
	protected ReadWriteEnd out;
	protected final int length;
	protected MutationOperation op;

	protected MultiInputGate(Timeline timeline, int processTime, MutationOperation op, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime);
		this.op = op;
		length = out.length();
		this.in = in.clone();
		if (in.length < 1)
			throw new IllegalArgumentException(String.format("Cannot create gate with %d wires.", in.length));
		for (ReadEnd w : in)
		{
			if (w.length() != length)
				throw new IllegalArgumentException("All wires connected to the gate must be of uniform length.");
			w.addObserver(this);
		}
		this.out = out;
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}

	@Override
	protected void compute()
	{
		BitVectorMutator mutator = BitVectorMutator.empty();
		for (ReadEnd w : in)
			op.apply(mutator, w.getValues());
		out.feedSignals(mutator.get());
	}
}
