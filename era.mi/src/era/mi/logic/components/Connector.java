package era.mi.logic.components;

import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.WireEnd;
import era.mi.logic.wires.WireObserver;

public class Connector implements WireObserver, Component
{
	private boolean connected;
	private final WireEnd a;
	private final WireEnd b;

	public Connector(WireEnd a, WireEnd b)
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
		update(a.getWire());
		update(b.getWire());
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
	public void update(Wire initiator, Bit[] oldValues)
	{
		if (connected)
			Simulation.TIMELINE.addEvent(e -> update(initiator), 1);
	}

	private void update(Wire initiator)
	{
		if (initiator == a.getWire())
			b.feedSignals(a.wireValuesExcludingMe());
		else
			a.feedSignals(b.wireValuesExcludingMe());
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return List.of(a, b);
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(a, b);
	}
}
