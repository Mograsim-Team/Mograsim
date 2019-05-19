package era.mi.logic.components;

import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;
import era.mi.logic.wires.WireArrayObserver;

public class Connector implements WireArrayObserver, Component
{
	private boolean connected;
	private final WireArray a;
	private final WireArray b;
	private final WireArrayEnd aI;
	private final WireArrayEnd bI;

	public Connector(WireArray a, WireArray b)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException(String.format("WireArray width does not match: %d, %d", a.length, b.length));
		this.a = a;
		this.b = b;
		a.addObserver(this);
		b.addObserver(this);
		aI = a.createInput();
		bI = b.createInput();
	}

	public void connect()
	{
		connected = true;
		update(a);
		update(b);
	}

	public void disconnect()
	{
		connected = false;
		aI.clearSignals();
		bI.clearSignals();
	}

	public void setConnection(boolean connected)
	{
		if (connected)
			connect();
		else
			disconnect();
	}

	@Override
	public void update(WireArray initiator, Bit[] oldValues)
	{
		if (connected)
			Simulation.TIMELINE.addEvent(e -> update(initiator), 1);
	}

	private void update(WireArray initiator)
	{
		if (initiator == a)
			bI.feedSignals(aI.wireValuesExcludingMe());
		else
			aI.feedSignals(bI.wireValuesExcludingMe());
	}

	@Override
	public List<WireArray> getAllInputs()
	{
		return List.of(a, b);
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return List.of(a, b);
	}
}
