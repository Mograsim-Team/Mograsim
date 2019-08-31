package net.mograsim.logic.model.examples;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.SimpleLogicUIStandalone.VisualisationObjects;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class Am2910Testbench
{
	public static void main(String[] args)
	{
		SimpleLogicUIStandalone.executeVisualisation(Am2910Testbench::create, Am2910Testbench::beforeRun);
	}

	@SuppressWarnings("unused") // for GUIWires being created
	public static void create(ViewModelModifiable model)
	{
		GUIComponent am2910 = IndirectGUIComponentCreator.createComponent(model, "file:components/am2910/GUIAm2910.json", "Am2910");
		GUIManualSwitch C = new GUIManualSwitch(model, 1, "C");
		GUIManualSwitch D = new GUIManualSwitch(model, 12, "D");
		GUIManualSwitch _RLD = new GUIManualSwitch(model, 1, "_RLD");
		GUIManualSwitch _CC = new GUIManualSwitch(model, 1, "_CC");
		GUIManualSwitch _CCEN = new GUIManualSwitch(model, 1, "_CCEN");
		GUIManualSwitch I = new GUIManualSwitch(model, 4, "I");
		GUIManualSwitch CI = new GUIManualSwitch(model, 1, "CI");
		GUIManualSwitch _OE = new GUIManualSwitch(model, 1, "_OE");
		GUIBitDisplay _FULL = new GUIBitDisplay(model, 1, "_FULL");
		GUIBitDisplay _PL = new GUIBitDisplay(model, 1, "_PL");
		GUIBitDisplay _MAP = new GUIBitDisplay(model, 1, "_MAP");
		GUIBitDisplay _VECT = new GUIBitDisplay(model, 1, "_VECT");
		GUIBitDisplay Y = new GUIBitDisplay(model, 12, "Y");

		C.moveTo(35, -35);
		D.moveTo(10, -20);
		_RLD.moveTo(-30, -1.5);
		_CC.moveTo(-30, 20);
		_CCEN.moveTo(-30, 40);
		I.moveTo(-30, 60);
		CI.moveTo(75, 45);
		_FULL.moveTo(80, 6.5);
		_PL.moveTo(30, 85);
		_MAP.moveTo(30, 105);
		_VECT.moveTo(30, 125);
		Y.moveTo(40, 65);
		_OE.moveTo(60, 85);

		new GUIWire(model, C.getOutputPin(), am2910.getPin("C"), new Point(60, -27.5));
		new GUIWire(model, D.getOutputPin(), am2910.getPin("D"), new Point(36, -12.5));
		new GUIWire(model, _RLD.getOutputPin(), am2910.getPin("_RLD"), new Point[0]);
		new GUIWire(model, _CC.getOutputPin(), am2910.getPin("_CC"));
		new GUIWire(model, _CCEN.getOutputPin(), am2910.getPin("_CCEN"));
		new GUIWire(model, I.getOutputPin(), am2910.getPin("I"));
		new GUIWire(model, CI.getOutputPin(), am2910.getPin("CI"), new Point(100, 52.5), new Point(100, 40));
		new GUIWire(model, am2910.getPin("_FULL"), _FULL.getInputPin(), new Point[0]);
		new GUIWire(model, am2910.getPin("_PL"), _PL.getInputPin(), new Point(13, 92.5));
		new GUIWire(model, am2910.getPin("_MAP"), _MAP.getInputPin(), new Point(19, 112.5));
		new GUIWire(model, am2910.getPin("_VECT"), _VECT.getInputPin(), new Point(25, 132.5));
		new GUIWire(model, am2910.getPin("Y"), Y.getInputPin(), new Point(38, 72.5));
		new GUIWire(model, am2910.getPin("_OE"), _OE.getOutputPin());
	}

	public static void beforeRun(VisualisationObjects vis)
	{
		vis.model.getComponentsByName().values().forEach(c ->
		{
			if (c instanceof GUIManualSwitch)
			{
				GUIManualSwitch cCasted = (GUIManualSwitch) c;
				cCasted.setHighLevelState("out", BitVector.of(Bit.ZERO, cCasted.logicWidth));
			}
		});
	}
}