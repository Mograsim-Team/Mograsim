package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;

public class TriStateBuffer extends BasicComponent
{
	WireEnd in, enable;
	WireEnd out;

	public TriStateBuffer(int processTime, WireEnd in, WireEnd out, WireEnd enable)
	{
		super(processTime);
		if (in.length() != out.length())
			throw new IllegalArgumentException(
					"Tri-state output must have the same amount of bits as the input. Input: " + in.length() + " Output: " + out.length());
		if (enable.length() != 1)
			throw new IllegalArgumentException("Tri-state enable must have exactly one bit, not " + enable.length() + ".");
		this.in = in;
		in.addObserver(this);
		this.enable = enable;
		enable.addObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		if (enable.getValue() == Bit.ONE)
			out.feedSignals(in.getValues());
		else
			out.clearSignals();
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(in, enable));
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(out));
	}

}
