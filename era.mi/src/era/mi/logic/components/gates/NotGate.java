package era.mi.logic.components.gates;

import java.util.List;

import era.mi.logic.components.BasicComponent;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

public class NotGate extends BasicComponent
{
	private ReadEnd in;
	private ReadWriteEnd out;

	public NotGate(int processTime, ReadEnd in, ReadWriteEnd out)
	{
		super(processTime);
		this.in = in;
		in.addObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		out.feedSignals(in.getValues().not());
	}

	public ReadEnd getIn()
	{
		return in;
	}

	public ReadEnd getOut()
	{
		return out;
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(in);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(out);
	}
}
