package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreBitDisplay extends BasicCoreComponent implements LogicObservable
{
	private Collection<LogicObserver> observers;
	private final ReadEnd in;
	private BitVector displayedValue;

	public CoreBitDisplay(Timeline timeline, ReadEnd in)
	{
		super(timeline, 1);
		observers = new ArrayList<>();
		this.in = in;
		in.registerObserver(this);
		compute();
	}

	@Override
	protected TimelineEventHandler compute()
	{
		BitVector newValues = in.getValues();
		return e ->
		{
			displayedValue = newValues;
			notifyObservers();
		};
	}

	public BitVector getDisplayedValue()
	{
		return displayedValue;
	}

	public boolean isDisplaying(Bit... values)
	{
		return displayedValue.equals(BitVector.of(values));
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of();
	}

	@Override
	public void registerObserver(LogicObserver ob)
	{
		observers.add(ob);
	}

	@Override
	public void deregisterObserver(LogicObserver ob)
	{
		observers.remove(ob);
	}

	@Override
	public void notifyObservers()
	{
		observers.forEach(ob -> ob.update(this));
	}
}
