package net.mograsim.logic.ui.model.components.atomic;

import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIAndGate extends SimpleRectangularGUIGate
{
	public GUIAndGate(ViewModelModifiable model, int logicWidth)
	{
		super(model, logicWidth, "&", false);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUIAndGate.class, AndGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUIAndGate.class.getCanonicalName(), (m, p) -> new GUIAndGate(m, p.getAsInt()));
	}
}