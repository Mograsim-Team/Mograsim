package net.mograsim.logic.model.am2900.components.am2904;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.Z;
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

public class ModelAm2904ShiftInstrDecode extends SimpleRectangularHardcodedModelComponent
{
	public ModelAm2904ShiftInstrDecode(LogicModelModifiable model, String name)
	{
		super(model, "Am2904ShiftInstrDecode", name, "Shift \ninstruction\ndecode");
		setSize(60, 80);
		addPin(new Pin(this, "I", 5, PinUsage.INPUT, 0, 25), Position.RIGHT);
		addPin(new Pin(this, "_SE", 1, PinUsage.INPUT, 0, 55), Position.RIGHT);
		// SIO0 MUX:
		// 000: 0
		// 001: 1
		// 01x: SIOn
		// 10x: QIOn
		// 11x: MC
		addPin(new Pin(this, "SIO0_MUX", 3, PinUsage.OUTPUT, 60, 5), Position.LEFT);
		// SIOn MUX:
		// 000: 0
		// 001: 1
		// 010: SIO0
		// 011: QIO0
		// 100: MC
		// 101: MN
		// 110: IC
		// 111: IN xor IVOR
		addPin(new Pin(this, "SIOn_MUX", 3, PinUsage.OUTPUT, 60, 15), Position.LEFT);
		// QIO0 MUX:
		// 000: 0
		// 001: 1
		// 01x: SIOn
		// 10x: QIOn
		// 11x: MC
		addPin(new Pin(this, "QIO0_MUX", 3, PinUsage.OUTPUT, 60, 25), Position.LEFT);
		// QIOn MUX:
		// 000: 0
		// 001: 1
		// 01x: SIO0
		// 10x: QIO0
		// 11x: MN
		addPin(new Pin(this, "QIOn_MUX", 3, PinUsage.OUTPUT, 60, 35), Position.LEFT);
		addPin(new Pin(this, "OEn", 1, PinUsage.OUTPUT, 60, 45), Position.LEFT);
		addPin(new Pin(this, "OE0", 1, PinUsage.OUTPUT, 60, 55), Position.LEFT);
		// 00: SIO0
		// 01: QIO0
		// 1x: SIOn
		addPin(new Pin(this, "MC_MUX", 2, PinUsage.OUTPUT, 60, 65), Position.LEFT);
		addPin(new Pin(this, "MC_EN", 1, PinUsage.OUTPUT, 60, 75), Position.LEFT);
	}

	@Override
	public Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit _SE = readEnds.get("_SE").getValue();
		BitVector I = readEnds.get("I").getValues();
		readWriteEnds.get("OEn").feedSignals(I.getMSBit(0).not().and(_SE.not()));
		readWriteEnds.get("OE0").feedSignals(I.getMSBit(0).and(_SE.not()));
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
		if (!I.isBinary())
		{
			Bit val = null;
			for (Bit b : I.getBits())
				if (!b.isBinary())
				{
					val = b;
					break;
				}
			readWriteEnds.get("SIO0_MUX").feedSignals(val, val, val);
			readWriteEnds.get("SIOn_MUX").feedSignals(val, val, val);
			readWriteEnds.get("QIO0_MUX").feedSignals(val, val, val);
			readWriteEnds.get("QIOn_MUX").feedSignals(val, val, val);
			readWriteEnds.get("MC_MUX").feedSignals(val, val);
			readWriteEnds.get("MC_EN").feedSignals(val);
			return null;
		}
		int IAsInt = I.getUnsignedValue().intValue();
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
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2904ShiftInstrDecode.class.getCanonicalName(),
				(m, p, n) -> new ModelAm2904ShiftInstrDecode(m, n));
	}
}