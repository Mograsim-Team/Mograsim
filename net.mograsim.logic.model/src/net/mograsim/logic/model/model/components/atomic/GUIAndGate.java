package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIAndGate extends SimpleRectangularGUIGate
{
	public GUIAndGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUIAndGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "GUIAndGate", "&", false, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUIAndGate.class, AndGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUIAndGate.class.getCanonicalName(),
				(m, p, n) -> new GUIAndGate(m, p.getAsInt(), n));
	}
}