package era.mi.logic.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.function.LongConsumer;

import org.junit.jupiter.api.Test;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.components.Merger;
import era.mi.logic.components.Mux;
import era.mi.logic.components.Splitter;
import era.mi.logic.components.TriStateBuffer;
import era.mi.logic.components.gates.AndGate;
import era.mi.logic.components.gates.NotGate;
import era.mi.logic.components.gates.OrGate;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

class ComponentTest
{
    
	@Test
	void circuitExampleTest()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(1, 1), b = new WireArray(1, 1), c = new WireArray(1, 10), d = new WireArray(2, 1), e = new WireArray(1, 1),
				f = new WireArray(1, 1), g = new WireArray(1, 1), h = new WireArray(2, 1), i = new WireArray(2, 1), j = new WireArray(1, 1), k = new WireArray(1, 1);
		new AndGate(1, a, b, f);
		new NotGate(1, f, g);
		new Merger(h, c, g);
		new Mux(1, i, e, h, d);
		new Splitter(i, k, j);
		
		a.createInput().feedSignals(Bit.ZERO);
		b.createInput().feedSignals(Bit.ONE);
		c.createInput().feedSignals(Bit.ZERO);
		d.createInput().feedSignals(Bit.ONE, Bit.ONE);
		e.createInput().feedSignals(Bit.ZERO);
		
		Simulation.TIMELINE.executeAll();
		
		assertEquals(Bit.ONE, j.getValue());
		assertEquals(Bit.ZERO, k.getValue());
	}

    @Test
    void splitterTest()
    {
	Simulation.TIMELINE.reset();
	WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), in = new WireArray(8, 1);
	in.createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
	new Splitter(in, a, b, c);

	Simulation.TIMELINE.executeAll();

	assertArrayEquals(new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO }, a.getValues());
	assertArrayEquals(new Bit[] { Bit.ONE, Bit.ZERO }, b.getValues());
	assertArrayEquals(new Bit[] { Bit.ONE, Bit.ZERO, Bit.ONE }, c.getValues());
    }

    @Test
    void mergerTest()
    {
	Simulation.TIMELINE.reset();
	WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), out = new WireArray(8, 1);
	a.createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
	b.createInput().feedSignals(Bit.ONE, Bit.ZERO);
	c.createInput().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

	new Merger(out, a, b, c);

	Simulation.TIMELINE.executeAll();

	assertTrue(Arrays.equals(out.getValues(),
		new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE }));
    }

    @Test
    void triStateBufferTest()
    {
	WireArray a = new WireArray(1, 1), b = new WireArray(1, 1), en = new WireArray(1, 1),
		notEn = new WireArray(1, 1);
	new NotGate(1, en, notEn);
	new TriStateBuffer(1, a, b, en);
	new TriStateBuffer(1, b, a, notEn);

	WireArrayInput enI = en.createInput(), aI = a.createInput(), bI = b.createInput();
	enI.feedSignals(Bit.ONE);
	aI.feedSignals(Bit.ONE);

	Simulation.TIMELINE.executeAll();

	assertEquals(Bit.ONE, b.getValue());

	bI.feedSignals(Bit.ZERO);

	Simulation.TIMELINE.executeAll();

	assertEquals(Bit.X, b.getValue());
	assertEquals(Bit.ONE, a.getValue());

	aI.clearSignals();
	enI.feedSignals(Bit.ZERO);

	Simulation.TIMELINE.executeAll();

	assertEquals(Bit.ZERO, a.getValue());

    }

    @Test
    void muxTest()
    {
	Simulation.TIMELINE.reset();
	WireArray a = new WireArray(4, 3), b = new WireArray(4, 6), c = new WireArray(4, 4),
		select = new WireArray(2, 5), out = new WireArray(4, 1);
	WireArrayInput selectIn = select.createInput();

	selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
	a.createInput().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
	c.createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

	new Mux(1, out, select, a, b, c);
	Simulation.TIMELINE.executeAll();

	assertArrayEquals(new Bit[] { Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO }, out.getValues());
	selectIn.feedSignals(Bit.ZERO, Bit.ONE);
	Simulation.TIMELINE.executeAll();

	assertArrayEquals(new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE }, out.getValues());

	selectIn.feedSignals(Bit.ONE, Bit.ONE);
	Simulation.TIMELINE.executeAll();

	assertArrayEquals(new Bit[] { Bit.Z, Bit.Z, Bit.Z, Bit.Z }, out.getValues());

    }

    @Test
    void andTest()
    {
	Simulation.TIMELINE.reset();
	AndGate gate = new AndGate(1, new WireArray(4, 1), new WireArray(4, 1), new WireArray(4, 1));
	gate.getA().createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
	gate.getB().createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

	Simulation.TIMELINE.executeAll();
	assertArrayEquals(new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ZERO }, gate.getOut().getValues());
    }

    @Test
    void orTest()
    {
	Simulation.TIMELINE.reset();
	OrGate gate = new OrGate(1, new WireArray(4, 1), new WireArray(4, 1), new WireArray(4, 1));
	gate.getA().createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
	gate.getB().createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

	Simulation.TIMELINE.executeAll();

	assertArrayEquals(new Bit[] { Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE }, gate.getOut().getValues());
    }

    @Test
    void rsLatchCircuitTest()
    {
	Simulation.TIMELINE.reset();
	WireArray r = new WireArray(1, 1), s = new WireArray(1, 1), t1 = new WireArray(1, 15), t2 = new WireArray(1, 1),
		q = new WireArray(1, 1), nq = new WireArray(1, 1);

	new OrGate(1, r, nq, t2);
	new OrGate(1, s, q, t1);
	new NotGate(1, t2, q);
	new NotGate(1, t1, nq);

	WireArrayInput sIn = s.createInput(), rIn = r.createInput();

	sIn.feedSignals(Bit.ONE);
	rIn.feedSignals(Bit.ZERO);

	Simulation.TIMELINE.executeAll();

	assertEquals(Bit.ONE, q.getValue());
	assertEquals(Bit.ZERO, nq.getValue());

	sIn.feedSignals(Bit.ZERO);

	Simulation.TIMELINE.executeAll();
	assertEquals(Bit.ONE, q.getValue());
	assertEquals(Bit.ZERO, nq.getValue());

	rIn.feedSignals(Bit.ONE);

	Simulation.TIMELINE.executeAll();

	assertEquals(Bit.ZERO, q.getValue());
	assertEquals(Bit.ONE, nq.getValue());
    }

    @Test
    void numericValueTest()
    {
	Simulation.TIMELINE.reset();

	WireArray a = new WireArray(4, 1);
	a.createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE);

	Simulation.TIMELINE.executeAll();

	assertEquals(15, a.getUnsignedValue());
	assertEquals(-1, a.getSignedValue());
    }

    @Test
    void multipleInputs()
    {
	Simulation.TIMELINE.reset();
	WireArray w = new WireArray(2, 1);
	WireArrayInput wI1 = w.createInput(), wI2 = w.createInput();
	wI1.feedSignals(Bit.ONE, Bit.Z);
	wI2.feedSignals(Bit.Z, Bit.X);
	Simulation.TIMELINE.executeAll();
	assertArrayEquals(new Bit[] { Bit.ONE, Bit.X }, w.getValues());

	wI2.feedSignals(Bit.ZERO, Bit.Z);
	Simulation.TIMELINE.executeAll();
	assertArrayEquals(new Bit[] { Bit.X, Bit.Z }, w.getValues());

	wI2.feedSignals(Bit.Z, Bit.Z);
	Simulation.TIMELINE.executeAll();
	assertArrayEquals(new Bit[] { Bit.ONE, Bit.Z }, w.getValues());

	wI2.feedSignals(Bit.ONE, Bit.Z);
	w.addObserver((i) -> fail("WireArray notified observer, although value did not change."));
	Simulation.TIMELINE.executeAll();
	assertArrayEquals(new Bit[] { Bit.ONE, Bit.Z }, w.getValues());
    }

	@Test
	void wireConnections()
	{
		// Nur ein Experiment, was über mehrere 'passive' Bausteine hinweg passieren würde
		
		Simulation.TIMELINE.reset();

		WireArray a = new WireArray(1, 2);
		WireArray b = new WireArray(1, 2);
		WireArray c = new WireArray(1, 2);
		WireArrayInput aI = a.createInput();
		WireArrayInput bI = b.createInput();
		WireArrayInput cI = c.createInput();

		TestBitDisplay test = new TestBitDisplay(c);
		TestBitDisplay test2 = new TestBitDisplay(a);
		LongConsumer print = time -> System.out.format("Time %2d\n   a: %s\n   b: %s\n   c: %s\n", time, a, b, c);

		cI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);

		cI.feedSignals(Bit.X);
		test.assertAfterSimulationIs(print, Bit.X);

		cI.feedSignals(Bit.X);
		cI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);

		Connector c1 = new Connector(b, c);
		test.assertAfterSimulationIs(print, Bit.Z);
		System.err.println("ONE");
		bI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		System.err.println("ZERO");
		bI.feedSignals(Bit.ZERO);
		test.assertAfterSimulationIs(print, Bit.ZERO);
		System.err.println("Z");
		bI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);
		
		Connector c2 = new Connector(a, b);
		System.err.println("Z 2");
		aI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);
		test2.assertAfterSimulationIs(Bit.Z);
		System.err.println("ONE 2");
		aI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		test2.assertAfterSimulationIs(Bit.ONE);
		System.err.println("ZERO 2");
		aI.feedSignals(Bit.ZERO);
		test.assertAfterSimulationIs(print, Bit.ZERO);
		test2.assertAfterSimulationIs(Bit.ZERO);
		System.err.println("Z 2 II");
		aI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);
		test2.assertAfterSimulationIs(Bit.Z);
		
		System.err.println("No Conflict yet");
		bI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		test2.assertAfterSimulationIs(Bit.ONE);
		aI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		test2.assertAfterSimulationIs(Bit.ONE);
		System.err.println("Conflict");
		aI.feedSignals(Bit.ZERO);
		test.assertAfterSimulationIs(print, Bit.X);
		test2.assertAfterSimulationIs(Bit.X);
		aI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		test2.assertAfterSimulationIs(Bit.ONE);
	}

    private static void assertBitArrayEquals(Bit[] actual, Bit... expected)
    {
	assertArrayEquals(expected, actual);
    }
}
