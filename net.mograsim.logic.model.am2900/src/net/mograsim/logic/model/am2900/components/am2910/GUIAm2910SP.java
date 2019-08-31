package net.mograsim.logic.model.am2900.components.am2910;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIAm2910SP extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2910SP(ViewModelModifiable model, String name)
	{
		super(model, name, "Stack\npointer");
		setSize(40, 30);
		addPin(new Pin(this, "STKI0", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(this, "STKI1", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(this, "C", 1, PinUsage.INPUT, 0, 25), Position.RIGHT);
		addPin(new Pin(this, "A", 3, PinUsage.OUTPUT, 10, 30), Position.TOP);
		addPin(new Pin(this, "B", 3, PinUsage.OUTPUT, 30, 30), Position.TOP);
		addPin(new Pin(this, "_FULL", 1, PinUsage.OUTPUT, 40, 15), Position.LEFT);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		BitAndInt SPC = (BitAndInt) lastState;
		if (SPC == null)
		{
			SPC = new BitAndInt();
			SPC.bit = U;
			SPC.i = -2;
		}
		int SP = SPC.i;

		Bit STKI0Val = readEnds.get("STKI0").getValue();
		Bit STKI1Val = readEnds.get("STKI1").getValue();
		Bit CVal = readEnds.get("C").getValue();
		if (SPC.bit == ZERO && CVal == ONE)
			if (STKI0Val == U && STKI1Val == U)
				SP = -2;
			else if (!STKI0Val.isBinary() || !STKI1Val.isBinary())
				SP = -1;
			else if (STKI0Val == ONE && STKI1Val == ZERO)
			{
				// PUSH
				if (SP >= 0)
					SP = SP == 5 ? 5 : SP + 1;
			} else if (STKI0Val == ZERO && STKI1Val == ONE)
				// CLEAR
				SP = 0;
			else if (STKI0Val == ONE && STKI1Val == ONE)
				// POP
				SP = SP <= 0 ? SP : SP - 1;
		readWriteEnds.get("A").feedSignals(getAsBitVector(SP == 0 ? 7 : SP - 1));
		readWriteEnds.get("B").feedSignals(getAsBitVector(SP == 5 ? 4 : SP));
		readWriteEnds.get("_FULL").feedSignals(SP == -2 ? U : SP == -1 ? X : SP == 5 ? ZERO : ONE);

		SPC.i = SP;
		SPC.bit = CVal;
		return SPC;
	}

	private static class BitAndInt
	{
		Bit bit;
		int i;
	}

	/**
	 * -1 means X, -2 means U
	 */
	private static BitVector getAsBitVector(int i)
	{
		if (i == -1)
			return BitVector.of(X, 3);
		if (i == -2)
			return BitVector.of(U, 3);
		// TODO maybe this is the wrong way around
		return BitVector.of((i & 0b100) > 0 ? ONE : ZERO, (i & 0b10) > 0 ? ONE : ZERO, (i & 0b1) > 0 ? ONE : ZERO);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2910SP.class.getCanonicalName(), (m, p, n) -> new GUIAm2910SP(m, n));
	}
}