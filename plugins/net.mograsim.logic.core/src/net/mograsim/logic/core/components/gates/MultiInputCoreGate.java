package net.mograsim.logic.core.components.gates;

import java.util.List;

import net.mograsim.logic.core.components.BasicCoreComponent;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.core.types.MutationOperation;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public abstract class MultiInputCoreGate extends BasicCoreComponent
{
	protected ReadEnd[] in;
	protected ReadWriteEnd out;
	protected final int width;
	protected MutationOperation op;
	protected boolean invert = false;

	protected MultiInputCoreGate(Timeline timeline, int processTime, MutationOperation op, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime);
		this.op = op;
		width = out.width();
		this.in = in.clone();
		if (in.length < 1)
			throw new IllegalArgumentException(String.format("Cannot create gate with %d wires.", in.length));
		for (ReadEnd w : in)
		{
			if (w.width() != width)
				throw new IllegalArgumentException("All wires connected to the gate must be of uniform length.");
			w.registerObserver(this);
		}
		this.out = out;
	}

	protected MultiInputCoreGate(Timeline timeline, int processTime, MutationOperation op, boolean invert, ReadWriteEnd out, ReadEnd... in)
	{
		this(timeline, processTime, op, out, in);
		this.invert = invert;
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
	public TimelineEventHandler compute()
	{
		BitVectorMutator mutator = BitVectorMutator.empty();
		for (ReadEnd w : in)
			op.apply(mutator, w.getValues());
		return e -> out.feedSignals(invert ? mutator.toBitVector().not() : mutator.toBitVector());
	}
}
