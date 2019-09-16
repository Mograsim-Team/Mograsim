package net.mograsim.logic.model.am2900.am2910;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.SimpleLogicUIStandalone.VisualisationObjects;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.model.components.atomic.ModelClock.ModelClockParams;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class Am2910Testbench
{
	public static void main(String[] args)
	{
		Am2900Loader.setup();
		SimpleLogicUIStandalone.executeVisualisation(Am2910Testbench::create, Am2910Testbench::beforeRun);
	}

	@SuppressWarnings("unused") // for ModelWires being created
	public static void create(LogicModelModifiable model)
	{
		ModelComponent am2910 = IndirectModelComponentCreator.createComponent(model, "Am2910", "Am2910");
		ModelClock C = new ModelClock(model, new ModelClockParams(1000, Orientation.RIGHT));
		ModelManualSwitch D = new ModelManualSwitch(model, 12, "D");
		ModelManualSwitch _RLD = new ModelManualSwitch(model, 1, "_RLD");
		ModelManualSwitch _CC = new ModelManualSwitch(model, 1, "_CC");
		ModelManualSwitch _CCEN = new ModelManualSwitch(model, 1, "_CCEN");
		ModelManualSwitch I = new ModelManualSwitch(model, 4, "I");
		ModelManualSwitch CI = new ModelManualSwitch(model, 1, "CI");
		ModelManualSwitch _OE = new ModelManualSwitch(model, 1, "_OE");
		ModelBitDisplay _FULL = new ModelBitDisplay(model, 1, "_FULL");
		ModelBitDisplay _PL = new ModelBitDisplay(model, 1, "_PL");
		ModelBitDisplay _MAP = new ModelBitDisplay(model, 1, "_MAP");
		ModelBitDisplay _VECT = new ModelBitDisplay(model, 1, "_VECT");
		ModelBitDisplay Y = new ModelBitDisplay(model, 12, "Y");

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

		new ModelWire(model, C.getOutputPin(), am2910.getPin("C"), new Point(60, -27.5));
		new ModelWire(model, D.getOutputPin(), am2910.getPin("D"), new Point(36, -12.5));
		new ModelWire(model, _RLD.getOutputPin(), am2910.getPin("_RLD"), new Point[0]);
		new ModelWire(model, _CC.getOutputPin(), am2910.getPin("_CC"));
		new ModelWire(model, _CCEN.getOutputPin(), am2910.getPin("_CCEN"));
		new ModelWire(model, I.getOutputPin(), am2910.getPin("I"));
		new ModelWire(model, CI.getOutputPin(), am2910.getPin("CI"), new Point(100, 52.5), new Point(100, 40));
		new ModelWire(model, am2910.getPin("_FULL"), _FULL.getInputPin(), new Point[0]);
		new ModelWire(model, am2910.getPin("_PL"), _PL.getInputPin(), new Point(13, 92.5));
		new ModelWire(model, am2910.getPin("_MAP"), _MAP.getInputPin(), new Point(19, 112.5));
		new ModelWire(model, am2910.getPin("_VECT"), _VECT.getInputPin(), new Point(25, 132.5));
		new ModelWire(model, am2910.getPin("Y"), Y.getInputPin(), new Point(38, 72.5));
		new ModelWire(model, am2910.getPin("_OE"), _OE.getOutputPin());
	}

	public static void beforeRun(VisualisationObjects vis)
	{
		vis.model.getComponentsByName().values().forEach(c ->
		{
			if (c instanceof ModelManualSwitch)
			{
				ModelManualSwitch cCasted = (ModelManualSwitch) c;
				cCasted.setHighLevelState("out", BitVector.of(Bit.ZERO, cCasted.logicWidth));
			}
		});
	}
}