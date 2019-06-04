package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIand extends SubmodelComponent
{
	public GUIand(ViewModelModifiable model)
	{
		super(model, "GUIand");
		setSize(35, 25);
		setSubmodelScale(.4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = addSubmodelInterface(1, 0, 5);
		Pin B = addSubmodelInterface(1, 0, 20);
		Pin Y = addSubmodelInterface(1, 35, 12.5);

		GUINandGate nand = new GUINandGate(submodelModifiable, 1);
		nand.moveTo(20, 21.25);
		GUINandGate not = new GUINandGate(submodelModifiable, 1);
		not.moveTo(50, 21.25);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		cp1.moveTo(45, 31.25);

		new GUIWire(submodelModifiable, A, nand.getInputPins().get(0), new Point(10, 12.5), new Point(10, 26.25));
		new GUIWire(submodelModifiable, B, nand.getInputPins().get(1), new Point(10, 50), new Point(10, 36.25));
		new GUIWire(submodelModifiable, nand.getOutputPin(), cp1.getPin());
		new GUIWire(submodelModifiable, cp1.getPin(), not.getInputPins().get(0), new Point(45, 26.25));
		new GUIWire(submodelModifiable, cp1.getPin(), not.getInputPins().get(1), new Point(45, 36.25));
		new GUIWire(submodelModifiable, not.getOutputPin(), Y);
	}
}