package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdlatch extends SimpleRectangularSubmodelComponent
{
	public GUIdlatch(ViewModelModifiable model)
	{
		super(model, 1, "GUIdlatch");
		setSubmodelScale(.4);
		setInputCount(2);
		setOutputCount(2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin D = getInputSubmodelPins().get(0);
		Pin E = getInputSubmodelPins().get(1);
		Pin Q = getOutputSubmodelPins().get(0);
		Pin _Q = getOutputSubmodelPins().get(1);

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		GUI_rsLatch _rsLatch = new GUI_rsLatch(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 2.5);
		nand2.moveTo(15, 27.5);
		_rsLatch.moveTo(45, 7.5);
		cp1.moveTo(5, 37.5);
		cp2.moveTo(35, 12.5);

		new GUIWire(submodelModifiable, D, nand1.getInputPins().get(0));
		new GUIWire(submodelModifiable, E, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nand1.getInputPins().get(1), new Point(5, 17.5));
		new GUIWire(submodelModifiable, cp1, nand2.getInputPins().get(1), new Point(5, 42.5));
		new GUIWire(submodelModifiable, nand1.getOutputPin(), cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, nand2.getInputPins().get(0), new Point(35, 25), new Point(10, 25), new Point(10, 32.5));
		new GUIWire(submodelModifiable, cp2, _rsLatch.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, nand2.getOutputPin(), _rsLatch.getInputPins().get(1), new Point(40, 37.5), new Point(40, 22.5));
		new GUIWire(submodelModifiable, _rsLatch.getOutputPins().get(0), Q, new Point[0]);
		new GUIWire(submodelModifiable, _rsLatch.getOutputPins().get(1), _Q);
	}
}