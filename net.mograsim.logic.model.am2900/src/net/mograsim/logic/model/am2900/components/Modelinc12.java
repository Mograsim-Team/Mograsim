package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
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

public class Modelinc12 extends SimpleRectangularHardcodedModelComponent
{
	public Modelinc12(LogicModelModifiable model, String name)
	{
		super(model, "inc12", name, "Incrementer", false);
		setSize(40, 20);
		addPin(new Pin(model, this, "A", 12, PinUsage.INPUT, 20, 20), Position.TOP);
		addPin(new Pin(model, this, "CI", 1, PinUsage.INPUT, 40, 10), Position.LEFT);
		addPin(new Pin(model, this, "Y", 12, PinUsage.OUTPUT, 20, 0), Position.BOTTOM);

		init();
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] ABits = readEnds.get("A").getValues().getBits();
		Bit CIVal = readEnds.get("CI").getValue();
		Bit[] YBits = new Bit[12];
		if (CIVal == X)
			Arrays.fill(YBits, X);
		else if (CIVal == U)
			Arrays.fill(YBits, U);
		else if (CIVal == Z)
			Arrays.fill(YBits, X);
		else if (CIVal == ZERO)
			YBits = ABits;
		else
		{
			Bit carry = Bit.ONE;
			// TODO extract to helper. This code almost also exists in ModelAm2910RegCntr.
			for (int i = 11; i >= 0; i--)
			{
				Bit a = ABits[i];
				YBits[i] = a.xor(carry);
				carry = a.and(carry);
			}
		}
		readWriteEnds.get("Y").feedSignals(YBits);
		return null;
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(Modelinc12.class.getCanonicalName(), (m, p, n) -> new Modelinc12(m, n));
	}
}