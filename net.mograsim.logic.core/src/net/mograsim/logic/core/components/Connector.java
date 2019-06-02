package net.mograsim.logic.core.components;

import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

public class Connector extends Component implements LogicObserver
{
	private boolean connected;
	private final ReadWriteEnd a;
	private final ReadWriteEnd b;

	public Connector(Timeline timeline, ReadWriteEnd a, ReadWriteEnd b)
	{
		super(timeline);
		if (a.length() != b.length())
			throw new IllegalArgumentException(String.format("WireArray width does not match: %d, %d", a.length(), b.length()));
		this.a = a;
		this.b = b;
		a.registerObserver(this);
		b.registerObserver(this);
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
	public void update(LogicObservable initiator)
	{
		if (connected)
			timeline.addEvent(e -> innerUpdate(initiator), 1);
	}

	private void innerUpdate(LogicObservable initiator)
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
