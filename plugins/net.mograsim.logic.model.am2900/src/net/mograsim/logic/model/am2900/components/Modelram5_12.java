package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Modelram5_12 extends SimpleRectangularHardcodedModelComponent
{
	public Modelram5_12(LogicModelModifiable model, String name)
	{
		super(model, "ram5_12", name, "RAM\n5 x 12 Bit", false);
		setSize(40, 40);
		addPin(new Pin(model, this, "A", 3, PinUsage.INPUT, 10, 0), Position.BOTTOM);
		addPin(new Pin(model, this, "B", 3, PinUsage.INPUT, 30, 0), Position.BOTTOM);
		addPin(new Pin(model, this, "WE", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(model, this, "C", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(model, this, "Y", 12, PinUsage.OUTPUT, 0, 30), Position.RIGHT);
		addPin(new Pin(model, this, "D", 12, PinUsage.INPUT, 20, 40), Position.TOP);

		init();
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		BitVector[] memC = castAndInitState(lastState);

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
		return (bits[0] == ONE ? 4 : 0) + (bits[1] == ONE ? 2 : 0) + (bits[2] == ONE ? 1 : 0);
	}

	Pattern stateIDPattern = Pattern.compile("c(0[10][10]|100).q");

	@Override
	protected Object getHighLevelState(Object state, String stateID)
	{
		BitVector[] memC = castAndInitState(state);

		Matcher m = stateIDPattern.matcher(stateID);
		if (m.matches())
			return memC[Integer.parseInt(m.group(1), 2)];
		return super.getHighLevelState(memC, stateID);
	}

	@Override
	protected Object setHighLevelState(Object lastState, String stateID, Object newHighLevelState)
	{
		BitVector[] memC = castAndInitState(lastState);

		Matcher m = stateIDPattern.matcher(stateID);
		if (m.matches())
		{
			int addr = Integer.parseInt(m.group(1), 2);
			BitVector newHighLevelStateCasted = (BitVector) newHighLevelState;
			if (newHighLevelStateCasted.length() != 12)
				throw new IllegalArgumentException("Expected BitVector of length 12, not " + newHighLevelStateCasted.length());
			memC[addr] = newHighLevelStateCasted;
			return memC;
		}
		return super.setHighLevelState(memC, stateID, newHighLevelState);
	}

	private static BitVector[] castAndInitState(Object state)
	{
		BitVector[] memC = (BitVector[]) state;
		if (memC == null)
		{
			memC = new BitVector[6];
			Arrays.fill(memC, 0, 5, BitVector.of(U, 12));
			memC[5] = BitVector.of(U);
		}
		return memC;
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(Modelram5_12.class.getCanonicalName(), (m, p, n) -> new Modelram5_12(m, n));
	}
}