package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreOrGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUIOrGate extends SimpleRectangularGUIGate
{
	public GUIOrGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUIOrGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "GUIOrGate", "\u22651", false, logicWidth, name);// ">=1"
		setInputCount(2);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUIOrGate.class, CoreOrGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUIOrGate.class.getCanonicalName(),
				(m, p, n) -> new GUIOrGate(m, p.getAsInt(), n));
	}
}