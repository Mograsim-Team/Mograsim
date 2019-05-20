package era.mi.logic.components.gates;

import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.WireEnd;

public class OrGate extends MultiInputGate
{
	public OrGate(int processTime, WireEnd out, WireEnd... in)
	{
		super(processTime, BitVectorMutator::or, out, in);
	}
}
