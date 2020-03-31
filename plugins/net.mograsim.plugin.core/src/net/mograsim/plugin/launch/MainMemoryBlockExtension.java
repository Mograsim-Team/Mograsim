package net.mograsim.plugin.launch;

import static net.mograsim.plugin.preferences.PluginPreferences.MAX_MEMORY_CHANGE_INTERVAL;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlockExtension;
import org.eclipse.debug.core.model.IMemoryBlockRetrievalExtension;
import org.eclipse.debug.core.model.MemoryByte;

import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.plugin.MograsimActivator;

public class MainMemoryBlockExtension extends PlatformObject implements IMemoryBlockExtension
{
	private static final byte MEM_BYTE_FLAGS = (byte) (MemoryByte.READABLE | MemoryByte.WRITABLE | MemoryByte.ENDIANESS_KNOWN
			| MemoryByte.BIG_ENDIAN);

	private final String expression;
	private final MachineDebugTarget debugTarget;
	private final MainMemory mem;

	private final MainMemoryDefinition memDef;
	private final int cellWidthBits;
	private final int cellWidthBytes;
	private final BigInteger cellWidthBytesBI;
	private final BigInteger minAddrWords;
	private final BigInteger maxAddrWords;

	private BigInteger baseAddrWords;
	private BigInteger lengthWords;

	private final Set<Object> clients;
	private final MemoryCellModifiedListener memListener;
	private final AtomicBoolean memListenerRegistered;

	private final int maxContentChangeInterval;
	private final Object contentChangeLock;
	private Thread contentChangeThread;
	private long nextContentChangeAllowedMillis;
	private boolean contentChangeQueued;

	public MainMemoryBlockExtension(MachineDebugTarget debugTarget, String expression, @SuppressWarnings("unused") Object expressionContext)
			throws DebugException
	{
		this.expression = expression;
		this.debugTarget = debugTarget;
		this.mem = debugTarget.getMachine().getMainMemory();

		this.memDef = mem.getDefinition();
		this.cellWidthBits = memDef.getCellWidth();
		this.cellWidthBytes = (cellWidthBits + 7) / 8;
		this.cellWidthBytesBI = BigInteger.valueOf(cellWidthBytes);
		this.minAddrWords = BigInteger.valueOf(memDef.getMinimalAddress());
		this.maxAddrWords = BigInteger.valueOf(memDef.getMaximalAddress());

		// TODO parse expression better
		this.baseAddrWords = new BigInteger(expression, 16);
		this.lengthWords = BigInteger.ONE;

		if (baseAddrWords.compareTo(minAddrWords) < 0 || baseAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("Base address out of range");
		if (baseAddrWords.add(lengthWords).subtract(BigInteger.ONE).compareTo(maxAddrWords) > 0)
			throwDebugException("End address out of range");

		this.clients = new HashSet<>();
		// don't check whether the address is in range, because this memory block could be read outside its "range"
		this.memListener = a -> queueFireContentChangeEvent();
		this.memListenerRegistered = new AtomicBoolean();

		// TODO add a listener
		this.maxContentChangeInterval = MograsimActivator.instance().getPluginPrefs().getInt(MAX_MEMORY_CHANGE_INTERVAL);
		this.contentChangeLock = new Object();
		this.nextContentChangeAllowedMillis = System.currentTimeMillis() - maxContentChangeInterval - 1;
	}

	@Override
	public long getStartAddress()
	{
		return baseAddrWords.multiply(cellWidthBytesBI).longValueExact();
	}

	@Override
	public long getLength()
	{
		return lengthWords.multiply(cellWidthBytesBI).longValueExact();
	}

	@Override
	public byte[] getBytes() throws DebugException
	{
		BigInteger endAddrWords = baseAddrWords.add(lengthWords);
		if (endAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("End address out of range");
		int lengthBytes = lengthWords.multiply(cellWidthBytesBI).intValueExact();

		byte[] bytes = new byte[lengthBytes];
		int i;
		long j;
		for (i = 0, j = baseAddrWords.longValue(); i < lengthBytes; i += cellWidthBytes, j++)
		{
			BigInteger word = mem.getCellAsBigInteger(j);
			System.arraycopy(word.toByteArray(), 0, bytes, i, cellWidthBytes);
		}
		return bytes;
	}

	@Override
	public boolean supportsValueModification()
	{
		return true;
	}

	@Override
	public void setValue(long offset, byte[] bytes) throws DebugException
	{
		if (offset % cellWidthBytes != 0 || bytes.length % cellWidthBytes != 0)
			throwDebugException("Requested unaligned memory write");
		BigInteger startAddrWords = baseAddrWords.add(BigInteger.valueOf(offset / cellWidthBytes));
		if (startAddrWords.compareTo(minAddrWords) < 0 || startAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("Start address out of range");

		BigInteger endAddrWords = startAddrWords.add(BigInteger.valueOf(bytes.length / cellWidthBytes));
		if (endAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("End address out of range");

		int i;
		long j;
		for (i = 0, j = startAddrWords.longValue(); i < bytes.length; i += cellWidthBytes, j++)
		{
			BigInteger word = new BigInteger(bytes, i, cellWidthBytes);
			mem.setCellAsBigInteger(j, word);
		}
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
	public String getExpression()
	{
		return expression;
	}

	@Override
	public BigInteger getBigBaseAddress() throws DebugException
	{
		return baseAddrWords;
	}

	@Override
	public BigInteger getMemoryBlockStartAddress() throws DebugException
	{
		return minAddrWords;
	}

	@Override
	public BigInteger getMemoryBlockEndAddress() throws DebugException
	{
		return maxAddrWords;
	}

	@Override
	public BigInteger getBigLength() throws DebugException
	{
		return maxAddrWords.subtract(minAddrWords);
	}

	@Override
	public int getAddressSize() throws DebugException
	{
		return (getBigLength().bitLength() + 7) / 8;
	}

	@Override
	public boolean supportBaseAddressModification() throws DebugException
	{
		return true;
	}

	@Override
	public boolean supportsChangeManagement()
	{
		return false;
	}

	@Override
	public void setBaseAddress(BigInteger address) throws DebugException
	{
		if (address.compareTo(minAddrWords) < 0 || address.compareTo(maxAddrWords) > 0)
			throwDebugException("Address out of range");
		this.baseAddrWords = address;
	}

	@Override
	public MemoryByte[] getBytesFromOffset(BigInteger unitOffset, long addressableUnits) throws DebugException
	{
		return getBytesFromAddress(getBigBaseAddress().add(unitOffset), addressableUnits);
	}

	@Override
	public MemoryByte[] getBytesFromAddress(BigInteger address, long units) throws DebugException
	{
		if (units < 0)
			throwDebugException("Requested negative amount of unites");
		int lengthBytes = BigInteger.valueOf(units).multiply(cellWidthBytesBI).intValueExact();

		MemoryByte[] bytes = new MemoryByte[lengthBytes];
		int i;
		BigInteger j;
		for (i = 0, j = address; i < lengthBytes; i += cellWidthBytes, j = j.add(BigInteger.ONE))
		{
			if (j.compareTo(minAddrWords) >= 0 && j.compareTo(maxAddrWords) <= 0)
			{
				BigInteger word = mem.getCellAsBigInteger(j.longValue());
				byte[] wordBytes = word.toByteArray();
				int l = wordBytes[0] == 0 ? 1 : 0;
				int k;
				for (k = 0; k < cellWidthBytes - wordBytes.length + l; k++)
					bytes[i + k] = new MemoryByte((byte) 0, MEM_BYTE_FLAGS);
				for (; k < cellWidthBytes; k++, l++)
					bytes[i + k] = new MemoryByte(wordBytes[l], MEM_BYTE_FLAGS);
			} else
				for (int k = 0; k < cellWidthBytes; k++)
					bytes[i + k] = new MemoryByte((byte) 0, (byte) 0);
		}
		return bytes;
	}

	@Override
	public void setValue(BigInteger offset, byte[] bytes) throws DebugException
	{
		if (bytes.length % cellWidthBytes != 0)
			throwDebugException("Requested unaligned memory write");
		BigInteger startAddrWords = baseAddrWords.add(offset);
		if (startAddrWords.compareTo(minAddrWords) < 0 || startAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("Start address out of range");
		BigInteger endAddrWords = startAddrWords.add(BigInteger.valueOf(bytes.length / cellWidthBytes));
		if (endAddrWords.compareTo(maxAddrWords) > 0)
			throwDebugException("End address out of range");

		unregisterMemoryListener();

		int i;
		long j;
		for (i = 0, j = startAddrWords.longValue(); i < bytes.length; i += cellWidthBytes, j++)
		{
			BigInteger word = new BigInteger(bytes, i, cellWidthBytes);
			mem.setCellAsBigInteger(j, word);
		}

		if (!clients.isEmpty())
			registerMemoryListener();
		queueFireContentChangeEvent();
	}

	@Override
	public void connect(Object client)
	{
		registerMemoryListener();
		clients.add(client);
	}

	@Override
	public void disconnect(Object client)
	{
		clients.remove(client);

		if (clients.isEmpty())
			unregisterMemoryListener();
	}

	@Override
	public Object[] getConnections()
	{

		Set<Object> clientsLocal = clients;
		return clientsLocal == null ? new Object[0] : clientsLocal.toArray();
	}

	private void registerMemoryListener()
	{
		if (!memListenerRegistered.getAndSet(true))
			mem.registerCellModifiedListener(memListener);
	}

	private void unregisterMemoryListener()
	{
		if (memListenerRegistered.getAndSet(false))
			mem.deregisterCellModifiedListener(memListener);

		Thread contentChangeThreadLocal;
		synchronized (contentChangeLock)
		{
			contentChangeThreadLocal = contentChangeThread;
			// set contentChangeQueued here to prevent the following scenario:
			// 1. A change event is requested -> it gets fired "directly"
			// 2. A second change event is requested during the "cooldown time" -> a queue thread gets started
			// 3. The last client is disconnected -> the queue thread is interrupted
			// 4. A new client is connected
			// 5. A third change event is requested; queueFireContentChangeEvent locks contentChangeLock
			// before the queue thread locks contentChangeLock.
			// Now queueFireContentChangeEvent would return doing nothing, since contentChangeQueued still is true,
			// causing a change event to be missed.
			contentChangeQueued = false;
		}
		if (contentChangeThreadLocal != null)
			contentChangeThreadLocal.interrupt();
	}

	@Override
	public void dispose() throws DebugException
	{
		clients.clear();
		unregisterMemoryListener();
	}

	@Override
	public IMemoryBlockRetrievalExtension getMemoryBlockRetrieval()
	{
		return debugTarget;
	}

	@Override
	public int getAddressableSize() throws DebugException
	{
		return cellWidthBytes;
	}

	private void queueFireContentChangeEvent()
	{
		long sleepTime;
		boolean fireInOwnThread = false;
		synchronized (contentChangeLock)
		{
			if (contentChangeQueued)
				return;
			long nextContentChangeAllowedMillisLocal = nextContentChangeAllowedMillis;
			long now = System.currentTimeMillis();
			sleepTime = nextContentChangeAllowedMillisLocal - now;
			if (sleepTime >= 0)
			{
				fireInOwnThread = true;
				contentChangeQueued = true;
				nextContentChangeAllowedMillis = nextContentChangeAllowedMillisLocal + maxContentChangeInterval;
			} else
			{
				fireInOwnThread = false;
				nextContentChangeAllowedMillis = now + maxContentChangeInterval;
			}
		}
		if (fireInOwnThread)
		{
			// the following two statements can't cause racing problems since we set contentChangeQueued to true,
			// which means no-one will write this field until the thread started:
			// this method will never get here, and in this moment there is no (other) content change thread running,
			// since contentChangeQueued was false
			contentChangeThread = new Thread(() ->
			{
				boolean interrupted = false;
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (@SuppressWarnings("unused") InterruptedException e)
				{
					interrupted = true;
				}
				synchronized (contentChangeLock)
				{
					contentChangeThread = null;
					contentChangeQueued = false;
				}
				if (!interrupted && !Thread.interrupted())
					fireContentChangeEventNow();
			});
			contentChangeThread.start();
		} else
			fireContentChangeEventNow();
	}

	/**
	 * Fires a terminate event for this debug element.
	 */
	private void fireContentChangeEventNow()
	{
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE, DebugEvent.CONTENT));
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

	private static void throwDebugException(String message) throws DebugException
	{
		throw new DebugException(
				new Status(IStatus.ERROR, MograsimActivator.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED, message, null));
	}
}