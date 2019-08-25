package net.mograsim.plugin.memory;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import net.mograsim.machine.MainMemory;

public class MemoryTableContentProvider implements IStructuredContentProvider
{
	private long lower;
	private int amount;
	public final static int limit = 128;

	@Override
	public Object[] getElements(Object arg0)
	{
		if (arg0 == null)
			return new Object[0];
		MainMemory memory = (MainMemory) arg0;
		lower = Long.max(lower, memory.getDefinition().getMinimalAddress());
		Object[] rows = new Object[amount];
		for (int i = 0; i < amount; i++)
			rows[i] = new MemoryTableRow(lower + i, memory);
		return rows;
	}

	/**
	 * Sets the bounds for the addresses in memory to be provided to the table.
	 * 
	 * @param lower  lower bound for address (inclusive)
	 * @param amount amount of cells to show; limited to {@link MemoryTableContentProvider#limit}
	 */
	public void setAddressRange(long lower, int amount)
	{
		this.lower = lower;
		this.amount = Integer.min(Integer.max(amount, 0), limit);
	}

	public void setLowerBound(long lower)
	{
		setAddressRange(lower, amount);
	}

	public void setAmount(int amount)
	{
		setAddressRange(lower, amount);
	}

	public long getLowerBound()
	{
		return lower;
	}

	public int getAmount()
	{
		return amount;
	}
}
