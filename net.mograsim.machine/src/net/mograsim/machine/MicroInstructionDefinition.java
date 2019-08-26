package net.mograsim.machine;

import net.mograsim.machine.mnemonics.Mnemonic;
import net.mograsim.machine.mnemonics.MnemonicFamily;

public interface MicroInstructionDefinition
{
	/**
	 * @return The {@link MnemonicFamily}s of which a MicroInstruction is composed.
	 */
	public MnemonicFamily[] getMnemonicFamilies();
	
	/**
	 * @return The amount of {@link Mnemonic}s a {@link MicroInstruction} that follows this definition consists of.
	 */
	public int size();
	
}
