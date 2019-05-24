package era.mi.logic.components.gates;

import era.mi.logic.types.BitVector.BitVectorMutator;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class OrGate extends MultiInputGate
{
	public OrGate(int processTime, ReadWriteEnd out, ReadEnd... in)
	{
		super(processTime, BitVectorMutator::or, out, in);
	}
}
