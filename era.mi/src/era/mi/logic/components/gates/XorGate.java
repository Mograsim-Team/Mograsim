package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.wires.WireArray;

/**
 * Outputs 1 when the number of 1 inputs is odd.
 * 
 * @author Fabian Stemmler
 */
public class XorGate extends MultiInputGate {
	public XorGate(int processTime, WireArray out, WireArray... in) {
		super(processTime, Util::xor, out, in);
	}

}
