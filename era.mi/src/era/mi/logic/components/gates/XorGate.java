package era.mi.logic.components.gates;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

/**
 * Outputs 1 when the number of 1 inputs is odd.
 * 
 * @author Fabian Stemmler
 */
public class XorGate extends MultiInputGate
{
	public XorGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::xor, out, in);
	}

}
