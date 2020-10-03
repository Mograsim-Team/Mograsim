package net.mograsim.machine;

public interface MainMemory extends BitVectorMemory
{
	@Override
	public MainMemoryDefinition getDefinition();
}