package net.mograsim.logic.model.am2900.components;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Arrays;
import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIinc12 extends SimpleRectangularHardcodedGUIComponent
{
	public GUIinc12(ViewModelModifiable model, String name)
	{
		super(model, name, "Incrementer");
		setSize(40, 20);
		addPin(new Pin(this, "A", 12, 20, 20), Usage.INPUT, Position.TOP);
		addPin(new Pin(this, "CI", 1, 40, 10), Usage.INPUT, Position.LEFT);
		addPin(new Pin(this, "Y", 12, 20, 0), Usage.OUTPUT, Position.BOTTOM);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
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
			// TODO extract to helper. This code almost also exists in GUIAM2910RegCntr.
			// TODO maybe invert loop direction
			for (int i = 11; i >= 0; i--)
			{
				Bit a = ABits[i];
				Bit z;
				if (a.isBinary() && carry.isBinary())
				{
					boolean aBool = a == ONE;
					boolean carryBool = carry == ONE;
					z = aBool ^ carryBool ? ONE : ZERO;
					carry = aBool && carryBool ? ONE : ZERO;
				} else
				{
					carry = carry.join(a);
					z = carry;
				}
				YBits[i] = z;
			}
		}
		readWriteEnds.get("Y").feedSignals(YBits);
		return null;
	}

}