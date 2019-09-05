package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class Modelnor12 extends SimpleRectangularHardcodedModelComponent
{
	public Modelnor12(LogicModelModifiable model, String name)
	{
		super(model, "nor12", name, "=0");
		setSize(35, 20);
		addPin(new Pin(model, this, "D", 12, PinUsage.INPUT, 0, 10), Position.RIGHT);
		addPin(new Pin(model, this, "Y", 1, PinUsage.OUTPUT, 35, 10), Position.LEFT);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
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
		IndirectModelComponentCreator.setComponentSupplier(Modelnor12.class.getCanonicalName(), (m, p, n) -> new Modelnor12(m, n));
	}
}