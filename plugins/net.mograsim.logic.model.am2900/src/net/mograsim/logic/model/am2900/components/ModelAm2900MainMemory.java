package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900MainMemoryDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.standard.memory.AbstractModelBitVectorMemory;

public class ModelAm2900MainMemory extends AbstractModelBitVectorMemory<MainMemory, MainMemoryDefinition>
{
	public ModelAm2900MainMemory(LogicModelModifiable model, String name)
	{
		super(model, 120, 150, 30, 50, 70, false, "RAM", Am2900MainMemoryDefinition.instance, name);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MainMemory";
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MainMemory.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MainMemory(m, n);
		});
	}
}