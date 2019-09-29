package net.mograsim.plugin.launch;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;

import net.mograsim.machine.MainMemory;
import net.mograsim.plugin.MograsimActivator;

public class MainMemoryBlock implements IMemoryBlock
{
	private final MachineDebugTarget debugTarget;
	private final MainMemory mem;
	private final long startAddress;
	private final int length;

	public MainMemoryBlock(MachineDebugTarget debugTarget, long startAddress, long length)
	{
		if (length < 0)
			throw new IllegalArgumentException("Can't create a memory block of negative size");
		if (length > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Can't create a memory block bigger than Integer.MAX_VALUE (" + Integer.MAX_VALUE + "\"");
		if (startAddress % 2 != 0)
			throw new IllegalArgumentException("Can't create an unaligned memory block");

		this.debugTarget = debugTarget;
		this.mem = debugTarget.getMachine().getMainMemory();
		this.startAddress = startAddress;
		this.length = (int) length;
	}

	@Override
	public String getModelIdentifier()
	{
		return MograsimActivator.PLUGIN_ID;
	}

	@Override
	public IDebugTarget getDebugTarget()
	{
		return debugTarget;
	}

	@Override
	public ILaunch getLaunch()
	{
		return debugTarget.getLaunch();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter)
	{
		// TODO
		return null;
	}

	@Override
	public long getStartAddress()
	{
		return startAddress;
	}

	@Override
	public long getLength()
	{
		return length;
	}

	@Override
	public byte[] getBytes() throws DebugException
	{
		// TODO speedup. Maybe make a method for this in MainMemory?
		byte[] bs = new byte[length];
		int i;
		long j;
		for (i = 0, j = startAddress / 2; i < length; i++)
		{
			short word = mem.getCellAsBigInteger(j).shortValueExact();
			bs[i + 0] = (byte) (word & 0xFF);
			bs[i + 1] = (byte) (word >>> 8);
		}
		return bs;
	}

	@Override
	public boolean supportsValueModification()
	{
		// TODO
		return false;
	}

	@Override
	public void setValue(long offset, byte[] bytes) throws DebugException
	{
		// TODO
	}
}