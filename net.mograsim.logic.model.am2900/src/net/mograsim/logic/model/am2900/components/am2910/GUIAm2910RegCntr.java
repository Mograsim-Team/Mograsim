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
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIAm2910RegCntr extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2910RegCntr(ViewModelModifiable model, String name)
	{
		super(model, name, "Register/\nCounter");
		setSize(40, 40);
		addPin(new Pin(this, "D", 12, PinUsage.INPUT, 20, 0), Position.BOTTOM);
		addPin(new Pin(this, "_RLD", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(this, "WE", 1, PinUsage.INPUT, 0, 20), Position.RIGHT);
		addPin(new Pin(this, "DEC", 1, PinUsage.INPUT, 0, 30), Position.RIGHT);
		addPin(new Pin(this, "C", 1, PinUsage.INPUT, 40, 20), Position.LEFT);
		addPin(new Pin(this, "Y", 12, PinUsage.OUTPUT, 20, 40), Position.TOP);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] QC = (Bit[]) lastState;
		if (QC == null)
		{
			QC = new Bit[13];
			Arrays.fill(QC, U);
		}

		ReadEnd D = readEnds.get("D");
		ReadEnd _RLD = readEnds.get("_RLD");
		ReadEnd WE = readEnds.get("WE");
		ReadEnd DEC = readEnds.get("DEC");
		ReadEnd C = readEnds.get("C");
		ReadWriteEnd Y = readWriteEnds.get("Y");

		Bit oldCVal = QC[12];
		Bit CVal = C.getValue();

		// TODO handle U/X/Z
		if (oldCVal == ZERO && CVal == ONE)
		{
			if ((DEC.getValue() == ZERO && WE.getValue() == ONE) || _RLD.getValue() == ZERO)
				System.arraycopy(D.getValues().getBits(), 0, QC, 0, 12);
			else if (WE.getValue() == ONE)
			{
				Bit carry = Bit.ZERO;
				// TODO extract to helper. This code almost also exists in GUIinc12.
				// TODO maybe invert loop direction
				for (int i = 11; i >= 0; i--)
				{
					Bit a = QC[i];
					QC[i] = a.xnor(carry);
					carry = a.or(carry);
				}
			}
		}
		QC[12] = CVal;
		Y.feedSignals(Arrays.copyOfRange(QC, 0, 12));

		return QC;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2910RegCntr.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2910RegCntr(m, n));
	}
}