package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUInot4 extends SimpleRectangularSubmodelComponent
{
	public GUInot4(ViewModelModifiable model)
	{
		super(model, 1, "GUInot4");
		setSubmodelScale(.4);
		setInputCount(4);
		setOutputCount(4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A1 = getInputSubmodelPins().get(0);
		Pin A2 = getInputSubmodelPins().get(1);
		Pin A3 = getInputSubmodelPins().get(2);
		Pin A4 = getInputSubmodelPins().get(3);
		Pin Y1 = getOutputSubmodelPins().get(0);
		Pin Y2 = getOutputSubmodelPins().get(1);
		Pin Y3 = getOutputSubmodelPins().get(2);
		Pin Y4 = getOutputSubmodelPins().get(3);

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
		cp1.moveTo(15, 12.5);
		cp2.moveTo(15, 37.5);
		cp3.moveTo(15, 62.5);
		cp4.moveTo(15, 87.5);

		new GUIWire(submodelModifiable, A1, cp1, new Point[0]);
		new GUIWire(submodelModifiable, A2, cp2, new Point[0]);
		new GUIWire(submodelModifiable, A3, cp3, new Point[0]);
		new GUIWire(submodelModifiable, A4, cp4, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nand1.getInputPins().get(0), new Point(15, 7.5));
		new GUIWire(submodelModifiable, cp2, nand2.getInputPins().get(0), new Point(15, 32.5));
		new GUIWire(submodelModifiable, cp3, nand3.getInputPins().get(0), new Point(15, 57.5));
		new GUIWire(submodelModifiable, cp4, nand4.getInputPins().get(0), new Point(15, 82.5));
		new GUIWire(submodelModifiable, cp1, nand1.getInputPins().get(1), new Point(15, 17.5));
		new GUIWire(submodelModifiable, cp2, nand2.getInputPins().get(1), new Point(15, 42.5));
		new GUIWire(submodelModifiable, cp3, nand3.getInputPins().get(1), new Point(15, 67.5));
		new GUIWire(submodelModifiable, cp4, nand4.getInputPins().get(1), new Point(15, 92.5));
		new GUIWire(submodelModifiable, nand1.getOutputPin(), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nand2.getOutputPin(), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nand3.getOutputPin(), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nand4.getOutputPin(), Y4, new Point[0]);
	}
}