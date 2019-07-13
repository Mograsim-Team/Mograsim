package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIfulladder extends SimpleRectangularSubmodelComponent
{
	public GUIfulladder(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIfulladder(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIfulladder", name);
		setSubmodelScale(.4);
		setInputPins("A", "B", "C");
		setOutputPins("Y", "Z");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = getSubmodelPin("A");
		Pin B = getSubmodelPin("B");
		Pin C = getSubmodelPin("C");
		Pin Y = getSubmodelPin("Y");
		Pin Z = getSubmodelPin("Z");

		GUIhalfadder halfBC = new GUIhalfadder(submodelModifiable);
		GUIhalfadder halfAY = new GUIhalfadder(submodelModifiable);
		GUINandGate nandZ = new GUINandGate(submodelModifiable, 1);

		halfAY.moveTo(45, 7.5);
		halfBC.moveTo(5, 40);
		nandZ.moveTo(57.5, 40);

		new GUIWire(submodelModifiable, A, halfAY.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, B, halfBC.getPin("A"));
		new GUIWire(submodelModifiable, C, halfBC.getPin("B"));
		new GUIWire(submodelModifiable, halfBC.getPin("Y"), halfAY.getPin("B"));
		new GUIWire(submodelModifiable, halfBC.getPin("_Z"), nandZ.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, halfAY.getPin("Y"), Y, new Point[0]);
		new GUIWire(submodelModifiable, halfAY.getPin("_Z"), nandZ.getPin("A"), new Point(82.5, 22.5), new Point(82.5, 35),
				new Point(52.5, 35), new Point(52.5, 45));
		new GUIWire(submodelModifiable, nandZ.getPin("Y"), Z);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIfulladder.class.getCanonicalName(), (m, p, n) -> new GUIfulladder(m, n));
	}
}