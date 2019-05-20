package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.wires.Wire.WireEnd;

public class AndGate extends MultiInputGate
{
	public AndGate(int processTime, WireEnd out, WireEnd... in)
	{
		super(processTime, Util::and, out, in);
	}
}
