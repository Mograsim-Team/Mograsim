package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

/**
 * This class models a simple on/off (ONE/ZERO) switch for user interaction.
 *
 * @author Christian Femers
 *
 */
public class ManualSwitch extends Component implements LogicObservable
{
	private Collection<LogicObserver> observers;
	private ReadWriteEnd output;
	private boolean isOn;

	public ManualSwitch(Timeline timeline, ReadWriteEnd output)
	{
		super(timeline);
		observers = new ArrayList<>();
		if (output.length() != 1)
			throw new IllegalArgumentException("Switch output can be only a single wire");
		this.output = output;
	}

	public void switchOn()
	{
		setState(true);
	}

	public void switchOff()
	{
		setState(false);
	}

	public void toggle()
	{
		setState(!isOn);
	}

	public void setState(boolean isOn)
	{
		if (this.isOn == isOn)
			return;
		this.isOn = isOn;
		output.feedSignals(getValue());
		notifyObservers();
	}

	public void setToValueOf(Bit bit)
	{
		if (bit == Bit.ONE)
			switchOn();
		else if (bit == Bit.ZERO)
			switchOff();
		else
			throw new IllegalArgumentException("Cannot set ManualSwitch to the value of Bit " + bit);
	}

	public boolean isOn()
	{
		return isOn;
	}

	public Bit getValue()
	{
		return isOn ? Bit.ONE : Bit.ZERO;
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(output);
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
