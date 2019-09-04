package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUINotGate extends SimpleRectangularGUIGate
{
	public GUINotGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUINotGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "GUINotGate", "1", true, logicWidth, name);
		setInputCount(1);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUINotGate.class, (t, p, o, i) -> new NotGate(t, p, i[0], o)));
		IndirectGUIComponentCreator.setComponentSupplier(GUINotGate.class.getCanonicalName(),
				(m, p, n) -> new GUINotGate(m, p.getAsInt(), n));
	}
}