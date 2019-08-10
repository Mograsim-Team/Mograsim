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

public class GUInor12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUInor12(ViewModelModifiable model, String name)
	{
		super(model, name, "=0");
		setSize(35, 20);
		addPin(new Pin(this, "D", 12, 0, 10), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "Y", 1, 30, 10), Usage.OUTPUT, Position.LEFT);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		readWriteEnds.get("Y").feedSignals(getResult(readEnds.get("D").getValues().getBits()));
		return null;
	}

	private static Bit getResult(Bit[] DValArr)
	{
		for (int i = 0; i < 12; i++)
			if (DValArr[i] == X)
				return X;
		for (int i = 0; i < 12; i++)
			if (DValArr[i] == U)
				return U;
		for (int i = 0; i < 12; i++)
			if (DValArr[i] == ONE)
				return ZERO;
		for (int i = 0; i < 12; i++)
			if (DValArr[i] == Z)
				return X;
		return ONE;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUInor12.class.getCanonicalName(), (m, p, n) -> new GUInor12(m, n));
	}
}