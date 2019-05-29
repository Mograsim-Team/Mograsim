package mograsim.logic.core.components.gates;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.types.BitVector.BitVectorMutator;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class OrGate extends MultiInputGate
{
	public OrGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::or, out, in);
	}
}
