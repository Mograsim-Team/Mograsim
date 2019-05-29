package mograsim.logic.core.components.gates;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.types.BitVector.BitVectorMutator;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;

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
