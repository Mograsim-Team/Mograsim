package net.mograsim.machine.mi;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;

class StandardMicroInstruction implements MicroInstruction
{
	private final Runnable updateCallback;
	private MicroInstructionParameter[] parameters;

	StandardMicroInstruction(Runnable updateCallback, MicroInstructionParameter... parameters)
	{
		this.updateCallback = updateCallback;
		this.parameters = parameters;
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

	/**
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public void setParameter(int index, MicroInstructionParameter param)
	{
		parameters[index] = param;
		updateCallback.run();
	}

}
