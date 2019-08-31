package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
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

public class GUIram5_12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUIram5_12(ViewModelModifiable model, String name)
	{
		super(model, name, "RAM\n5 x 12 Bit");
		setSize(40, 40);
		addPin(new Pin(this, "A", 3, PinUsage.INPUT, 10, 0), Position.BOTTOM);
		addPin(new Pin(this, "B", 3, PinUsage.INPUT, 30, 0), Position.BOTTOM);
		addPin(new Pin(this, "WE", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(this, "C", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(this, "Y", 12, PinUsage.OUTPUT, 0, 30), Position.RIGHT);
		addPin(new Pin(this, "D", 12, PinUsage.INPUT, 20, 40), Position.TOP);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		BitVector[] memC = (BitVector[]) lastState;
		if (memC == null)
		{
			memC = new BitVector[6];
			Arrays.fill(memC, 0, 5, BitVector.of(U, 12));
			memC[5] = BitVector.of(U);
		}
		BitVector CVal = readEnds.get("C").getValues();
		BitVector oldC = memC[5];
		// TODO is the timing right?
		if (oldC.getLSBit(0) == ZERO && CVal.getLSBit(0) == ONE && readEnds.get("WE").getValue() == ONE)
		{
			int BInt = getAsInt(readEnds.get("B").getValues());
			if (BInt == -1 || BInt > 4)
				Arrays.fill(memC, BitVector.of(X, 12));
			else if (BInt == -2)
				Arrays.fill(memC, BitVector.of(U, 12));
			else
				memC[BInt] = readEnds.get("D").getValues();
		}
		int AInt = getAsInt(readEnds.get("A").getValues());
		BitVector YVal = AInt == -1 || AInt > 4 ? BitVector.of(X, 12) : AInt == -2 ? BitVector.of(U, 12) : memC[AInt];
		readWriteEnds.get("Y").feedSignals(YVal);
		memC[5] = CVal;
		return memC;
	}

	/**
	 * -1 means X, -2 means U
	 */
	private static int getAsInt(BitVector vect)
	{
		Bit[] bits = vect.getBits();
		for (int i = 0; i < 3; i++)
			if (bits[i] == X)
				return -1;
		for (int i = 0; i < 3; i++)
			if (bits[i] == U)
				return -2;
		for (int i = 0; i < 3; i++)
			if (bits[i] == Z)
				return -1;
		// TODO maybe this is the wrong way around
		return (bits[0] == ONE ? 4 : 0) + (bits[1] == ONE ? 2 : 0) + (bits[2] == ONE ? 1 : 0);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIram5_12.class.getCanonicalName(), (m, p, n) -> new GUIram5_12(m, n));
	}
}