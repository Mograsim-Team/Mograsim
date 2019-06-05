package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUImux1 extends SimpleRectangularSubmodelComponent
{
	public GUImux1(ViewModelModifiable model)
	{
		super(model, 1, "GUImux1");
		setSize(80, 40);
		setSubmodelScale(.4);
		setInputCount(3);
		setOutputCount(1);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents()
	{
		Pin S0 = getInputSubmodelPins().get(0);
		Pin I0 = getInputSubmodelPins().get(1);
		Pin I1 = getInputSubmodelPins().get(2);
		Pin Y = getOutputSubmodelPins().get(0);

		GUINandGate nandS0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI0 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY = new GUINandGate(submodelModifiable, 1);

		nandS0.moveTo(10, 7.5);
		nandI0.moveTo(35, 22.5);
		nandI1.moveTo(35, 47.5);
		nandY.moveTo(60, 30);

		WireCrossPoint cp0 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);

		cp0.moveTo(5, 12.5);
		cp1.moveTo(5, 22.5);

		new GUIWire(submodelModifiable, S0, cp0, new Point[0]);
		new GUIWire(submodelModifiable, cp0, nandS0.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cp0, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nandS0.getInputPins().get(1), new Point[0]);

		new GUIWire(submodelModifiable, nandS0.getOutputPin(), nandI0.getInputPins().get(0));
		new GUIWire(submodelModifiable, I0, nandI0.getInputPins().get(1), new Point[0]);

		new GUIWire(submodelModifiable, cp1, nandI1.getInputPins().get(0),
				new Point(cp1.getPin().getPos().x, nandI1.getInputPins().get(0).getPos().y));
		new GUIWire(submodelModifiable, I1, nandI1.getInputPins().get(1), new Point[0]);

		new GUIWire(submodelModifiable, nandI0.getOutputPin(), nandY.getInputPins().get(0));
		new GUIWire(submodelModifiable, nandI1.getOutputPin(), nandY.getInputPins().get(1));

		new GUIWire(submodelModifiable, nandY.getOutputPin(), Y);
	}
}