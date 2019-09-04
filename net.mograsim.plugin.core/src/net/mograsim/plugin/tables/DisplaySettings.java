package net.mograsim.plugin.tables;

import java.util.HashSet;
import java.util.Set;

import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class DisplaySettings
{
	private NumberType dataNumberType;
	private final Set<Runnable> observers;

	public DisplaySettings()
	{
		this(NumberType.HEXADECIMAL);
	}

	public DisplaySettings(NumberType dataNumberType)
	{
		this.dataNumberType = dataNumberType;
		observers = new HashSet<>();
	}

	public NumberType getDataNumberType()
	{
		return dataNumberType;
	}

	public void setDataNumberType(NumberType dataNumberType)
	{
		this.dataNumberType = dataNumberType;
		notifyObservers();
	}

	void notifyObservers()
	{
		observers.forEach(r -> r.run());
	}

	public void addObserver(Runnable ob)
	{
		observers.add(ob);
	}

	public void removeObserver(Runnable ob)
	{
		observers.remove(ob);
	}
}