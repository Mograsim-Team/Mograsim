package net.mograsim.logic.ui.model.components.atomic;

import net.mograsim.logic.core.components.gates.NandGate;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.ui.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUINandGate extends SimpleRectangularGUIGate
{
	public GUINandGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public GUINandGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "&", true, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(GUINandGate.class, NandGate::new));
		IndirectGUIComponentCreator.setComponentSupplier(GUINandGate.class.getCanonicalName(),
				(m, p, n) -> new GUINandGate(m, p.getAsInt(), n));
	}
}