package era.mi.logic.components;

import java.util.List;

import era.mi.logic.types.Bit;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.WireEnd;

public class BitDisplay extends BasicComponent
{
	private final WireEnd in;
	private BitVector displayedValue;

	public BitDisplay(WireEnd in)
	{
		super(1);
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
	public List<WireEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of();
	}
}
