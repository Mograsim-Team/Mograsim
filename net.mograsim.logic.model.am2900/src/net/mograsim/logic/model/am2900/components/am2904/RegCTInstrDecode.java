package net.mograsim.logic.model.am2900.components.am2904;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
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

public class RegCTInstrDecode extends SimpleRectangularHardcodedGUIComponent
{
	public RegCTInstrDecode(ViewModelModifiable model, String name)
	{
		super(model, name, "Instruction\ndecode");
		setSize(40, 40);
		addPin(new Pin(this, "I", 6, 0, 20), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "muSR_I1", 1, -1, -15), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR_IM", 1, -1, -14), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR_OVRRET", 1, -1, -13), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR_CINV", 1, -1, -12), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR__WEZ", 1, -1, -11), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR__WEC", 1, -1, -10), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR__WEN", 1, -1, -9), Usage.OUTPUT, null);
		addPin(new Pin(this, "muSR__WEOVR", 1, -1, -8), Usage.OUTPUT, null);
		addPin(new Pin(this, "MSR_1_Y_CINV__M", 1, -1, -7), Usage.OUTPUT, null);
		addPin(new Pin(this, "MSR_mu_Y_SOC__M", 1, -1, -6), Usage.OUTPUT, null);
		addPin(new Pin(this, "MSR_I_CINV_SOC__M", 1, -1, -5), Usage.OUTPUT, null);
		addPin(new Pin(this, "CT_SRC", 2, -1, -4), Usage.OUTPUT, null);
		addPin(new Pin(this, "CT_INV", 1, -1, -3), Usage.OUTPUT, null);
		addPin(new Pin(this, "CT_MUX", 3, -1, -2), Usage.OUTPUT, null);
		addPin(new Pin(this, "CT_EXP", 1, -1, -1), Usage.OUTPUT, null);
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
					e.feedSignals(U);
				return null;
			case X:
			case Z:
				for (ReadWriteEnd e : readWriteEnds.values())
					e.feedSignals(X);
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
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 1:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 3:
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 6:
		case 7:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ONE);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 8:
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 9:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 10:
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 11:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 12:
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 13:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ONE);
			break;
		case 14:
			readWriteEnds.get("muSR_I1").feedSignals(ZERO);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 15:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ZERO);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ONE);
			readWriteEnds.get("muSR__WEC").feedSignals(ONE);
			readWriteEnds.get("muSR__WEN").feedSignals(ONE);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		case 24:
		case 25:
		case 40:
		case 41:
		case 56:
		case 57:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ONE);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
			break;
		default:
			readWriteEnds.get("muSR_I1").feedSignals(ONE);
			readWriteEnds.get("muSR_IM").feedSignals(ONE);
			readWriteEnds.get("muSR_OVRRET").feedSignals(ZERO);
			readWriteEnds.get("muSR_CINV").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEZ").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEC").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEN").feedSignals(ZERO);
			readWriteEnds.get("muSR__WEOVR").feedSignals(ZERO);
		}
		switch (IAsInt)
		{
		case 0:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ONE);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ONE);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ZERO);
			break;
		case 1:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ONE);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ZERO);
			break;
		case 2:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ONE);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ZERO);
			break;
		case 3:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ZERO);
			break;
		case 4:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ONE);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ONE);
			break;
		case 5:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ONE);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ONE);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ONE);
			break;
		case 8:
		case 9:
		case 24:
		case 25:
		case 40:
		case 41:
		case 56:
		case 57:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ONE);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ONE);
			break;
		default:
			readWriteEnds.get("MSR_1_Y_CINV__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_mu_Y_SOC__M").feedSignals(ZERO);
			readWriteEnds.get("MSR_I_CINV_SOC__M").feedSignals(ONE);
			break;
		}
		readWriteEnds.get("CT_SRC").feedSignals(IBits[0], IBits[1]);
		readWriteEnds.get("CT_INV").feedSignals(IBits[5]);
		readWriteEnds.get("CT_MUX").feedSignals(IBits[2], IBits[3], IBits[4]);
		readWriteEnds.get("CT_EXP").feedSignals((IAsInt & 0b1110) == 0b1110 ? ONE : ZERO);
		return null;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(RegCTInstrDecode.class.getCanonicalName(),
				(m, p, n) -> new RegCTInstrDecode(m, n));
	}
}