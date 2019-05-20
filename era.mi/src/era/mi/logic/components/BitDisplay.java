package era.mi.logic.components;

import java.util.Arrays;
import java.util.List;

import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire.WireEnd;

public class BitDisplay extends BasicComponent
{
	private final WireEnd in;
	private Bit[] displayedValue;

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

	public Bit[] getDisplayedValue()
	{
		return displayedValue;
	}

	public boolean isDisplaying(Bit... values)
	{
		return Arrays.equals(displayedValue, values);
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
