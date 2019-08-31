package net.mograsim.machine.mi;

import net.mograsim.machine.mi.parameters.ParameterClassification;

class StandardMicroInstructionDefinition implements MicroInstructionDefinition
{
	private ParameterClassification[] classes;
	
	public StandardMicroInstructionDefinition(ParameterClassification... classes)
	{
		this.classes = classes;
	}

	@Override
	public ParameterClassification[] getParameterClassifications()
	{
		return classes.clone();
	}

	@Override
	public ParameterClassification getParameterClassification(int index)
	{
		return classes[index];
	}

}
