package net.mograsim.plugin.launch;

import java.math.BigInteger;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;

import net.mograsim.machine.MainMemory;
import net.mograsim.plugin.MograsimActivator;

@Deprecated
public class MainMemoryBlock extends PlatformObject implements IMemoryBlock
{
	private final MachineDebugTarget debugTarget;
	private final MainMemory mem;
	private final long startAddress;
	private final int length;

	public MainMemoryBlock(MachineDebugTarget debugTarget, long startAddress, long length)
	{
		MainMemory mem = debugTarget.getMachine().getMainMemory();

		if (length < 0)
			throw new IllegalArgumentException("Negative size");
		if (startAddress < 0)
			throw new IllegalArgumentException("Negative start address");
		if ((startAddress + length) / 2 > mem.size())
			throw new IllegalArgumentException("End address higher than memory size");
		if (length > Integer.MAX_VALUE)
			throw new IllegalArgumentException("Memory block bigger than Integer.MAX_VALUE (" + Integer.MAX_VALUE + "\"");
		if (startAddress % 2 != 0 || length % 2 != 0)
			throw new IllegalArgumentException("Unaligned memory block");

		this.debugTarget = debugTarget;
		this.mem = mem;
		this.startAddress = startAddress;
		this.length = (int) length;

		mem.registerCellModifiedListener(e ->
		{
			if (e >= startAddress / 2 && e < (startAddress + length) / 2)
				fireContentChangeEvent();
		});
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
		byte[] bs = new byte[length];
		int i;
		long j;
		for (i = 0, j = startAddress / 2; i < length; i += 2, j++)
		{
			short word = mem.getCellAsBigInteger(j).shortValue();
			bs[i + 0] = (byte) (word & 0xFF);
			bs[i + 1] = (byte) (word >>> 8);
		}
		return bs;
	}

	@Override
	public boolean supportsValueModification()
	{
		return true;
	}

	@Override
	public void setValue(long offset, byte[] bytes) throws DebugException
	{
		if (offset % 2 != 0 || bytes.length % 2 != 0)
			throw new IllegalArgumentException("Can't write unaligned to a memory block");
		int i;
		long j;
		for (i = 0, j = (startAddress + offset) / 2; i < bytes.length; i += 2, j++)
		{
			short word = 0;
			word |= bytes[i + 0] & 0xFF;
			word |= bytes[i + 1] << 8;
			mem.setCellAsBigInteger(j, BigInteger.valueOf(word));
		}
	}

	/**
	 * Fires a terminate event for this debug element.
	 */
	private void fireContentChangeEvent()
	{
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	/**
	 * Fires a debug event.
	 *
	 * @param event debug event to fire
	 */
	private static void fireEvent(DebugEvent event)
	{
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}
}