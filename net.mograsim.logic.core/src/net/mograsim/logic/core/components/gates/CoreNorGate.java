package net.mograsim.logic.core.components.gates;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreNorGate extends MultiInputCoreGate
{
	public CoreNorGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::or, true, out, in);
	}
}
