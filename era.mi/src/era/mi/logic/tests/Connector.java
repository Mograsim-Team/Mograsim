package era.mi.logic.tests;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;
import era.mi.logic.wires.WireObserver;

public class Connector implements WireObserver
{
	private final Wire a;
//	private final WireArray b;
	private final WireEnd aI;
	private final WireEnd bI;

	public Connector(Wire a, Wire b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException(String.format("WireArray width does not match: %d, %d", a.length, b.length));
		this.a = a;
//		this.b = b;
		a.addObserver(this);
		b.addObserver(this);
		aI = a.createEnd();
		bI = b.createEnd();
	}

	@Override
	public void update(Wire initiator, Bit[] oldValues)
	{
		Simulation.TIMELINE.addEvent((e) ->
		{
			if (initiator == a)
				bI.feedSignals(aI.wireValuesExcludingMe());
			else
				aI.feedSignals(bI.wireValuesExcludingMe());
		}, 1);
	}
}
