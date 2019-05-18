package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;

public class TriStateBuffer extends BasicComponent
{
	WireArray in, enable;
	WireArrayEnd outI;

	public TriStateBuffer(int processTime, WireArray in, WireArray out, WireArray enable)
	{
		super(processTime);
		if (in.length != out.length)
			throw new IllegalArgumentException(
					"Tri-state output must have the same amount of bits as the input. Input: " + in.length + " Output: " + out.length);
		if (enable.length != 1)
			throw new IllegalArgumentException("Tri-state enable must have exactly one bit, not " + enable.length + ".");
		this.in = in;
		in.addObserver(this);
		this.enable = enable;
		enable.addObserver(this);
		outI = out.createInput();
	}

	@Override
	protected void compute()
	{
		if (enable.getValue() == Bit.ONE)
			outI.feedSignals(in.getValues());
		else
			outI.clearSignals();
	}

	@Override
	public List<WireArray> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(in, enable));
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(outI.owner));
	}

}
