package net.mograsim.logic.model.am2900;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface TestableCircuit
{
	void setup();

	Result run();

	void clockOn(boolean isClockOn);

	default void assertRunSuccess()
	{
		assertEquals(Result.SUCCESS, run());
	}

	default void assertFullCycleSuccess()
	{
		assertRunSuccess();
		clockOn(false);
		assertRunSuccess();
		clockOn(true);
		assertRunSuccess();
	}

	public enum Result
	{
		SUCCESS, OUT_OF_TIME, ERROR;
	}
}
