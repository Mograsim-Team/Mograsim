package net.mograsim.logic.core.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

/**
 * This class models a simple on/off (ONE/ZERO) switch for user interaction.
 *
 * @author Christian Femers
 *
 */
public class CoreManualSwitch extends CoreComponent implements LogicObservable
{
	private Collection<LogicObserver> observers;
	private ReadWriteEnd output;
	private BitVector inputValues;

	public CoreManualSwitch(Timeline timeline, ReadWriteEnd output)
	{
		super(timeline);
		observers = new ArrayList<>();
		this.output = output;
		this.inputValues = output.getInputValues();
	}

	public void switchFullOn()
	{
		setState(BitVector.of(Bit.ONE, output.width()));
	}

	public void switchFullOff()
	{
		setState(BitVector.of(Bit.ZERO, output.width()));
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
		if (bits.length() != output.width())
			throw new IllegalArgumentException("Incorrect bit vector length");
		if (bits.equals(inputValues))
			return;
		inputValues = bits;
		output.feedSignals(bits);
		notifyObservers();
	}

	public boolean isFullOn()
	{
		return BitVector.of(Bit.ONE, output.width()).equals(output.getInputValues());
	}

	public BitVector getValues()
	{
		return inputValues;
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
