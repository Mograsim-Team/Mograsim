package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.wires.WireArray;

public class AndGate extends MultiInputGate
{
	public AndGate(int processTime, WireArray out, WireArray... in)
	{
		super(processTime, Util::and, out, in);
	}
}
