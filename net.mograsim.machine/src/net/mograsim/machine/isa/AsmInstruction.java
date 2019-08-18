package net.mograsim.machine.isa;

import java.util.Objects;

public final class AsmInstruction implements AsmElement
{
	private final AsmOperation operation;
	private final AsmOperands operands;

	public AsmInstruction(AsmOperation operation, AsmOperands operands)
	{
		this.operation = Objects.requireNonNull(operation);
		this.operands = Objects.requireNonNull(operands);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(operands, operation);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof AsmInstruction))
			return false;
		AsmInstruction other = (AsmInstruction) obj;
		return Objects.equals(operands, other.operands) && Objects.equals(operation, other.operation);
	}

	@Override
	public String toString()
	{
		return String.format("%s %s", operation, operands).trim();
	}

}
