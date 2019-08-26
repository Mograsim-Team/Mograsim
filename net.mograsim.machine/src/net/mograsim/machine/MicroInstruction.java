package net.mograsim.machine;

import net.mograsim.machine.mnemonics.Mnemonic;

public interface MicroInstruction {
	
	public Mnemonic getValue(int index);
	
	/**
	 * @return The amount of {@link Mnemonic}s, the instruction is composed of
	 */
	public int getSize();
}
