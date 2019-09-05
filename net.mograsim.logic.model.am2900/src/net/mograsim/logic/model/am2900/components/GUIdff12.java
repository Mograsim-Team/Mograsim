package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
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

public class GUIdff12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUIdff12(ViewModelModifiable model, String name)
	{
		super(model, "GUIdff12", name, "D flip flop\n12 bits");
		setSize(40, 20);
		addPin(new Pin(this, "D", 12, PinUsage.INPUT, 20, 20), Position.TOP);
		addPin(new Pin(this, "C", 1, PinUsage.INPUT, 0, 10), Position.RIGHT);
		addPin(new Pin(this, "Y", 12, PinUsage.OUTPUT, 20, 0), Position.BOTTOM);
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

		Bit CVal = readEnds.get("C").getValue();

		if (QC[12] == ZERO && CVal == ONE)
			System.arraycopy(readEnds.get("D").getValues().getBits(), 0, QC, 0, 12);

		readWriteEnds.get("Y").feedSignals(Arrays.copyOfRange(QC, 0, 12));
		QC[12] = CVal;

		return QC;
	}

	@Override
	protected Object getHighLevelState(Object state, String stateID)
	{
		switch (stateID)
		{
		case "q":
			return BitVector.of(Arrays.copyOfRange((Bit[]) state, 0, 12));
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
			BitVector newHighLevelStateCasted = (BitVector) newHighLevelState;
			if (newHighLevelStateCasted.length() != 12)
				throw new IllegalArgumentException("Expected BitVector of length 12, not " + newHighLevelStateCasted.length());
			System.arraycopy(newHighLevelStateCasted.getBits(), 0, lastState, 0, 12);
			return lastState;
		default:
			return super.setHighLevelState(lastState, stateID, newHighLevelState);
		}
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIdff12.class.getCanonicalName(), (m, p, n) -> new GUIdff12(m, n));
	}
}