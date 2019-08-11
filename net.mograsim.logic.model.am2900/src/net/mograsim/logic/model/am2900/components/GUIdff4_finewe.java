package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIdff4_finewe extends SimpleRectangularHardcodedGUIComponent
{
	public GUIdff4_finewe(ViewModelModifiable model, String name)
	{
		super(model, name, "D flip flop\n4 bits");
		setSize(35, 100);
		addPin(new Pin(this, "C", 1, 0, 5), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_WE1", 1, 0, 15), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_WE2", 1, 0, 25), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_WE3", 1, 0, 35), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_WE4", 1, 0, 45), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_WE", 1, 0, 55), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "D1", 1, 0, 65), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "D2", 1, 0, 75), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "D3", 1, 0, 85), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "D4", 1, 0, 95), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "Q1", 1, 35, 5), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "Q2", 1, 35, 15), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "Q3", 1, 35, 25), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "Q4", 1, 35, 35), Usage.OUTPUT, Position.LEFT);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] QC = (Bit[]) lastState;
		if (QC == null)
			QC = new Bit[] { U, U, U, U, U };

		Bit CVal = readEnds.get("C").getValue();

		if (QC[0] == ZERO && CVal == ONE && readEnds.get("_WE").getValue() == ZERO)
			for (int i = 1; i < 5; i++)
			{
				Bit WEiVal = readEnds.get("_WE" + i).getValue();
				if (WEiVal == X || WEiVal == Z)
					QC[i] = X;
				else if (WEiVal == U)
					QC[i] = U;
				else if (WEiVal == ZERO)
					QC[i] = readEnds.get("D" + i).getValue();
			}

		QC[0] = CVal;

		readWriteEnds.get("Q1").feedSignals(QC[1]);
		readWriteEnds.get("Q2").feedSignals(QC[2]);
		readWriteEnds.get("Q3").feedSignals(QC[3]);
		readWriteEnds.get("Q4").feedSignals(QC[4]);

		return QC;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIdff4_finewe.class.getCanonicalName(), (m, p, n) -> new GUIdff4_finewe(m, n));
	}
}