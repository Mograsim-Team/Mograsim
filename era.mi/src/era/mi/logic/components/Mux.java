package era.mi.logic.components;

import era.mi.logic.Bit;
import era.mi.logic.WireArray;

/**
 * Models a Multiplexer. A is selected when select bit is 1, B when select bit is 0. Outputs X otherwise.
 * @author Fabian
 *
 */
public class Mux extends BasicComponent
{
	private WireArray a, b, out;
	private WireArray select;
	private final int size;
	
	/**
	 * {@link WireArray}s a, b and out must be of uniform length, select
	 * @param a Must be of uniform length with b and out.
	 * @param b Must be of uniform length with a and out.
	 * @param select C
	 * @param out Must be of uniform length with a and b.
	 */
	public Mux(int processTime, WireArray a, WireArray b, WireArray select, WireArray out)
	{
		super(processTime);
		size = a.length();
		if(b.length() != out.length() || b.length() != size)
			throw new IllegalArgumentException("All MUX wire arrays must be of uniform length!");
		this.a = a;
		a.addObserver(this);
		this.b = b;
		b.addObserver(this);
		this.select = select;
		select.addObserver(this);
		this.out = out;
	}

	@Override
	protected void compute()
	{
		WireArray active = b;
		switch(select.getValue())
		{
		case ONE:
			active = a;
		case ZERO:
			out.feedSignals(active.getValues());
			break;
		default:
			Bit[] newValues = new Bit[size];
			for(int i = 0; i < size; i++)
				newValues[i] = Bit.X;
			out.feedSignals(newValues);
		}
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

	public WireArray getSelect()
	{
		return select;
	}
}
