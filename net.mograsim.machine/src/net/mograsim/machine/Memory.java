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
	
	public long size();
}
