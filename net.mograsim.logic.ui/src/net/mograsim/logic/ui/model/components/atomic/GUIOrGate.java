package net.mograsim.logic.ui.model.components.atomic;

import net.mograsim.logic.core.components.gates.OrGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIOrGate extends SimpleRectangularGUIGate
{
	public GUIOrGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUIOrGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "\u22651", false, logicWidth, name);// ">=1"
		setInputCount(2);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUIOrGate.class, OrGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUIOrGate.class.getCanonicalName(),
				(m, p, n) -> new GUIOrGate(m, p.getAsInt(), n));
	}
}