package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIsel4_12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUIsel4_12(ViewModelModifiable model, String name)
	{
		super(model, "GUIsel4_12", name, "4-way SEL\n12 bit");
		setSize(80, 40);
		addPin(new Pin(this, "SA", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(this, "SB", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(this, "SC", 1, PinUsage.INPUT, 0, 25), Position.RIGHT);
		addPin(new Pin(this, "SD", 1, PinUsage.INPUT, 0, 35), Position.RIGHT);
		addPin(new Pin(this, "A", 12, PinUsage.INPUT, 10, 0), Position.BOTTOM);
		addPin(new Pin(this, "B", 12, PinUsage.INPUT, 30, 0), Position.BOTTOM);
		addPin(new Pin(this, "C", 12, PinUsage.INPUT, 50, 0), Position.BOTTOM);
		addPin(new Pin(this, "D", 12, PinUsage.INPUT, 70, 0), Position.BOTTOM);
		addPin(new Pin(this, "Y", 12, PinUsage.OUTPUT, 40, 40), Position.TOP);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit SAVal = readEnds.get("SA").getValue();
		Bit SBVal = readEnds.get("SB").getValue();
		Bit SCVal = readEnds.get("SC").getValue();
		Bit SDVal = readEnds.get("SD").getValue();
		BitVector YVal;
		if (SAVal == X || SBVal == X || SCVal == X || SDVal == X)
			YVal = BitVector.of(X, 12);
		else if (SAVal == U || SBVal == U || SCVal == U || SDVal == U)
			YVal = BitVector.of(U, 12);
		else if (SAVal == Z || SBVal == Z || SCVal == Z || SDVal == Z)
			YVal = BitVector.of(X, 12);
		else
		{
			YVal = null;
			if (SAVal == ONE)
				YVal = readEnds.get("A").getValues();
			if (SBVal == ONE)
				if (YVal != null)
					YVal = BitVector.of(X, 12);
				else
					YVal = readEnds.get("B").getValues();
			if (SCVal == ONE)
				if (YVal != null)
					YVal = BitVector.of(X, 12);
				else
					YVal = readEnds.get("C").getValues();
			if (SDVal == ONE)
				if (YVal != null)
					YVal = BitVector.of(X, 12);
				else
					YVal = readEnds.get("D").getValues();
			if (YVal == null)
				YVal = BitVector.of(ZERO, 12);
		}

		readWriteEnds.get("Y").feedSignals(YVal);
		return null;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIsel4_12.class.getCanonicalName(), (m, p, n) -> new GUIsel4_12(m, n));
	}
}