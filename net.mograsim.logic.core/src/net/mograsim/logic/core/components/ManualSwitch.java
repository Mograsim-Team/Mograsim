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
		setState(!isOn());
	}

	public void setState(boolean isOn)
	{
		setToValueOf(isOn ? Bit.ONE : Bit.ZERO);
	}

	public void setToValueOf(Bit bit)
	{
		if (!bit.isBinary())
			throw new IllegalArgumentException("Cannot set ManualSwitch to the value of Bit " + bit);
		if (bit == output.getInputValue())
			return;
		output.feedSignals(bit);
		notifyObservers();
	}

	public boolean isOn()
	{
		return output.getInputValue() == Bit.ONE;
	}

	public Bit getValue()
	{
		return output.getInputValue();
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
