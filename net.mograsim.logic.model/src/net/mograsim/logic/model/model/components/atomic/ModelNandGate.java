package net.mograsim.logic.model.model.components.atomic;

import net.mograsim.logic.core.components.gates.CoreNandGate;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SimpleGateAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class ModelNandGate extends SimpleRectangularModelGate
{
	public ModelNandGate(ViewModelModifiable model, int logicWidth)
	{
		this(model, logicWidth, null);
	}

	public ModelNandGate(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, "NandGate", "&", true, logicWidth, name);
		setInputCount(2);// TODO make variable
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SimpleGateAdapter<>(ModelNandGate.class, CoreNandGate::new));
		IndirectModelComponentCreator.setComponentSupplier(ModelNandGate.class.getCanonicalName(),
				(m, p, n) -> new ModelNandGate(m, p.getAsInt(), n));
	}
}