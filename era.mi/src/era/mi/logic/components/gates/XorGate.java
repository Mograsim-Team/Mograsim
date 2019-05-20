package era.mi.logic.components.gates;

import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.WireEnd;

/**
 * Outputs 1 when the number of 1 inputs is odd.
 * 
 * @author Fabian Stemmler
 */
public class XorGate extends MultiInputGate
{
	public XorGate(int processTime, WireEnd out, WireEnd... in)
	{
		super(processTime, BitVectorMutator::xor, out, in);
	}

}
