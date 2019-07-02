package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUInot4 extends SimpleRectangularSubmodelComponent
{
	public GUInot4(ViewModelModifiable model)
	{
		super(model, 1, "GUInot4");
		setSubmodelScale(.4);
		setInputPins("A1", "A2", "A3", "A4");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand4 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp4 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(30, 2.5);
		nand2.moveTo(30, 27.5);
		nand3.moveTo(30, 52.5);
		nand4.moveTo(30, 77.5);
		cp1.moveCenterTo(15, 12.5);
		cp2.moveCenterTo(15, 37.5);
		cp3.moveCenterTo(15, 62.5);
		cp4.moveCenterTo(15, 87.5);

		new GUIWire(submodelModifiable, A1, cp1, new Point[0]);
		new GUIWire(submodelModifiable, A2, cp2, new Point[0]);
		new GUIWire(submodelModifiable, A3, cp3, new Point[0]);
		new GUIWire(submodelModifiable, A4, cp4, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nand1.getPin("A"), new Point(15, 7.5));
		new GUIWire(submodelModifiable, cp2, nand2.getPin("A"), new Point(15, 32.5));
		new GUIWire(submodelModifiable, cp3, nand3.getPin("A"), new Point(15, 57.5));
		new GUIWire(submodelModifiable, cp4, nand4.getPin("A"), new Point(15, 82.5));
		new GUIWire(submodelModifiable, cp1, nand1.getPin("B"), new Point(15, 17.5));
		new GUIWire(submodelModifiable, cp2, nand2.getPin("B"), new Point(15, 42.5));
		new GUIWire(submodelModifiable, cp3, nand3.getPin("B"), new Point(15, 67.5));
		new GUIWire(submodelModifiable, cp4, nand4.getPin("B"), new Point(15, 92.5));
		new GUIWire(submodelModifiable, nand1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nand2.getPin("Y"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nand3.getPin("Y"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nand4.getPin("Y"), Y4, new Point[0]);
	}
}