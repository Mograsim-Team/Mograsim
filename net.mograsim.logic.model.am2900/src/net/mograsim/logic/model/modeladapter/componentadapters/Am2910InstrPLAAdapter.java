package net.mograsim.logic.model.modeladapter.componentadapters;

import static net.mograsim.logic.core.types.Bit.ONE;
import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.X;
import static net.mograsim.logic.core.types.Bit.ZERO;

import java.util.Map;

import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;
import net.mograsim.logic.model.am2900.components.am2910.GUIAm2910InstrPLA;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.LogicModelParameters;

public class Am2910InstrPLAAdapter implements ComponentAdapter<GUIAm2910InstrPLA>
{
	@Override
	public Class<GUIAm2910InstrPLA> getSupportedClass()
	{
		return GUIAm2910InstrPLA.class;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, GUIAm2910InstrPLA guiComponent,
			Map<Pin, Wire> logicWiresPerPin)
	{
		ReadEnd PASS = logicWiresPerPin.get(guiComponent.getPin("PASS")).createReadOnlyEnd();
		ReadEnd I3 = logicWiresPerPin.get(guiComponent.getPin("I3")).createReadOnlyEnd();
		ReadEnd I2 = logicWiresPerPin.get(guiComponent.getPin("I2")).createReadOnlyEnd();
		ReadEnd I1 = logicWiresPerPin.get(guiComponent.getPin("I1")).createReadOnlyEnd();
		ReadEnd I0 = logicWiresPerPin.get(guiComponent.getPin("I0")).createReadOnlyEnd();
		ReadEnd Req0 = logicWiresPerPin.get(guiComponent.getPin("R=0")).createReadOnlyEnd();
		ReadWriteEnd _PL = logicWiresPerPin.get(guiComponent.getPin("_PL")).createReadWriteEnd();
		ReadWriteEnd _MAP = logicWiresPerPin.get(guiComponent.getPin("_MAP")).createReadWriteEnd();
		ReadWriteEnd _VECT = logicWiresPerPin.get(guiComponent.getPin("_VECT")).createReadWriteEnd();
		ReadWriteEnd RWE = logicWiresPerPin.get(guiComponent.getPin("RWE")).createReadWriteEnd();
		ReadWriteEnd RDEC = logicWiresPerPin.get(guiComponent.getPin("RDEC")).createReadWriteEnd();
		ReadWriteEnd YD = logicWiresPerPin.get(guiComponent.getPin("YD")).createReadWriteEnd();
		ReadWriteEnd YR = logicWiresPerPin.get(guiComponent.getPin("YR")).createReadWriteEnd();
		ReadWriteEnd YF = logicWiresPerPin.get(guiComponent.getPin("YF")).createReadWriteEnd();
		ReadWriteEnd YmuPC = logicWiresPerPin.get(guiComponent.getPin("YmuPC")).createReadWriteEnd();
		ReadWriteEnd STKI0 = logicWiresPerPin.get(guiComponent.getPin("STKI0")).createReadWriteEnd();
		ReadWriteEnd STKI1 = logicWiresPerPin.get(guiComponent.getPin("STKI1")).createReadWriteEnd();

		LogicObserver updateOutputs = o ->
		{
			Bit PASSVal = PASS.getValue();
			Bit I3Val = I3.getValue();
			Bit I2Val = I2.getValue();
			Bit I1Val = I1.getValue();
			Bit I0Val = I0.getValue();
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
				int I = (I3Val == ONE ? 8 : 0) + (I2Val == ONE ? 4 : 0) + (I1Val == ONE ? 2 : 0) + (I0Val == ONE ? 1 : 0);
				Bit _PLVal = ONE;
				Bit _MAPVal = ONE;
				Bit _VECTVal = ONE;
				if (I == 2)
					_MAPVal = ZERO;
				else if (I == 6)
					_VECTVal = ZERO;
				else
					_PLVal = ZERO;
				_PL.feedSignals(_PLVal);
				_MAP.feedSignals(_MAPVal);
				_VECT.feedSignals(_VECTVal);
				if (I == 8 || I == 9 || I == 15)
				{
					RWE.feedSignals(Req0Val);
					RDEC.feedSignals(Req0Val);// "forward" X/U/Z
				} else if (I == 4)
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
					switch (I + (PASSVal == ONE ? 16 : 0))
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
					switch (I + (PASSVal == ONE ? 16 : 0))
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
						STKI1Val = ZERO;
						STKI0Val = ZERO;
						break;
					case 4:
					case 5:
					case 1 + 16:
					case 4 + 16:
					case 5 + 16:
						STKI1Val = ZERO;
						STKI0Val = ONE;
						break;
					case 0:
					case 0 + 16:
						STKI1Val = ONE;
						STKI0Val = ZERO;
						break;
					case 10 + 16:
					case 11 + 16:
					case 13 + 16:
					case 15 + 16:
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
		};

		PASS.registerObserver(updateOutputs);
		I3.registerObserver(updateOutputs);
		I2.registerObserver(updateOutputs);
		I1.registerObserver(updateOutputs);
		I0.registerObserver(updateOutputs);
		Req0.registerObserver(updateOutputs);
	}
}