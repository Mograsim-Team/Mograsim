package net.mograsim.plugin.tables.memory;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MemoryObserver;

public class MemoryTableContentProvider implements ILazyContentProvider, MemoryObserver
{
	private long lower;
	private TableViewer viewer;
	private final static int limit = 10_000;
	private int amount = 0;
	private MainMemory memory;

	public void setLowerBound(long lower)
	{
		if (memory != null)
			this.lower = Long.min(memory.getDefinition().getMaximalAddress(), Long.max(memory.getDefinition().getMinimalAddress(), lower));
		else
			this.lower = lower;
		updateItemCount();
	}

	public void updateItemCount()
	{
		if (memory != null)
		{
			long size = memory.getDefinition().getMaximalAddress() - lower;
			viewer.setItemCount(size > amount ? amount : (int) size);
		} else
			viewer.setItemCount(0);
	}

	public long getLowerBound()
	{
		return lower;
	}

	public void setAmount(int amount)
	{
		this.amount = Integer.min(amount, limit);
		updateItemCount();
	}

	public int getAmount()
	{
		return amount;
	}

	@Override
	public void updateElement(int index)
	{
		long address = lower + index;
		if (address <= memory.getDefinition().getMaximalAddress())
			viewer.replace(new MemoryTableRow(address, memory), index);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = (TableViewer) viewer;
		this.memory = (MainMemory) newInput;
		if (oldInput != null)
			((MainMemory) oldInput).deregisterObserver(this);
		if (memory != null)
			memory.registerObserver(this);
		setLowerBound(0L);
	}

	@Override
	public void update(long address)
	{
		Display.getDefault().asyncExec(() -> updateElement((int) (address - lower)));
	}
}
