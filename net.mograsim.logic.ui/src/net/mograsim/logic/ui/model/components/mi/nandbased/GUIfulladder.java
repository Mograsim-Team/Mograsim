package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public class GUIfulladder extends SimpleRectangularSubmodelComponent
{
	public GUIfulladder(ViewModelModifiable model)
	{
		super(model, 1, "GUIfulladder");
		setSubmodelScale(.4);
		setInputCount(3);
		setOutputCount(2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getInputSubmodelPins().get(0);
		Pin B = getInputSubmodelPins().get(1);
		Pin C = getInputSubmodelPins().get(2);
		Pin Y = getOutputSubmodelPins().get(0);
		Pin Z = getOutputSubmodelPins().get(1);

		GUIhalfadder halfBC = new GUIhalfadder(submodelModifiable);
		GUIhalfadder halfAY = new GUIhalfadder(submodelModifiable);
		GUINandGate nandZ = new GUINandGate(submodelModifiable, 1);

		halfAY.moveTo(45, 7.5);
		halfBC.moveTo(5, 40);
		nandZ.moveTo(57.5, 40);

		new GUIWire(submodelModifiable, A, halfAY.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, B, halfBC.getInputPins().get(0));
		new GUIWire(submodelModifiable, C, halfBC.getInputPins().get(1));
		new GUIWire(submodelModifiable, halfBC.getOutputPins().get(0), halfAY.getInputPins().get(1));
		new GUIWire(submodelModifiable, halfBC.getOutputPins().get(1), nandZ.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, halfAY.getOutputPins().get(0), Y, new Point[0]);
		new GUIWire(submodelModifiable, halfAY.getOutputPins().get(1), nandZ.getInputPins().get(0), new Point(82.5, 22.5),
				new Point(82.5, 35), new Point(52.5, 35), new Point(52.5, 45));
		new GUIWire(submodelModifiable, nandZ.getOutputPin(), Z);
	}
}