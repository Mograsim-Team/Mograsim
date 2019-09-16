package net.mograsim.logic.model.am2900.util;

import java.util.Objects;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.mograsim.logic.model.am2900.TestableCircuit;

public class DisplayStateOnFailure implements AfterTestExecutionCallback
{
	public static final boolean ACTIVE = true;

	private final TestableCircuit circuitUnderTest;

	public DisplayStateOnFailure(TestableCircuit circuitUnderTest)
	{
		this.circuitUnderTest = Objects.requireNonNull(circuitUnderTest);
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception
	{
		if (ACTIVE && context.getExecutionException().isPresent())
		{
			context.getExecutionException().get().printStackTrace();
			circuitUnderTest.displayState();
		}
	}
}
