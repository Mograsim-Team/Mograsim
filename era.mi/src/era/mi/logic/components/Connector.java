package era.mi.logic.components;

import java.util.List;

import era.mi.logic.Simulation;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;
import era.mi.logic.wires.WireObserver;

public class Connector implements WireObserver, Component
{
	private boolean connected;
	private final ReadWriteEnd a;
	private final ReadWriteEnd b;

	public Connector(ReadWriteEnd a, ReadWriteEnd b)
	{
		if (a.length() != b.length())
			throw new IllegalArgumentException(String.format("WireArray width does not match: %d, %d", a.length(), b.length()));
		this.a = a;
		this.b = b;
		a.addObserver(this);
		b.addObserver(this);
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
		a.clearSignals();
		b.clearSignals();
	}

	public void setConnection(boolean connected)
	{
		if (connected)
			connect();
		else
			disconnect();
	}

	@Override
	public void update(ReadEnd initiator, BitVector oldValues)
	{
		if (connected)
			Simulation.TIMELINE.addEvent(e -> update(initiator), 1);
	}

	private void update(ReadEnd initiator)
	{
		if (initiator == a)
			b.feedSignals(a.wireValuesExcludingMe());
		else
			a.feedSignals(b.wireValuesExcludingMe());
	}

	@Override
	public List<ReadEnd> getAllInputs()
	{
		return List.of(a, b);
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(a, b);
	}
}
