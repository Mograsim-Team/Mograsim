package net.mograsim.logic.model.am2900.components.am2904;

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

public class GUIAm2904ShiftInstrDecode extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2904ShiftInstrDecode(ViewModelModifiable model, String name)
	{
		super(model, name, "Shift \ninstruction\ndecode");
		setSize(60, 80);
		addPin(new Pin(this, "I", 5, 0, 25), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "_SE", 1, 0, 55), Usage.INPUT, Position.RIGHT);
		// SIO0 MUX:
		// 000: 0
		// 001: 1
		// 01x: SIOn
		// 10x: QIOn
		// 11x: MC
		addPin(new Pin(this, "SIO0_MUX", 3, 60, 5), Usage.OUTPUT, Position.LEFT);
		// SIOn MUX:
		// 000: 0
		// 001: 1
		// 010: SIO0
		// 011: QIO0
		// 100: MC
		// 101: MN
		// 110: IC
		// 111: IN xor IVOR
		addPin(new Pin(this, "SIOn_MUX", 3, 60, 15), Usage.OUTPUT, Position.LEFT);
		// QIO0 MUX:
		// 000: 0
		// 001: 1
		// 01x: SIOn
		// 10x: QIOn
		// 11x: MC
		addPin(new Pin(this, "QIO0_MUX", 3, 60, 25), Usage.OUTPUT, Position.LEFT);
		// QIOn MUX:
		// 000: 0
		// 001: 1
		// 01x: SIO0
		// 10x: QIO0
		// 11x: MN
		addPin(new Pin(this, "QIOn_MUX", 3, 60, 35), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "OEn", 1, 60, 45), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "OE0", 1, 60, 55), Usage.OUTPUT, Position.LEFT);
		// 00: SIO0
		// 01: QIO0
		// 1x: SIOn
		addPin(new Pin(this, "MC_MUX", 2, 60, 65), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "MC_EN", 1, 60, 75), Usage.OUTPUT, Position.LEFT);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit _SE = readEnds.get("_SE").getValue();
		Bit[] IBits = readEnds.get("I").getValues().getBits();
		readWriteEnds.get("OEn").feedSignals(IBits[0].not().and(_SE.not()));
		readWriteEnds.get("OE0").feedSignals(IBits[0].and(_SE.not()));
		if (_SE == Z || _SE == X)
		{
			readWriteEnds.get("SIO0_MUX").feedSignals(X, X, X);
			readWriteEnds.get("SIOn_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIO0_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIOn_MUX").feedSignals(X, X, X);
			readWriteEnds.get("MC_MUX").feedSignals(X, X);
			readWriteEnds.get("MC_EN").feedSignals(X);
			return null;
		} else if (_SE == U)
		{

			readWriteEnds.get("SIO0_MUX").feedSignals(U, U, U);
			readWriteEnds.get("SIOn_MUX").feedSignals(U, U, U);
			readWriteEnds.get("QIO0_MUX").feedSignals(U, U, U);
			readWriteEnds.get("QIOn_MUX").feedSignals(U, U, U);
			readWriteEnds.get("MC_MUX").feedSignals(U, U);
			readWriteEnds.get("MC_EN").feedSignals(U);
			return null;
		} else if (_SE == ONE)
		{
			readWriteEnds.get("SIO0_MUX").feedSignals(X, X, X);
			readWriteEnds.get("SIOn_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIO0_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIOn_MUX").feedSignals(X, X, X);
			readWriteEnds.get("MC_MUX").feedSignals(X, X);
			readWriteEnds.get("MC_EN").feedSignals(ZERO);
			return null;
		}
		// TODO move the following loop to BitVector.
		int IAsInt = 0;
		for (int i = 0; i < 5; i++)
			switch (IBits[4 - i])
			{
			case ONE:
				IAsInt |= 1 << i;
				break;
			case U:
				readWriteEnds.get("SIO0_MUX").feedSignals(U, U, U);
				readWriteEnds.get("SIOn_MUX").feedSignals(U, U, U);
				readWriteEnds.get("QIO0_MUX").feedSignals(U, U, U);
				readWriteEnds.get("QIOn_MUX").feedSignals(U, U, U);
				readWriteEnds.get("MC_MUX").feedSignals(U, U);
				readWriteEnds.get("MC_EN").feedSignals(U);
				return null;
			case X:
			case Z:
				readWriteEnds.get("SIO0_MUX").feedSignals(X, X, X);
				readWriteEnds.get("SIOn_MUX").feedSignals(X, X, X);
				readWriteEnds.get("QIO0_MUX").feedSignals(X, X, X);
				readWriteEnds.get("QIOn_MUX").feedSignals(X, X, X);
				readWriteEnds.get("MC_MUX").feedSignals(X, X);
				readWriteEnds.get("MC_EN").feedSignals(X);
				return null;
			case ZERO:
				break;
			default:
				throw new IllegalArgumentException("Unknown enum constant: " + IBits[i]);
			}
		if (IAsInt < 16)
		{
			readWriteEnds.get("SIO0_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIO0_MUX").feedSignals(X, X, X);
			switch (IAsInt)
			{
			case 0:
			case 2:
			case 6:
			case 7:
				readWriteEnds.get("SIOn_MUX").feedSignals(ZERO, ZERO, ZERO);
				break;
			case 1:
			case 3:
				readWriteEnds.get("SIOn_MUX").feedSignals(ZERO, ZERO, ONE);
				break;
			case 4:
			case 9:
			case 12:
				readWriteEnds.get("SIOn_MUX").feedSignals(ONE, ZERO, ZERO);
				break;
			case 5:
				readWriteEnds.get("SIOn_MUX").feedSignals(ONE, ZERO, ONE);
				break;
			case 8:
			case 10:
				readWriteEnds.get("SIOn_MUX").feedSignals(ZERO, ONE, ZERO);
				break;
			case 11:
				readWriteEnds.get("SIOn_MUX").feedSignals(ONE, ONE, ZERO);
				break;
			case 13:
			case 15:
				readWriteEnds.get("SIOn_MUX").feedSignals(ZERO, ONE, ONE);
				break;
			case 14:
				readWriteEnds.get("SIOn_MUX").feedSignals(ONE, ONE, ONE);
				break;
			default:
				throw new IllegalStateException("can't happen");
			}
			switch (IAsInt)
			{
			case 0:
				readWriteEnds.get("QIOn_MUX").feedSignals(ZERO, ZERO, ZERO);
				break;
			case 1:
				readWriteEnds.get("QIOn_MUX").feedSignals(ZERO, ZERO, ONE);
				break;
			case 2:
				readWriteEnds.get("QIOn_MUX").feedSignals(ONE, ONE, X);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				readWriteEnds.get("QIOn_MUX").feedSignals(ZERO, ONE, X);
				break;
			case 8:
			case 9:
			case 10:
				readWriteEnds.get("QIOn_MUX").feedSignals(ONE, ZERO, X);
				break;
			default:
				throw new IllegalStateException("can't happen");
			}
		} else
		{
			readWriteEnds.get("SIOn_MUX").feedSignals(X, X, X);
			readWriteEnds.get("QIOn_MUX").feedSignals(X, X, X);
			switch (IAsInt)
			{
			case 16:
			case 18:
				readWriteEnds.get("SIO0_MUX").feedSignals(ZERO, ZERO, ZERO);
				break;
			case 17:
			case 19:
				readWriteEnds.get("SIO0_MUX").feedSignals(ZERO, ZERO, ONE);
				break;
			case 20:
			case 21:
			case 22:
			case 23:
			case 28:
			case 29:
			case 30:
			case 31:
				readWriteEnds.get("SIO0_MUX").feedSignals(ONE, ZERO, X);
				break;
			case 24:
			case 26:
				readWriteEnds.get("SIO0_MUX").feedSignals(ONE, ONE, X);
				break;
			case 25:
			case 27:
				readWriteEnds.get("SIO0_MUX").feedSignals(ZERO, ONE, X);
				break;
			default:
				throw new IllegalStateException("can't happen");
			}
			switch (IAsInt)
			{
			case 16:
			case 18:
			case 20:
			case 22:
			case 27:
				readWriteEnds.get("QIO0_MUX").feedSignals(ZERO, ZERO, ZERO);
				break;
			case 17:
			case 19:
			case 21:
			case 23:
				readWriteEnds.get("QIO0_MUX").feedSignals(ZERO, ZERO, ONE);
				break;
			case 24:
			case 25:
			case 26:
				readWriteEnds.get("SIO0_MUX").feedSignals(ONE, ZERO, X);
				break;
			case 28:
			case 30:
				readWriteEnds.get("SIO0_MUX").feedSignals(ONE, ONE, X);
				break;
			case 29:
			case 31:
				readWriteEnds.get("QIO0_MUX").feedSignals(ZERO, ONE, X);
				break;
			default:
				throw new IllegalStateException("can't happen");
			}
		}
		// MC
		switch (IAsInt)
		{
		case 0:
		case 1:
		case 3:
		case 4:
		case 5:
		case 6:
		case 10:
		case 11:
		case 14:
		case 15:
		case 18:
		case 19:
		case 22:
		case 23:
		case 26:
		case 27:
		case 30:
		case 31:
			readWriteEnds.get("MC_EN").feedSignals(ZERO);
			readWriteEnds.get("MC_MUX").feedSignals(X, X);
			break;
		case 2:
		case 8:
		case 9:
			readWriteEnds.get("MC_EN").feedSignals(ONE);
			readWriteEnds.get("MC_MUX").feedSignals(ZERO, ZERO);
			break;
		case 7:
		case 12:
		case 13:
			readWriteEnds.get("MC_EN").feedSignals(ONE);
			readWriteEnds.get("MC_MUX").feedSignals(ZERO, ONE);
			break;
		case 16:
		case 17:
		case 20:
		case 21:
		case 24:
		case 25:
		case 28:
		case 29:
			readWriteEnds.get("MC_EN").feedSignals(ONE);
			readWriteEnds.get("MC_MUX").feedSignals(ONE, X);
			break;
		default:
			throw new IllegalStateException("can't happen");
		}
		return null;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2904ShiftInstrDecode.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2904ShiftInstrDecode(m, n));
	}
}