package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
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
		this.output = output;
	}

	public void switchFullOn()
	{
		setState(BitVector.of(Bit.ONE, output.length()));
	}

	public void switchFullOff()
	{
		setState(BitVector.of(Bit.ZERO, output.length()));
	}

	public void toggle()
	{
		if (isFullOn())
			switchFullOff();
		else
			switchFullOn();
	}

	public void setState(Bit bit)
	{
		setState(BitVector.of(bit));
	}

	public void setState(BitVector bits)
	{
		if (bits.length() != output.length())
			throw new IllegalArgumentException("Incorrect bit vector length");
		if (bits.equals(output.getInputValues()))
			return;
		output.feedSignals(bits);
		notifyObservers();
	}

	public boolean isFullOn()
	{
		return BitVector.of(Bit.ONE, output.length()).equals(output.getInputValues());
	}

	public BitVector getValues()
	{
		return output.getInputValues();
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
