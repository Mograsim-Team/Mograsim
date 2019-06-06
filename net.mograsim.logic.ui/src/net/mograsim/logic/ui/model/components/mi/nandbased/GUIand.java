package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIand extends SimpleRectangularSubmodelComponent
{
	public GUIand(ViewModelModifiable model)
	{
		super(model, 1, "GUIand");
		setSubmodelScale(.4);
		setInputCount(2);
		setOutputCount(1);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getInputSubmodelPins().get(0);
		Pin B = getInputSubmodelPins().get(1);
		Pin Y = getOutputSubmodelPins().get(0);

		GUINandGate nand = new GUINandGate(submodelModifiable, 1);
		GUINandGate not = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);

		nand.moveTo(20, 15);
		not.moveTo(50, 15);
		cp1.moveCenterTo(45, 25);

		new GUIWire(submodelModifiable, A, nand.getInputPins().get(0));
		new GUIWire(submodelModifiable, B, nand.getInputPins().get(1));
		new GUIWire(submodelModifiable, nand.getOutputPin(), cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, not.getInputPins().get(0), new Point(45, 20));
		new GUIWire(submodelModifiable, cp1, not.getInputPins().get(1), new Point(45, 30));
		new GUIWire(submodelModifiable, not.getOutputPin(), Y);
	}
}