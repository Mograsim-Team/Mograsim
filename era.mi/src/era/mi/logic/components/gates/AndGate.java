package era.mi.logic.components.gates;

import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.WireEnd;

public class AndGate extends MultiInputGate
{
	public AndGate(int processTime, WireEnd out, WireEnd... in)
	{
		super(processTime, BitVectorMutator::and, out, in);
	}
}
