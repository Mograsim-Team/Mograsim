package net.mograsim.machine.isa;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AsmOperands
{
	private final List<AsmOperand> operands;

	public AsmOperands(List<AsmOperand> operands)
	{
		this.operands = List.copyOf(Objects.requireNonNull(operands));
	}

	public List<AsmOperand> getOperands()
	{
		return operands;
	}

	@Override
	public int hashCode()
	{
		return operands.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof AsmOperands))
			return false;
		AsmOperands other = (AsmOperands) obj;
		return operands.equals(other.operands);
	}

	@Override
	public String toString()
	{
		return operands.stream().map(AsmOperand::toString).collect(Collectors.joining(", "));
	}
}
