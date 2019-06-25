package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdff extends SimpleRectangularSubmodelComponent
{
	public GUIdff(ViewModelModifiable model)
	{
		super(model, 1, "GUIdff");
		setSubmodelScale(.2);
		setInputPins("C", "D");
		setOutputPins("Q", "_Q");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin C = getSubmodelPin("C");
		Pin D = getSubmodelPin("D");
		Pin Q = getSubmodelPin("Q");
		Pin _Q = getSubmodelPin("_Q");

		GUI_rsLatch _rsLatch1 = new GUI_rsLatch(submodelModifiable);
		GUInand3 nand3 = new GUInand3(submodelModifiable);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		GUI_rsLatch _rsLatch2 = new GUI_rsLatch(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp4 = new WireCrossPoint(submodelModifiable, 1);

		_rsLatch1.moveTo(40, 10);
		nand3.moveTo(40, 40);
		nand2.moveTo(120, 60);
		_rsLatch2.moveTo(120, 30);
		cp1.moveCenterTo(10, 25);
		cp2.moveCenterTo(20, 65);
		cp3.moveCenterTo(100, 35);
		cp4.moveCenterTo(100, 45);

		new GUIWire(submodelModifiable, C, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, _rsLatch1.getPin("_R"), new Point[0]);
		new GUIWire(submodelModifiable, cp1, nand3.getPin("B"), new Point(10, 55));
		new GUIWire(submodelModifiable, D, nand2.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nand2.getPin("Y"), cp2, new Point(145, 70), new Point(145, 85), new Point(20, 85));
		new GUIWire(submodelModifiable, cp2, _rsLatch1.getPin("_S"), new Point(20, 15));
		new GUIWire(submodelModifiable, cp2, nand3.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, _rsLatch1.getPin("_Q"), cp3, new Point(100, 25));
		new GUIWire(submodelModifiable, cp3, nand3.getPin("A"), new Point(30, 35), new Point(30, 45));
		new GUIWire(submodelModifiable, cp3, _rsLatch2.getPin("_S"), new Point[0]);
		new GUIWire(submodelModifiable, nand3.getPin("Y"), cp4, new Point[0]);
		new GUIWire(submodelModifiable, cp4, _rsLatch2.getPin("_R"), new Point[0]);
		new GUIWire(submodelModifiable, cp4, nand2.getPin("A"), new Point(100, 65));
		new GUIWire(submodelModifiable, _rsLatch2.getPin("Q"), Q);
		new GUIWire(submodelModifiable, _rsLatch2.getPin("_Q"), _Q);
	}
}