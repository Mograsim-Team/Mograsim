package net.mograsim.logic.model.am2900.components;

import net.mograsim.logic.model.am2900.machine.Am2900MPROMDefinition;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.mi.MPROM;
import net.mograsim.machine.mi.MPROMDefinition;
import net.mograsim.machine.standard.memory.AbstractModelBitVectorMemory;

public class ModelAm2900MPROM extends AbstractModelBitVectorMemory<MPROM, MPROMDefinition>
{
	public ModelAm2900MPROM(LogicModelModifiable model, String name)
	{
		super(model, 30, 80, 30, 50, -1, true, "MPROM", Am2900MPROMDefinition.instance, name);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "Am2900MPROM";
	}

	static
	{
		IndirectModelComponentCreator.setComponentSupplier(ModelAm2900MPROM.class.getCanonicalName(), (m, p, n) ->
		{
			return new ModelAm2900MPROM(m, n);
		});
	}
}