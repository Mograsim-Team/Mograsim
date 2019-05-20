package era.mi.logic.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.function.LongConsumer;

import era.mi.logic.Simulation;
import era.mi.logic.components.BitDisplay;
import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire.WireEnd;

public final class TestBitDisplay extends BitDisplay
{

	public TestBitDisplay(WireEnd in)
	{
		super(in);
	}

	public void assertDisplays(Bit... expected)
	{
		assertArrayEquals(expected, getDisplayedValue().getBits());
	}

	public void assertAfterSimulationIs(Bit... expected)
	{
		Simulation.TIMELINE.executeAll();
		assertDisplays(expected);
	}

	public void assertAfterSimulationIs(LongConsumer r, Bit... expected)
	{
		while (Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
			r.accept(Simulation.TIMELINE.getSimulationTime());
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
