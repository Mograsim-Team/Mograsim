package net.mograsim.logic.core.components;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEvent;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

public class CoreClock extends CoreComponent implements TimelineEventHandler, LogicObservable
{
	private Collection<LogicObserver> observers;
	private boolean isOn = false;// first update switches to 1; so the clock starts as 1.
	private ReadWriteEnd out;
	private int delta;

	/**
	 * 
	 * @param out   {@link CoreWire} the clock's impulses are fed into
	 * @param delta ticks between rising and falling edge
	 */
	public CoreClock(Timeline timeline, ReadWriteEnd out, int delta)
	{
		super(timeline);
		this.delta = delta;
		this.out = out;
		this.observers = new HashSet<>();
		addToTimeline();
	}

	@Override
	public void handle(TimelineEvent e)
	{
		addToTimeline();
		isOn = !isOn;
		out.feedSignals(getOutValues());
		notifyObservers();
	}

	public int getDelta()
	{
		return delta;
	}

	public BitVector getOutValues()
	{
		return isOn ? BitVector.SINGLE_1 : BitVector.SINGLE_0;
	}

	public boolean isOn()
	{
		return isOn;
	}

	private void addToTimeline()
	{
		timeline.addEvent(this, delta);
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
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
