package net.mograsim.plugin.tables;

public class TableRow<T>
{
	public final T data;
	public final long address;

	public TableRow(long address, T data)
	{
		this.data = data;
		this.address = address;
	}

	public T getData()
	{
		return data;
	}

	public long getAddress()
	{
		return address;
	}
}
