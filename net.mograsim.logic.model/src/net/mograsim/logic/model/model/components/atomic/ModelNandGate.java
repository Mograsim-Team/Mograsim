package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreNandGate;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelNandGate extends SimpleRectangularModelGate
{
	public ModelNandGate(LogicModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelNandGate(LogicModelModifiable model, int logicWidth, String name)
	{
		super(model, "NandGate", "&", true, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new SimpleGateAdapter<>(ModelNandGate.class, CoreNandGate::new));
		IndirectModelComponentCreator.setComponentSupplier(ModelNandGate.class.getCanonicalName(),
				(m, p, n) -> new ModelNandGate(m, p.getAsInt(), n));
	}
}