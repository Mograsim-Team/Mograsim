package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.wires.WireArray;

public class OrGate extends MultiInputGate
{
	public OrGate(int processTime, WireArray out, WireArray... in)
	{
		super(processTime, Util::or, out, in);
	}
}
