package net.mograsim.logic.core.components.gates;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

/**
 * Outputs 1 when the number of 1 inputs is odd.
 * 
 * @author Fabian Stemmler
 */
public class CoreXorGate extends MultiInputCoreGate
{
	public CoreXorGate(Timeline timeline, int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(timeline, processTime, BitVectorMutator::xor, out, in);
	}

}
