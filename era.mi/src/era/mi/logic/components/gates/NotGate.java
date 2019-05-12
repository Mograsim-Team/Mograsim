package era.mi.logic.components.gates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Util;
import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

public class NotGate extends BasicComponent
{
	private WireArray in, out;
	private WireArrayInput outI;

	
	public NotGate(int processTime, WireArray in, WireArray out)
	{
		super(processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
		outI = out.createInput();
	}
	
	public void compute()
	{
		outI.feedSignals(Util.not(in.getValues()));
	}

	public WireArray getIn()
	{
		return in;
	}

	public WireArray getOut()
	{
		return out;
	}
	
	@Override
	public List<WireArray> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(in));
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(out));
	}
}
