package net.mograsim.machine;

public interface Memory<T>
{
	/**
	 * @param address The address of the desired data. Must be non-negative
	 * @return The data at the requested address
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public T getCell(long address);
	
	/**
	 * Sets the data at the supplied address
	 * @throws IndexOutOfBoundsException
	 */
	public void setCell(long address, T data);
	
	public default long size()
	{
		MemoryDefinition def = getDefinition();
		return Long.max(0, def.getMaximalAddress() - def.getMinimalAddress() + 1);
	}
	
	/**
	 * Registers an observer to be notified when a memory cell is modified
	 */
	public void registerObserver(MemoryObserver ob);
	
	public void deregisterObserver(MemoryObserver ob);
	
	public void notifyObservers(long address);
	
	public MemoryDefinition getDefinition();
}
