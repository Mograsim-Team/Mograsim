package net.mograsim.logic.core.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.function.LongConsumer;

import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public final class TestBitDisplay extends BitDisplay
{

	public TestBitDisplay(Timeline timeline, ReadEnd in)
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
	protected void compute()
	{
		super.compute();
		System.out.println("update: value is " + getDisplayedValue());
	}
}
