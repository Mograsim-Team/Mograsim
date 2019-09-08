package net.mograsim.logic.core.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.function.LongConsumer;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.TimelineEventHandler;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;

public final class TestCoreBitDisplay extends CoreBitDisplay
{

	public TestCoreBitDisplay(Timeline timeline, ReadEnd in)
	{
		super(timeline, in);
	}

	public void assertDisplays(Bit... expected)
	{
		assertArrayEquals(expected, getDisplayedValue().getBits());
	}

	public void assertAfterSimulationIs(Bit... expected)
	{
		timeline.executeAll();
		assertDisplays(expected);
	}

	public void assertAfterSimulationIs(LongConsumer r, Bit... expected)
	{
		while (timeline.hasNext())
		{
			timeline.executeNext();
			r.accept(timeline.getSimulationTime());
		}
		assertDisplays(expected);
	}

	@Override
	protected TimelineEventHandler compute()
	{
		TimelineEventHandler handler = super.compute();
		return e ->
		{
			handler.handle(e);
			System.out.println("update: value is " + getDisplayedValue());
		};
	}
}
