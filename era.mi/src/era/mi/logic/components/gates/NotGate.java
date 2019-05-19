package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.Util;
import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;

public class NotGate extends BasicComponent
{
	private WireArray in, out;
	private WireArrayEnd outI;

	public NotGate(int processTime, WireArray in, WireArray out)
	{
		super(processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
		outI = out.createInput();
	}

	@Override
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
		return List.of(in);
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return List.of(out);
	}
}
