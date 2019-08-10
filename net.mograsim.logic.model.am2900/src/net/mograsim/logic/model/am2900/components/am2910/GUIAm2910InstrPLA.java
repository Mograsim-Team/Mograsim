package net.mograsim.logic.model.am2900.components.am2910;

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

public class GUIAm2910InstrPLA extends SimpleRectangularHardcodedGUIComponent
{
	public GUIAm2910InstrPLA(ViewModelModifiable model, String name)
	{
		super(model, name, "Instr.\nPLA");
		setSize(30, 85);
		addPin(new Pin(this, "PASS", 1, 0, 5), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "I", 4, 0, 20), Usage.INPUT, Position.RIGHT);
		addPin(new Pin(this, "R=0", 1, 15, 0), Usage.INPUT, Position.BOTTOM);
		addPin(new Pin(this, "_PL", 1, 5, 85), Usage.OUTPUT, Position.TOP);
		addPin(new Pin(this, "_MAP", 1, 15, 85), Usage.OUTPUT, Position.TOP);
		addPin(new Pin(this, "_VECT", 1, 25, 85), Usage.OUTPUT, Position.TOP);
		addPin(new Pin(this, "RWE", 1, 30, 5), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "RDEC", 1, 30, 15), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "YD", 1, 30, 25), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "YR", 1, 30, 35), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "YF", 1, 30, 45), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "YmuPC", 1, 30, 55), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "STKI0", 1, 30, 65), Usage.OUTPUT, Position.LEFT);
		addPin(new Pin(this, "STKI1", 1, 30, 75), Usage.OUTPUT, Position.LEFT);
	}

	@Override
	protected Object recalculate(Object lastState, Map<String, ReadEnd> readEnds, Map<String, ReadWriteEnd> readWriteEnds)
	{
		ReadEnd PASS = readEnds.get("PASS");
		ReadEnd I = readEnds.get("I");
		ReadEnd Req0 = readEnds.get("R=0");
		ReadWriteEnd _PL = readWriteEnds.get("_PL");
		ReadWriteEnd _MAP = readWriteEnds.get("_MAP");
		ReadWriteEnd _VECT = readWriteEnds.get("_VECT");
		ReadWriteEnd RWE = readWriteEnds.get("RWE");
		ReadWriteEnd RDEC = readWriteEnds.get("RDEC");
		ReadWriteEnd YD = readWriteEnds.get("YD");
		ReadWriteEnd YR = readWriteEnds.get("YR");
		ReadWriteEnd YF = readWriteEnds.get("YF");
		ReadWriteEnd YmuPC = readWriteEnds.get("YmuPC");
		ReadWriteEnd STKI0 = readWriteEnds.get("STKI0");
		ReadWriteEnd STKI1 = readWriteEnds.get("STKI1");

		Bit PASSVal = PASS.getValue();
		Bit I3Val = I.getValue(3);
		Bit I2Val = I.getValue(2);
		Bit I1Val = I.getValue(1);
		Bit I0Val = I.getValue(0);
		Bit Req0Val = Req0.getValue();

		if (!I3Val.isBinary() || !I2Val.isBinary() || !I1Val.isBinary() || !I0Val.isBinary())
			if ((I3Val == U || I3Val.isBinary()) || (I2Val == U || I2Val.isBinary()) || (I1Val == U || I1Val.isBinary())
					|| (I0Val == U || I0Val.isBinary()))
			{
				_PL.feedSignals(U);
				_MAP.feedSignals(U);
				_VECT.feedSignals(U);
				RWE.feedSignals(U);
				RDEC.feedSignals(U);
				YD.feedSignals(U);
				YR.feedSignals(U);
				YF.feedSignals(U);
				YmuPC.feedSignals(U);
				STKI0.feedSignals(U);
				STKI1.feedSignals(U);
			} else
			{
				_PL.feedSignals(X);
				_MAP.feedSignals(X);
				_VECT.feedSignals(X);
				RWE.feedSignals(X);
				RDEC.feedSignals(X);
				YD.feedSignals(X);
				YR.feedSignals(X);
				YF.feedSignals(X);
				YmuPC.feedSignals(X);
				STKI0.feedSignals(X);
				STKI1.feedSignals(X);
			}
		else
		{
			int IAsInt = (I3Val == ONE ? 8 : 0) + (I2Val == ONE ? 4 : 0) + (I1Val == ONE ? 2 : 0) + (I0Val == ONE ? 1 : 0);
			Bit _PLVal = ONE;
			Bit _MAPVal = ONE;
			Bit _VECTVal = ONE;
			if (IAsInt == 2)
				_MAPVal = ZERO;
			else if (IAsInt == 6)
				_VECTVal = ZERO;
			else
				_PLVal = ZERO;
			_PL.feedSignals(_PLVal);
			_MAP.feedSignals(_MAPVal);
			_VECT.feedSignals(_VECTVal);
			if (IAsInt == 8 || IAsInt == 9 || IAsInt == 15)
			{
				RWE.feedSignals(Req0Val);
				RDEC.feedSignals(Req0Val);// "forward" X/U/Z
			} else if (IAsInt == 4)
			{
				RWE.feedSignals(PASSVal);
				RDEC.feedSignals(PASSVal == ONE ? ZERO : PASSVal);// "forward" X/U/Z
			} else
			{
				RWE.feedSignals(ZERO);
				RDEC.feedSignals(ZERO);
			}
			if (!PASSVal.isBinary())
			{
				YD.feedSignals(PASSVal);// "forward" X/U/Z
				YR.feedSignals(PASSVal);// "forward" X/U/Z
				YF.feedSignals(PASSVal);// "forward" X/U/Z
				YmuPC.feedSignals(PASSVal);// "forward" X/U/Z
			} else
			{
				Bit YDVal = ZERO;
				Bit YRVal = ZERO;
				Bit YFVal = ZERO;
				Bit YmuPCVal = ZERO;
				switch (IAsInt + (PASSVal == ONE ? 16 : 0))
				{
				case 0:
				case 0 + 16:
					break;
				case 2:
				case 1 + 16:
				case 2 + 16:
				case 3 + 16:
				case 5 + 16:
				case 6 + 16:
				case 7 + 16:
				case 11 + 16:
					YDVal = ONE;
					break;
				case 5:
				case 7:
					YRVal = ONE;
					break;
				case 13:
				case 10 + 16:
					YFVal = ONE;
					break;
				case 1:
				case 3:
				case 4:
				case 6:
				case 10:
				case 11:
				case 12:
				case 14:
				case 4 + 16:
				case 12 + 16:
				case 13 + 16:
				case 14 + 16:
				case 15 + 16:
					YmuPCVal = ONE;
					break;
				case 8:
				case 8 + 16:
					YFVal = Req0Val.not();// "forward" X/U/Z
					YmuPCVal = Req0Val;// "forward" X/U/Z
					break;
				case 9:
				case 9 + 16:
					YDVal = Req0Val.not();// "forward" X/U/Z
					YmuPCVal = Req0Val;// "forward" X/U/Z
					break;
				case 15:
					YFVal = Req0Val.not();// "forward" X/U/Z
					YDVal = Req0Val;// "forward" X/U/Z
					break;
				default:
					throw new IllegalStateException("shouldn't happen");
				}
				YD.feedSignals(YDVal);
				YR.feedSignals(YRVal);
				YF.feedSignals(YFVal);
				YmuPC.feedSignals(YmuPCVal);
				Bit STKI0Val;
				Bit STKI1Val;
				switch (IAsInt + (PASSVal == ONE ? 16 : 0))
				{
				case 1:
				case 2:
				case 3:
				case 6:
				case 7:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 2 + 16:
				case 3 + 16:
				case 6 + 16:
				case 7 + 16:
				case 9 + 16:
				case 12 + 16:
				case 14 + 16:
					// HOLD
					STKI1Val = ZERO;
					STKI0Val = ZERO;
					break;
				case 4:
				case 5:
				case 1 + 16:
				case 4 + 16:
				case 5 + 16:
					// PUSH
					STKI1Val = ZERO;
					STKI0Val = ONE;
					break;
				case 0:
				case 0 + 16:
					// CLEAR
					STKI1Val = ONE;
					STKI0Val = ZERO;
					break;
				case 10 + 16:
				case 11 + 16:
				case 13 + 16:
				case 15 + 16:
					// POP
					STKI1Val = ONE;
					STKI0Val = ONE;
					break;
				case 8:
				case 15:
				case 8 + 16:
					STKI1Val = Req0Val;// "forward" X/U/Z
					STKI0Val = Req0Val;// "forward" X/U/Z
					break;
				default:
					throw new IllegalStateException("shouldn't happen");
				}
				STKI0.feedSignals(STKI0Val);
				STKI1.feedSignals(STKI1Val);
			}
		}
		return null;
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2910InstrPLA.class.getCanonicalName(),
				(m, p, n) -> new GUIAm2910InstrPLA(m, n));
	}
}