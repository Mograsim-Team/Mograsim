package net.mograsim.machine.isa;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.isa.types.AsmException;

public interface AsmOperand
{
	int getSize();

	BitVector parse(String s) throws AsmException;
}
