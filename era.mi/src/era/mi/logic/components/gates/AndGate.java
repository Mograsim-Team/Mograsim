package era.mi.logic.components.gates;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Util;
import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

public class AndGate extends BasicComponent
{
	private WireArray a, b, out;
	private WireArrayInput outI;
	
	public AndGate(int processTime, WireArray a, WireArray b, WireArray out)
	{
		super(processTime);
		this.a = a;
		a.addObserver(this);
		this.b = b;
		b.addObserver(this);
		this.out = out;
		outI = out.createInput();
	}

	protected void compute()
	{
		outI.feedSignals(Util.and(a.getValues(), b.getValues()));
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

	@Override
	public List<WireArray> getAllInputs()
	{
		return Collections.unmodifiableList(Arrays.asList(a, b));
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return Collections.unmodifiableList(Arrays.asList(out));
	}
}
