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
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class Modeldff4_finewe extends SimpleRectangularHardcodedModelComponent
{
	public Modeldff4_finewe(LogicModelModifiable model, String name)
	{
		super(model, "dff4_finewe", name, "D flip flop\n4 bits", false);
		setSize(35, 90);
		addPin(new Pin(model, this, "C", 1, PinUsage.INPUT, 0, 5), Position.RIGHT);
		addPin(new Pin(model, this, "_WE1", 1, PinUsage.INPUT, 0, 15), Position.RIGHT);
		addPin(new Pin(model, this, "_WE2", 1, PinUsage.INPUT, 0, 25), Position.RIGHT);
		addPin(new Pin(model, this, "_WE3", 1, PinUsage.INPUT, 0, 35), Position.RIGHT);
		addPin(new Pin(model, this, "_WE4", 1, PinUsage.INPUT, 0, 45), Position.RIGHT);
		addPin(new Pin(model, this, "D1", 1, PinUsage.INPUT, 0, 55), Position.RIGHT);
		addPin(new Pin(model, this, "D2", 1, PinUsage.INPUT, 0, 65), Position.RIGHT);
		addPin(new Pin(model, this, "D3", 1, PinUsage.INPUT, 0, 75), Position.RIGHT);
		addPin(new Pin(model, this, "D4", 1, PinUsage.INPUT, 0, 85), Position.RIGHT);
		addPin(new Pin(model, this, "Q1", 1, PinUsage.OUTPUT, 35, 5), Position.LEFT);
		addPin(new Pin(model, this, "Q2", 1, PinUsage.OUTPUT, 35, 15), Position.LEFT);
		addPin(new Pin(model, this, "Q3", 1, PinUsage.OUTPUT, 35, 25), Position.LEFT);
		addPin(new Pin(model, this, "Q4", 1, PinUsage.OUTPUT, 35, 35), Position.LEFT);

		init();
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] QC = castAndInitState(lastState);

		Bit CVal = readEnds.get("C").getValue();

		if (QC[0] == ZERO && CVal == ONE)
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

	@Override
	protected Object getHighLevelState(Object state, String stateID)
	{
		Bit[] QC = castAndInitState(state);

		if ("q".equals(stateID))
			return BitVector.of(Arrays.copyOfRange(QC, 1, 5));
		if (stateID.length() == 2 && stateID.charAt(0) == 'q')
		{
			char secondChar = stateID.charAt(1);
			if (secondChar >= '1' && secondChar <= '4')
				return BitVector.of(QC[secondChar - '0']);
		}
		return super.getHighLevelState(state, stateID);
	}

	@Override
	protected Object setHighLevelState(Object lastState, String stateID, Object newHighLevelState)
	{
		Bit[] QC = castAndInitState(lastState);

		if ("q".equals(stateID))
		{
			BitVector newHighLevelStateCasted = (BitVector) newHighLevelState;
			if (newHighLevelStateCasted.length() != 4)
				throw new IllegalArgumentException("Expected BitVector of length 4, not " + newHighLevelStateCasted.length());
			System.arraycopy(newHighLevelStateCasted.getBits(), 0, QC, 1, 4);
			return QC;
		}
		if (stateID.length() == 2 && stateID.charAt(0) == 'q')
		{
			char secondChar = stateID.charAt(1);
			if (secondChar >= '1' && secondChar <= '4')
			{
				Bit newHighLevelStateCasted;
				if (newHighLevelState instanceof Bit)
					newHighLevelStateCasted = (Bit) newHighLevelState;
				else
				{
					BitVector vector = (BitVector) newHighLevelState;
					if (vector.length() != 1)
						throw new IllegalArgumentException("Expected BitVector of length 1, not " + vector.length());
					newHighLevelStateCasted = vector.getMSBit(0);
				}
				QC[secondChar - '0'] = newHighLevelStateCasted;
				return QC;
			}
		}
		return super.setHighLevelState(QC, stateID, newHighLevelState);
	}

	private static Bit[] castAndInitState(Object state)
	{
		Bit[] QC = (Bit[]) state;
		if (QC == null)
			QC = new Bit[] { U, U, U, U, U };
		return QC;
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(Modeldff4_finewe.class.getCanonicalName(),
				(m, p, n) -> new Modeldff4_finewe(m, n));
	}
}