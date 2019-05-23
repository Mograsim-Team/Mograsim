package era.mi.gui.examples;

import era.mi.gui.LogicUICanvas;
import era.mi.gui.LogicUIStandalone;
import era.mi.gui.components.GUIManualSwitch;
import era.mi.gui.components.GUINotGate;
import era.mi.gui.components.GUIOrGate;
import era.mi.gui.wires.WireConnectionPoint;
import era.mi.logic.Simulation;
import era.mi.logic.wires.Wire;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class RSLatchGUIExample
{
	private static final int WIRE_DELAY = 10;
	private static final int OR_DELAY = 50;
	private static final int NOT_DELAY = 50;

	public static void main(String[] args)
	{
		LogicUIStandalone ui = new LogicUIStandalone();
		addComponentsAndWires(ui.getLogicUICanvas());
		ui.run();
	}

	public static void addComponentsAndWires(LogicUICanvas ui)
	{
		Simulation.TIMELINE.reset();
		Wire r = new Wire(1, WIRE_DELAY);
		Wire s = new Wire(1, WIRE_DELAY);
		Wire t2 = new Wire(1, WIRE_DELAY);
		Wire t1 = new Wire(1, WIRE_DELAY);
		Wire q = new Wire(1, WIRE_DELAY);
		Wire nq = new Wire(1, WIRE_DELAY);

		GUIManualSwitch rIn = ui.addComponent(new GUIManualSwitch(r.createEnd()), 100, 100);
		GUIManualSwitch sIn = ui.addComponent(new GUIManualSwitch(s.createEnd()), 100, 200);
		GUIOrGate or1 = ui.addComponent(new GUIOrGate(OR_DELAY, t1.createEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd()), 160, 102.5);
		GUIOrGate or2 = ui.addComponent(new GUIOrGate(OR_DELAY, t2.createEnd(), q.createReadOnlyEnd(), s.createReadOnlyEnd()), 160, 192.5);
		GUINotGate not1 = ui.addComponent(new GUINotGate(NOT_DELAY, t1.createReadOnlyEnd(), q.createEnd()), 200, 107.5);
		GUINotGate not2 = ui.addComponent(new GUINotGate(NOT_DELAY, t2.createReadOnlyEnd(), nq.createEnd()), 200, 197.5);

		WireConnectionPoint p1 = ui.addComponent(new WireConnectionPoint(q, 3), 250, 112.5);
		WireConnectionPoint p2 = ui.addComponent(new WireConnectionPoint(nq, 3), 250, 202.5);
		WireConnectionPoint o1 = ui.addComponent(new WireConnectionPoint(q, 1), 270, 112.5);
		WireConnectionPoint o2 = ui.addComponent(new WireConnectionPoint(nq, 1), 270, 202.5);

		ui.addWire(rIn, 0, or1, 0);
		ui.addWire(sIn, 0, or2, 1);
		ui.addWire(or1, 2, not1, 0);
		ui.addWire(or2, 2, not2, 0);
		ui.addWire(not1, 1, p1, 0);
		ui.addWire(not2, 1, p2, 0);
		ui.addWire(p1, 1, or2, 0, new Point(250, 130), new Point(140, 185), new Point(140, 197.5));
		ui.addWire(p2, 1, or1, 1, new Point(250, 185), new Point(140, 130), new Point(140, 117.5));
		ui.addWire(p1, 2, o1, 0);
		ui.addWire(p2, 2, o2, 0);
	}
}