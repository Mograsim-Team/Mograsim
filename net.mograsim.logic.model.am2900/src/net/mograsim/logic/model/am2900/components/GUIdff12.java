package net.mograsim.logic.model.am2900.components;

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
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIdff12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUIdff12(ViewModelModifiable model, String name)
	{
		super(model, name, "D flip flop\n12 bits");
		setSize(40, 20);
		addPin(new Pin(this, "D", 12, 20, 20), Usage.INPUT, Position.TOP);
		addPin(new Pin(this, "C", 1, 0, 10), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "Y", 12, 20, 0), Usage.OUTPUT, Position.BOTTOM);
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

		Bit CVal = readEnds.get("C").getValue();

		if (QC[12] == ZERO && CVal == ONE)
			System.arraycopy(readEnds.get("D").getValues().getBits(), 0, QC, 0, 12);

		readWriteEnds.get("Y").feedSignals(Arrays.copyOfRange(QC, 0, 12));
		QC[12] = CVal;

		return QC;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIdff12.class.getCanonicalName(), (m, p, n) -> new GUIdff12(m, n));
	}
}