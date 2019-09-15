package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900MachineDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.mi.components.ModelMicroInstructionMemory;

public class ModelAm2900MicroInstructionMemory extends ModelMicroInstructionMemory
{
	public ModelAm2900MicroInstructionMemory(LogicModelModifiable model, String name)
	{
		super(model, Am2900MachineDefinition.getInstance().getMicroInstructionMemoryDefinition(), name);
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new ModelAm2900MicroInstructionMemoryAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MicroInstructionMemory.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MicroInstructionMemory(m, n);
		});
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MicroInstructionMemory";
	}
}
