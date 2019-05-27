package era.mi.logic.components;

import java.util.List;

import era.mi.logic.timeline.Timeline;
import era.mi.logic.types.Bit;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

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
