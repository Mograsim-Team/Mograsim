package mograsim.logic.core.components;

import java.util.List;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.types.Bit;
import mograsim.logic.core.types.BitVector;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class BitDisplay extends BasicComponent
{
	private final ReadEnd in;
	private BitVector displayedValue;

	public BitDisplay(Timeline timeline, ReadEnd in)
	{
		super(timeline, 1);
		this.in = in;
		in.addObserver(this);
		compute();
	}

	@Override
	protected void compute()
	{
		displayedValue = in.getValues();
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
}
