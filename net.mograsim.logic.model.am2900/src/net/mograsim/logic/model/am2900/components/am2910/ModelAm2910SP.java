package net.mograsim.logic.model.am2900.components.am2910;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class ModelAm2910SP extends SimpleRectangularHardcodedModelComponent
{
	public ModelAm2910SP(LogicModelModifiable model, String name)
	{
		super(model, "Am2910SP", name, "Stack\npointer");
		setSize(40, 30);
		addPin(new Pin(model, this, "STKI0", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(model, this, "STKI1", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(model, this, "C", 1, PinUsage.INPUT, 0, 25), Position.RIGHT);
		addPin(new Pin(model, this, "A", 3, PinUsage.OUTPUT, 10, 30), Position.TOP);
		addPin(new Pin(model, this, "B", 3, PinUsage.OUTPUT, 30, 30), Position.TOP);
		addPin(new Pin(model, this, "_FULL", 1, PinUsage.OUTPUT, 40, 15), Position.LEFT);
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
		readWriteEnds.get("A").feedSignals(getAsBitVector(SP == 0 ? 7 : SP < 0 ? SP : SP - 1));
		readWriteEnds.get("B").feedSignals(getAsBitVector(SP == 5 ? 4 : SP));
		readWriteEnds.get("_FULL").feedSignals(SP == -2 ? U : SP == -1 ? X : SP == 5 ? ZERO : ONE);

		SPC.i = SP;
		SPC.bit = CVal;
		return SPC;
	}

	@Override
	protected Object getHighLevelState(Object state, String stateID)
	{
		switch (stateID)
		{
		case "q":
			return getAsBitVector(((BitAndInt) state).i);
		default:
			return super.getHighLevelState(state, stateID);
		}
	}

	@Override
	protected Object setHighLevelState(Object lastState, String stateID, Object newHighLevelState)
	{
		switch (stateID)
		{
		case "q":
			int i;
			BitVector newHighLevelStateCasted = (BitVector) newHighLevelState;
			if (newHighLevelStateCasted.length() != 3)
				throw new IllegalArgumentException("Expected BitVector of length 3, not " + newHighLevelStateCasted.length());
			if (newHighLevelStateCasted.isBinary())
				i = newHighLevelStateCasted.getUnsignedValue().intValue();
			else
				i = -1;// this makes setting to U impossible
			if (i > 5)
				throw new IllegalArgumentException("Given value not in range (0-5 incl.): " + i);
			((BitAndInt) lastState).i = i;
			return lastState;
		default:
			return super.setHighLevelState(lastState, stateID, newHighLevelState);
		}
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
		return BitVector.of((i & 0b100) > 0 ? ONE : ZERO, (i & 0b10) > 0 ? ONE : ZERO, (i & 0b1) > 0 ? ONE : ZERO);
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2910SP.class.getCanonicalName(), (m, p, n) -> new ModelAm2910SP(m, n));
	}
}