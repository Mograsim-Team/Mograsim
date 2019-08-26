package net.mograsim.machine;

public interface MicroprogramMemory {
	
	/**
	 * @param address The address of the desired instruction. Must be non-negative
	 * @return The instruction at the requested address
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public MicroInstruction getInstruction(long address);
	
	/**
	 * Sets the instruction at the supplied address
	 * @param address 
	 * @param instruction
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public void setInstruction(long address, MicroInstruction instruction);
}
