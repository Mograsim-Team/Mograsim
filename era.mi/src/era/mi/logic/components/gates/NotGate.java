package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.Util;
import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.Wire.WireEnd;

public class NotGate extends BasicComponent
{
	private WireEnd in;
	private WireEnd out;

	public NotGate(int processTime, WireEnd in, WireEnd out)
	{
		super(processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		out.feedSignals(Util.not(in.getValues()));
	}

	public WireEnd getIn()
	{
		return in;
	}

	public WireEnd getOut()
	{
		return out;
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
