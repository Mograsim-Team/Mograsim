package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreAndGate;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelAndGate extends SimpleRectangularModelGate
{
	public ModelAndGate(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelAndGate(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, "AndGate", "&", false, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new SimpleGateAdapter<>(ModelAndGate.class, CoreAndGate::new));
		IndirectModelComponentCreator.setComponentSupplier(ModelAndGate.class.getCanonicalName(),
				(m, p, n) -> new ModelAndGate(m, p.getAsInt(), n));
	}
}