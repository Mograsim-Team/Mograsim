package net.mograsim.logic.core.components.gates;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreNandGate extends MultiInputCoreGate
{
	public CoreNandGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::and, true, out, in);
	}
}
