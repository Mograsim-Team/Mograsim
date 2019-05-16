package era.mi.gui.examples;

import era.mi.gui.LogicUI;
import era.mi.gui.components.GUIManualSwitch;
import era.mi.gui.components.GUINotGate;
import era.mi.gui.components.GUIOrGate;
import era.mi.gui.wires.WireConnectionPoint;
import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class RSLatchGUIExample
{
	private static final int	WIRE_DELAY	= 10;
	private static final int	OR_DELAY	= 50;
	private static final int	NOT_DELAY	= 50;

	public static void main(String[] args)
	{
		LogicUI ui = new LogicUI();
		initComponents(ui);
		ui.run();
	}

	private static void initComponents(LogicUI ui)
	{
		Simulation.TIMELINE.reset();
		WireArray r = new WireArray(1, WIRE_DELAY);
		WireArray s = new WireArray(1, WIRE_DELAY);
		WireArray t2 = new WireArray(1, WIRE_DELAY);
		WireArray t1 = new WireArray(1, WIRE_DELAY);
		WireArray q = new WireArray(1, WIRE_DELAY);
		WireArray nq = new WireArray(1, WIRE_DELAY);

		GUIManualSwitch rIn = ui.addComponent(new GUIManualSwitch(r), 100, 100);
		GUIManualSwitch sIn = ui.addComponent(new GUIManualSwitch(s), 100, 200);
		GUIOrGate or1 = ui.addComponent(new GUIOrGate(OR_DELAY, t1, r, nq), 160, 102.5);
		GUIOrGate or2 = ui.addComponent(new GUIOrGate(OR_DELAY, t2, q, s), 160, 192.5);
		GUINotGate not1 = ui.addComponent(new GUINotGate(NOT_DELAY, t1, q), 200, 107.5);
		GUINotGate not2 = ui.addComponent(new GUINotGate(NOT_DELAY, t2, nq), 200, 197.5);

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