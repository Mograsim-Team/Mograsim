package net.mograsim.plugin.asm.model;

import java.util.Objects;

public final class AsmLabel implements AsmElement
{
	private final String name;
	private AsmInstruction inst;

	public AsmLabel(String name)
	{
		this.name = Objects.requireNonNull(name);
	}

	public String getName()
	{
		return name;
	}

	public void setInst(AsmInstruction inst)
	{
		if (inst != null)
			throw new IllegalStateException("Instrution already set for " + name);
		this.inst = inst;
	}

	@Override
	public String toString()
	{
		return name + ":";
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(inst, name);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof AsmLabel))
			return false;
		AsmLabel other = (AsmLabel) obj;
		return Objects.equals(inst, other.inst) && Objects.equals(name, other.name);
	}
}
