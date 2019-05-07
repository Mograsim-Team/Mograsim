package era.mi.logic.components.gates;

import era.mi.logic.Util;
import era.mi.logic.WireArray;
import era.mi.logic.components.BasicComponent;

public class XorGate extends BasicComponent
{
	private WireArray a, b, out;
	
	public XorGate(int processTime, WireArray a, WireArray b, WireArray out)
	{
		super(processTime);
		this.a = a;
		a.addObserver(this);
		this.b = b;
		b.addObserver(this);
		this.out = out;
	}

	protected void compute()
	{
		out.feedSignals(Util.xor(a.getValues(), b.getValues()));
	}

	public WireArray getA()
	{
		return a;
	}

	public WireArray getB()
	{
		return b;
	}

	public WireArray getOut()
	{
		return out;
	}
}
