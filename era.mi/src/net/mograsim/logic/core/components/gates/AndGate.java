package net.mograsim.logic.core.components.gates;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class AndGate extends MultiInputGate
{
	public AndGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::and, out, in);
	}
}
