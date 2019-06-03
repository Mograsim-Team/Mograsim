package net.mograsim.logic.ui.model.components;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public class TestSubmodelNANDComponent extends SubmodelComponent
{
	public TestSubmodelNANDComponent(ViewModelModifiable model)
	{
		super(model, "TestNAND");
		setSize(30, 20);
		setSubmodelScale(.5);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A = addSubmodelInterface(1, 0, 5);
		Pin B = addSubmodelInterface(1, 0, 15);
		Pin Y = addSubmodelInterface(1, 30, 10);

		GUIAndGate and = new GUIAndGate(submodelModifiable, 1);
		and.moveTo(5, 10);
		GUINotGate not = new GUINotGate(submodelModifiable, 1);
		not.moveTo(30, 15);

		new GUIWire(submodelModifiable, A, and.getInputPins().get(0));
		new GUIWire(submodelModifiable, B, and.getInputPins().get(1));
		new GUIWire(submodelModifiable, and.getOutputPin(), not.getInputPins().get(0));
		new GUIWire(submodelModifiable, not.getOutputPin(), Y);
	}
}