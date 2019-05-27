package era.mi.logic.components.gates;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class AndGate extends MultiInputGate
{
	public AndGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::and, out, in);
	}
}
