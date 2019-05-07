package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.WireArray;
import era.mi.logic.components.BasicComponent;

public class NotGate extends BasicComponent
{
	private WireArray in, out;
	
	public NotGate(int processTime, WireArray in, WireArray out)
	{
		super(processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
	}
	
	public void compute()
	{
		out.feedSignals(Util.not(in.getValues()));
	}

	public WireArray getIn()
	{
		return in;
	}

	public WireArray getOut()
	{
		return out;
	}
}
