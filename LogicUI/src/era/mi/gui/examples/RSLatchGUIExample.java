package era.mi.gui.examples;

import era.mi.gui.LogicUICanvas;
import era.mi.gui.LogicUIStandalone;
import era.mi.gui.components.GUIManualSwitch;
import era.mi.gui.components.GUINotGate;
import era.mi.gui.components.GUIOrGate;
import era.mi.gui.wires.WireConnectionPoint;
import era.mi.logic.timeline.Timeline;
import era.mi.logic.wires.Wire;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class RSLatchGUIExample
{
	private static final int WIRE_DELAY = 10;
	private static final int OR_DELAY = 50;
	private static final int NOT_DELAY = 50;
	private static final Timeline t = new Timeline(11);

	public static void main(String[] args)
	{
		LogicUIStandalone ui = new LogicUIStandalone(t);
		addComponentsAndWires(ui.getLogicUICanvas());
		ui.run();
	}

	public static void addComponentsAndWires(LogicUICanvas ui)
	{
		Wire r = new Wire(t, 1, WIRE_DELAY);
		Wire s = new Wire(t, 1, WIRE_DELAY);
		Wire t2 = new Wire(t, 1, WIRE_DELAY);
		Wire t1 = new Wire(t, 1, WIRE_DELAY);
		Wire q = new Wire(t, 1, WIRE_DELAY);
		Wire nq = new Wire(t, 1, WIRE_DELAY);

		GUIManualSwitch rIn = ui.addComponent(new GUIManualSwitch(t, r.createReadWriteEnd()), 100, 100);
		GUIManualSwitch sIn = ui.addComponent(new GUIManualSwitch(t, s.createReadWriteEnd()), 100, 200);
		GUIOrGate or1 = ui.addComponent(new GUIOrGate(t, OR_DELAY, t1.createReadWriteEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd()),
				160, 102.5);
		GUIOrGate or2 = ui.addComponent(new GUIOrGate(t, OR_DELAY, t2.createReadWriteEnd(), q.createReadOnlyEnd(), s.createReadOnlyEnd()),
				160, 192.5);
		GUINotGate not1 = ui.addComponent(new GUINotGate(t, NOT_DELAY, t1.createReadOnlyEnd(), q.createReadWriteEnd()), 200, 107.5);
		GUINotGate not2 = ui.addComponent(new GUINotGate(t, NOT_DELAY, t2.createReadOnlyEnd(), nq.createReadWriteEnd()), 200, 197.5);

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