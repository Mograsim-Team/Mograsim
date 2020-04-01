package net.mograsim.logic.model.am2900;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.LogicUIStandaloneGUI;
import net.mograsim.logic.model.am2900.machine.Am2900Machine;
import net.mograsim.logic.model.am2900.machine.Am2900MainMemoryDefinition;
import net.mograsim.logic.model.am2900.machine.Am2900MicroInstructionDefinition;
import net.mograsim.logic.model.am2900.machine.Am2900MicroInstructionMemoryDefinition;
import net.mograsim.logic.model.am2900.machine.StrictAm2900MachineDefinition;
import net.mograsim.logic.model.am2900.machine.registers.am2901.NumberedRegister;
import net.mograsim.logic.model.preferences.DefaultRenderPreferences;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.machine.standard.memory.WordAddressableMemory;

@TestInstance(Lifecycle.PER_CLASS)
public class TestGCD
{
	private static final boolean startGUI = false;

	private Am2900Machine mach;
	private MicroInstructionMemory mpm;
	private MainMemory ram;
	private AtomicInteger machState;
	private AtomicInteger halfCycleCount;
	private AtomicReference<MicroInstructionMemory> mpmToSetNextCycle;

	@BeforeAll
	public void setupMachine() throws IOException
	{
		Am2900Loader.setup();
		mach = new StrictAm2900MachineDefinition().createNew();
		mpm = MicroInstructionMemoryParser.parseMemory(Am2900MicroInstructionMemoryDefinition.instance,
				TestGCD.class.getResourceAsStream("gcd.mpm"));
		ram = new WordAddressableMemory(Am2900MainMemoryDefinition.instance);
		mach.getMainMemory().bind(ram);

		if (startGUI)
			startGUI();

		machState = new AtomicInteger(2);
		halfCycleCount = new AtomicInteger();
		// needed for avoiding hazard loops in the first muIR
		mpmToSetNextCycle = new AtomicReference<>();
		mach.getClock().registerObserver(c ->
		{
			halfCycleCount.addAndGet(1);
			synchronized (machState)
			{
				long muPC = mach.getActiveMicroInstructionAddress();
				if (muPC == 0)
				{
					machState.set(0);
					machState.notify();
				} else if (muPC == 0x12)
				{
					machState.set(1);
					machState.notify();
				}
				if (!mach.getClock().isOn())
				{
					MicroInstructionMemory mpmToSet = mpmToSetNextCycle.getAndSet(null);
					if (mpmToSet != null)
						mach.getMicroInstructionMemory().bind(mpmToSet);
				}
			}
		});

		mach.reset();

		Thread execT = new Thread(mach.getTimeline()::executeAll, "Logic executer");
		execT.setDaemon(true);
		execT.start();

		System.out.println("Machine initialized");

	}

	private void startGUI()
	{
		new Thread(() -> new LogicUIStandaloneGUI(mach.getModel(), new DefaultRenderPreferences()).run(), "GUI thread").start();
	}

	@Test
	public void testGCDHardcodedCases() throws InterruptedException
	{
		checkGCD(4, 12);
		checkGCD(4, 13);
		checkGCD(0, 3);
		checkGCD(4, 0);
		checkGCD(0, 0);
		checkGCD(48820, 8480);
		checkGCD(21420, 11288);
		checkGCD(15862, 21219);
		checkGCD(15525, 57040);
	}

	@ParameterizedTest
	@MethodSource("generateRandomInts")
	public void testGCDRandomCases(int i) throws InterruptedException
	{
		int a = i & 0xFFFF;
		int b = (i >>> 16);
		checkGCD(a, b);
	}

	public static IntStream generateRandomInts()
	{
		return ThreadLocalRandom.current().ints(10);
	}

	private void checkGCD(int euclidA, int euclidB) throws InterruptedException
	{
		int exp = gcd(euclidA, euclidB);
		System.out.println("Checking gcd(" + euclidA + ", " + euclidB + "); expected " + exp);
		int act = executeGCD(euclidA, euclidB);
		assertEquals(exp, act);
	}

	private static int gcd(int a, int b)
	{
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

	private int executeGCD(int euclidA, int euclidB) throws InterruptedException
	{
		ram.setCell(0, BitVector.from(euclidA, 16));
		ram.setCell(1, BitVector.from(euclidB, 16));

		resetMachine();

		synchronized (machState)
		{
			while (machState.get() != 1)
				machState.wait();
		}

		BitVector result = mach.getRegister(NumberedRegister.instancesCorrectOrder.get(1));
		return result.isBinary() ? (int) result.getUnsignedValueLong() : -1;
	}

	private void resetMachine() throws InterruptedException
	{
		MicroInstructionDefinition muiDef = Am2900MicroInstructionDefinition.instance;
		ParameterClassification[] paramClassifications = muiDef.getParameterClassifications();
		MicroInstructionParameter[] defaultParams = muiDef.createDefaultInstruction().getParameters();
		defaultParams[19] = paramClassifications[19].parse("JZ");
		MicroInstruction jzMI = MicroInstruction.create(defaultParams);

		MicroInstructionMemory jzMPM = new StandardMicroInstructionMemory(Am2900MicroInstructionMemoryDefinition.instance);
		jzMPM.setCell(0x00, jzMI);
		jzMPM.setCell(0x13, jzMI);
		mpmToSetNextCycle.set(jzMPM);

		synchronized (machState)
		{
			while (machState.get() != 0)
				machState.wait();
		}

		mpmToSetNextCycle.set(mpm);
	}

	@AfterAll
	public void printStats()
	{
		System.out.println("Machine executed " + halfCycleCount.get() + " cycles in total (including JZs)");
	}
}