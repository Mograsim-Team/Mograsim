package net.mograsim.machine.mi;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;

class StandardMicroInstruction implements MicroInstruction
{
	private MicroInstructionParameter[] parameters;

	StandardMicroInstruction(MicroInstructionParameter... parameters)
	{
		this.parameters = parameters.clone();
	}

	/**
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public MicroInstructionParameter getParameter(int index)
	{
		return parameters[index];
	}

	@Override
	public int getSize()
	{
		return parameters.length;
	}

	@Override
	public MicroInstructionParameter[] getParameters()
	{
		return parameters.clone();
	}

}
