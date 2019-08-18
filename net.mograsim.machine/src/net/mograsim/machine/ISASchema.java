package net.mograsim.machine;

import java.util.Set;

import net.mograsim.machine.isa.AsmOperand;

public interface ISASchema {
	Set<AsmOperand> getSupportedOperands();
	
	AddressingScema getAddressingSchema();
}
