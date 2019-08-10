package net.mograsim.logic.model.am2900.components.am2910;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIAm2910RegCntr extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2910RegCntr(ViewModelModifiable model, String name)
	{
		super(model, name, "Register/\nCounter");
		setSize(40, 40);
		addPin(new Pin(this, "D", 12, 20, 0), Usage.INPUT, Position.BOTTOM);
		addPin(new Pin(this, "_RLD", 1, 0, 5), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "RWE", 1, 0, 20), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "RDEC", 1, 0, 30), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "C", 1, 40, 20), Usage.INPUT, Position.LEFT);
		addPin(new Pin(this, "Y", 12, 20, 40), Usage.OUTPUT, Position.TOP);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] QC = (Bit[]) lastState;
		if (QC == null)
		{
			QC = new Bit[13];
			Arrays.fill(QC, U);
		}

		ReadEnd D = readEnds.get("D");
		ReadEnd _RLD = readEnds.get("_RLD");
		ReadEnd RWE = readEnds.get("RWE");
		ReadEnd RDEC = readEnds.get("RDEC");
		ReadEnd C = readEnds.get("C");
		ReadWriteEnd Y = readWriteEnds.get("Y");

		Bit oldCVal = QC[12];
		Bit CVal = C.getValue();

		if (oldCVal == ZERO && CVal == ONE && _RLD.getValue() == ZERO && RWE.getValue() == ONE)
		{
			if (RDEC.getValue() == ONE)
			{
				Bit carry = Bit.ZERO;
				// TODO maybe invert loop direction
				for (int i = 12; i >= 0; i--)
				{
					Bit a = QC[i];
					Bit z;
					if (a.isBinary() && carry.isBinary())
					{
						boolean aBool = a == ONE;
						boolean carryBool = carry == ONE;
						z = !aBool ^ carryBool ? ONE : ZERO;
						carry = aBool || carryBool ? ONE : ZERO;
					} else
					{
						carry = carry.join(a);
						z = carry;
					}
					QC[i] = z;
				}
			} else
				System.arraycopy(D.getValues().getBits(), 0, QC, 0, 12);
		}
		QC[12] = CVal;
		Y.feedSignals(Arrays.copyOfRange(QC, 0, 12));

		return QC;
	}
}