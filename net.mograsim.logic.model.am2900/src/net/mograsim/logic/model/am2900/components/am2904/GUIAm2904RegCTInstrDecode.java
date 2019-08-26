package net.mograsim.logic.model.am2900.components.am2904;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.SimpleRectangularHardcodedGUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIAm2904RegCTInstrDecode extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2904RegCTInstrDecode(ViewModelModifiable model, String name)
	{
		super(model, name, "Instruction\ndecode");
		setSize(80, 80);
		addPin(new Pin(this, "I", 6, 0, 40), Usage.INPUT, Position.RIGHT);
		// muSR MUX:
		// 00: 0
		// 01: 1
		// 10: M
		// 11: I
		addPin(new Pin(this, "muSR_MUX", 2, 80, 10), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_OVRRET", 1, 80, 20), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_CINV", 1, 80, 30), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_WEZ", 1, 80, 40), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_WEC", 1, 80, 50), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_WEN", 1, 80, 60), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "muSR_WEOVR", 1, 80, 70), Usage.OUTPUT, Position.LEFT);
		// MSR MUX:
		// 000: 0
		// 001: 1
		// 010: mu
		// 011: Y
		// 100: I
		// 101: I, invert C
		// 110: Swap OVR and C
		// 111: _M
		addPin(new Pin(this, "MSR_MUX", 3, 20, 0), Usage.OUTPUT, Position.BOTTOM);
		// TODO when is this HIGH?
		addPin(new Pin(this, "OEN", 1, 60, 0), Usage.OUTPUT, Position.BOTTOM);
		// CT SRC MUX:
		// 00: mu
		// 01: mu
		// 10: M
		// 11: I
		addPin(new Pin(this, "Y_MUX", 2, 10, 80), Usage.OUTPUT, Position.TOP);
		// CT MUX:
		// see Am2900 Family Data Book, Am2904, Table 4 (CT_MUX2-0 = I3-1)
		addPin(new Pin(this, "CT_MUX", 3, 30, 80), Usage.OUTPUT, Position.TOP);
		addPin(new Pin(this, "CT_INV", 1, 50, 80), Usage.OUTPUT, Position.TOP);
		addPin(new Pin(this, "CT_EXP", 1, 70, 80), Usage.OUTPUT, Position.TOP);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		Bit[] IBits = readEnds.get("I").getValues().getBits();
		int IAsInt = 0;
		for (int i = 0; i < 6; i++)
			switch (IBits[5 - i])
			{
			case ONE:
				IAsInt |= 1 << i;
				break;
			case U:
				for (ReadWriteEnd e : readWriteEnds.values())
					e.feedSignals(BitVector.of(U, e.width()));
				return null;
			case X:
			case Z:
				for (ReadWriteEnd e : readWriteEnds.values())
					e.feedSignals(BitVector.of(X, e.width()));
				return null;
			case ZERO:
				break;
			default:
				throw new IllegalArgumentException("Unknown enum constant: " + IBits[i]);
			}
		switch (IAsInt)
		{
		case 0:
		case 2:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 1:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 3:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 6:
		case 7:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ONE);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 8:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 9:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 10:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 11:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 12:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 13:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ZERO);
			break;
		case 14:
			readWriteEnds.get("muSR_MUX").feedSignals(ZERO, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 15:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		case 24:
		case 25:
		case 40:
		case 41:
		case 56:
		case 57:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ONE);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
			break;
		default:
			readWriteEnds.get("muSR_MUX").feedSignals(ONE, ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR_WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR_WEC").feedSignals(ONE);
			readWriteEnds.get("muSR_WEN").feedSignals(ONE);
			readWriteEnds.get("muSR_WEOVR").feedSignals(ONE);
		}
		switch (IAsInt)
		{
		case 0:
			readWriteEnds.get("MSR_MUX").feedSignals(ONE, ONE, ZERO);
			break;
		case 1:
			readWriteEnds.get("MSR_MUX").feedSignals(ONE, ZERO, ZERO);
			break;
		case 2:
			readWriteEnds.get("MSR_MUX").feedSignals(ZERO, ONE, ZERO);
			break;
		case 3:
			readWriteEnds.get("MSR_MUX").feedSignals(ZERO, ZERO, ZERO);
			break;
		case 4:
			readWriteEnds.get("MSR_MUX").feedSignals(ZERO, ONE, ONE);
			break;
		case 5:
			readWriteEnds.get("MSR_MUX").feedSignals(ONE, ONE, ONE);
			break;
		case 8:
		case 9:
		case 24:
		case 25:
		case 40:
		case 41:
		case 56:
		case 57:
			readWriteEnds.get("MSR_MUX").feedSignals(ONE, ZERO, ONE);
			break;
		default:
			readWriteEnds.get("MSR_MUX").feedSignals(ZERO, ZERO, ONE);
			break;
		}
		readWriteEnds.get("Y_MUX").feedSignals(IBits[0], IBits[1]);
		readWriteEnds.get("CT_INV").feedSignals(IBits[5]);
		readWriteEnds.get("CT_MUX").feedSignals(IBits[2], IBits[3], IBits[4]);
		readWriteEnds.get("CT_EXP").feedSignals((IAsInt & 0b1110) == 0b1110 ? ONE : ZERO);
		return null;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2904RegCTInstrDecode.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2904RegCTInstrDecode(m, n));
	}
}