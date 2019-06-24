package net.mograsim.logic.core.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.LongConsumer;

import org.junit.jupiter.api.Test;

import net.mograsim.logic.core.components.Connector;
import net.mograsim.logic.core.components.Demux;
import net.mograsim.logic.core.components.Merger;
import net.mograsim.logic.core.components.Mux;
import net.mograsim.logic.core.components.Splitter;
import net.mograsim.logic.core.components.TriStateBuffer;
import net.mograsim.logic.core.components.gates.AndGate;
import net.mograsim.logic.core.components.gates.NandGate;
import net.mograsim.logic.core.components.gates.NorGate;
import net.mograsim.logic.core.components.gates.NotGate;
import net.mograsim.logic.core.components.gates.OrGate;
import net.mograsim.logic.core.components.gates.XorGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.core.wires.Wire.ReadWriteEnd;

@SuppressWarnings("unused")
class ComponentTest
{
	private Timeline t = new Timeline(11);

	@Test
	void circuitExampleTest()
	{
		Wire a = new Wire(t, 1, 1), b = new Wire(t, 1, 1), c = new Wire(t, 1, 10), d = new Wire(t, 2, 1), e = new Wire(t, 1, 1),
				f = new Wire(t, 1, 1), g = new Wire(t, 1, 1), h = new Wire(t, 2, 1), i = new Wire(t, 2, 1), j = new Wire(t, 1, 1),
				k = new Wire(t, 1, 1);
		new AndGate(t, 1, f.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		new NotGate(t, 1, f.createReadOnlyEnd(), g.createReadWriteEnd());
		new Merger(t, h.createReadWriteEnd(), c.createReadOnlyEnd(), g.createReadOnlyEnd());
		new Mux(t, 1, i.createReadWriteEnd(), e.createReadOnlyEnd(), h.createReadOnlyEnd(), d.createReadOnlyEnd());
		new Splitter(t, i.createReadOnlyEnd(), k.createReadWriteEnd(), j.createReadWriteEnd());

		a.createReadWriteEnd().feedSignals(Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ZERO);
		d.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE);
		e.createReadWriteEnd().feedSignals(Bit.ZERO);

		t.executeAll();

		assertEquals(Bit.ONE, j.getValue());
		assertEquals(Bit.ZERO, k.getValue());
	}

	@Test
	void splitterTest()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 2, 1), c = new Wire(t, 3, 1), in = new Wire(t, 8, 1);
		in.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		new Splitter(t, in.createReadOnlyEnd(), a.createReadWriteEnd(), b.createReadWriteEnd(), c.createReadWriteEnd());

		t.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void mergerTest()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 2, 1), c = new Wire(t, 3, 1), out = new Wire(t, 8, 1);
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		new Merger(t, out.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());

		t.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void fusionTest1()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 2, 1), c = new Wire(t, 3, 1), out = new Wire(t, 8, 1);
		Wire.fuse(a, out, 0, 0, a.length);
		Wire.fuse(b, out, 0, a.length, b.length);
		Wire.fuse(c, out, 0, a.length + b.length, c.length);
		ReadWriteEnd rA = a.createReadWriteEnd();
		rA.feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
		ReadWriteEnd rB = b.createReadWriteEnd();
		rB.feedSignals(Bit.ONE, Bit.ZERO);
		ReadWriteEnd rC = c.createReadWriteEnd();
		rC.feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();
		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		out.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		t.executeAll();
		assertBitArrayEquals(rA.getValues(), Bit.X, Bit.X, Bit.X);
		assertBitArrayEquals(rB.getValues(), Bit.X, Bit.X);
		assertBitArrayEquals(rC.getValues(), Bit.X, Bit.X, Bit.X);
		rA.clearSignals();
		rB.clearSignals();
		rC.clearSignals();
		t.executeAll();
		assertBitArrayEquals(rA.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE);
		assertBitArrayEquals(rB.getValues(), Bit.ZERO, Bit.ONE);
		assertBitArrayEquals(rC.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO);
	}

	@Test
	void fusionTest2()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 3, 1);
		Wire.fuse(a, b);
		ReadWriteEnd rw = a.createReadWriteEnd();
		t.executeAll();
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U);

		rw.feedSignals(Bit.ONE, Bit.U, Bit.Z);
		t.executeAll();
		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.U, Bit.Z);
	}

	@Test
	void fusionTest3()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 3, 1);
		a.createReadWriteEnd().feedSignals(Bit.Z, Bit.U, Bit.X);
		t.executeAll();
		Wire.fuse(a, b);
		t.executeAll();
		assertBitArrayEquals(b.getValues(), Bit.Z, Bit.U, Bit.X);
	}

//	@Test
//	void connectorTest()
//	{
//		t.reset();
//		Wire a = new Wire(t, 3, 1), b = new Wire(t, 3, 1);
//		new Connector(t, a.createReadWriteEnd(), b.createReadWriteEnd()).connect();
////		b.createReadWriteEnd();
//		a.createReadWriteEnd();
//		t.executeAll();
//		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U);
//	}

	@Test
	void triStateBufferTest()
	{
		Wire a = new Wire(t, 1, 1), b = new Wire(t, 1, 1), en = new Wire(t, 1, 1), notEn = new Wire(t, 1, 1);
		new NotGate(t, 1, en.createReadOnlyEnd(), notEn.createReadWriteEnd());
		new TriStateBuffer(t, 1, a.createReadOnlyEnd(), b.createReadWriteEnd(), en.createReadOnlyEnd());
		new TriStateBuffer(t, 1, b.createReadOnlyEnd(), a.createReadWriteEnd(), notEn.createReadOnlyEnd());

		ReadWriteEnd enI = en.createReadWriteEnd(), aI = a.createReadWriteEnd(), bI = b.createReadWriteEnd();
		enI.feedSignals(Bit.ONE);
		aI.feedSignals(Bit.ONE);
		bI.feedSignals(Bit.Z);

		t.executeAll();

		assertEquals(Bit.ONE, b.getValue());

		bI.feedSignals(Bit.ZERO);

		t.executeAll();

		assertEquals(Bit.X, b.getValue());
		assertEquals(Bit.ONE, a.getValue());

		aI.clearSignals();
		enI.feedSignals(Bit.ZERO);

		t.executeAll();

		assertEquals(Bit.ZERO, a.getValue());

	}

	@Test
	void muxTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 3), b = new Wire(t, 4, 6), c = new Wire(t, 4, 4), select = new Wire(t, 2, 5), out = new Wire(t, 4, 1);
		ReadWriteEnd selectIn = select.createReadWriteEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		c.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		new Mux(t, 1, out.createReadWriteEnd(), select.createReadOnlyEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(),
				c.createReadOnlyEnd());
		t.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		selectIn.feedSignals(Bit.ZERO, Bit.ONE);
		t.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		selectIn.feedSignals(Bit.ONE, Bit.ONE);
		t.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);

	}

	@Test
	void demuxTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 3), b = new Wire(t, 4, 6), c = new Wire(t, 4, 4), select = new Wire(t, 2, 5), in = new Wire(t, 4, 1);
		ReadWriteEnd selectIn = select.createReadWriteEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		in.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);

		new Demux(t, 1, in.createReadOnlyEnd(), select.createReadOnlyEnd(), a.createReadWriteEnd(), b.createReadWriteEnd(),
				c.createReadWriteEnd());
		t.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		selectIn.feedSignals(Bit.ZERO, Bit.ONE);
		t.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);

		selectIn.feedSignals(Bit.ONE, Bit.ONE);
		t.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);

	}

	@Test
	void andTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 1), b = new Wire(t, 4, 3), c = new Wire(t, 4, 1);
		new AndGate(t, 1, c.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void orTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 1), b = new Wire(t, 4, 3), c = new Wire(t, 4, 1);
		new OrGate(t, 1, c.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void nandTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 1), b = new Wire(t, 4, 3), c = new Wire(t, 4, 1), d = new Wire(t, 4, 1);
		new NandGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ONE);
	}

	@Test
	void norTest()
	{
		t.reset();
		Wire a = new Wire(t, 4, 1), b = new Wire(t, 4, 3), c = new Wire(t, 4, 1), d = new Wire(t, 4, 1);
		new NorGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ZERO, Bit.ZERO, Bit.ONE, Bit.ZERO);
	}

	@Test
	void xorTest()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 3, 2), c = new Wire(t, 3, 1), d = new Wire(t, 3, 1);
		new XorGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);
		b.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ZERO, Bit.ONE, Bit.ONE);
	}

	@Test
	void notTest()
	{
		t.reset();
		Wire a = new Wire(t, 3, 1), b = new Wire(t, 3, 2);
		new NotGate(t, 1, a.createReadOnlyEnd(), b.createReadWriteEnd());
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void rsLatchCircuitTest()
	{
		t.reset();
		Wire r = new Wire(t, 1, 1), s = new Wire(t, 1, 1), t1 = new Wire(t, 1, 15), t2 = new Wire(t, 1, 1), q = new Wire(t, 1, 1),
				nq = new Wire(t, 1, 1);

		new OrGate(t, 1, t2.createReadWriteEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd());
		new OrGate(t, 1, t1.createReadWriteEnd(), s.createReadOnlyEnd(), q.createReadOnlyEnd());
		new NotGate(t, 1, t2.createReadOnlyEnd(), q.createReadWriteEnd());
		new NotGate(t, 1, t1.createReadOnlyEnd(), nq.createReadWriteEnd());

		ReadWriteEnd sIn = s.createReadWriteEnd(), rIn = r.createReadWriteEnd();

		sIn.feedSignals(Bit.ONE);
		rIn.feedSignals(Bit.ZERO);

		t.executeAll();

		assertEquals(Bit.ONE, q.getValue());
		assertEquals(Bit.ZERO, nq.getValue());

		sIn.feedSignals(Bit.ZERO);

		t.executeAll();
		assertEquals(Bit.ONE, q.getValue());
		assertEquals(Bit.ZERO, nq.getValue());

		rIn.feedSignals(Bit.ONE);

		t.executeAll();

		assertEquals(Bit.ZERO, q.getValue());
		assertEquals(Bit.ONE, nq.getValue());
	}

	@Test
	void numericValueTest()
	{
		t.reset();

		Wire a = new Wire(t, 4, 1);
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE);

		t.executeAll();

		assertEquals(15, a.getUnsignedValue());
		assertEquals(-1, a.getSignedValue());
	}

	boolean flag = false;

	@Test
	void simpleTimelineTest()
	{
		Timeline t = new Timeline(3);
		flag = false;
		t.addEvent((e) ->
		{
			if (!flag)
				fail("Events executed out of order!");
			flag = false;
		}, 15);
		t.addEvent((e) ->
		{
			if (flag)
				fail("Events executed out of order!");
			flag = true;
		}, 10);
		t.addEvent((e) ->
		{
			if (flag)
				fail("Events executed out of order!");
			flag = true;
		}, 20);
		t.addEvent((e) ->
		{
			fail("Only supposed to execute until timestamp 20, not 25");
		}, 25);

		t.executeUntil(t.laterThan(20), 100);

		if (!flag)
			fail("Not all events were executed in order!");
	}

	@Test
	void multipleInputs()
	{
		t.reset();
		Wire w = new Wire(t, 2, 1);
		ReadWriteEnd wI1 = w.createReadWriteEnd(), wI2 = w.createReadWriteEnd();
		wI1.feedSignals(Bit.ONE, Bit.Z);
		wI2.feedSignals(Bit.Z, Bit.X);
		t.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.X);

		wI2.feedSignals(Bit.ZERO, Bit.Z);
		t.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.X, Bit.Z);

		wI2.feedSignals(Bit.Z, Bit.Z);
		t.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.Z);

		wI2.feedSignals(Bit.ONE, Bit.Z);
		ReadEnd rE = w.createReadOnlyEnd();
		rE.registerObserver((i) -> fail("WireEnd notified observer, although value did not change."));
		t.executeAll();
		rE.close();
		wI1.feedSignals(Bit.X, Bit.X);
		t.executeAll();
		wI1.registerObserver((i) -> fail("WireEnd notified observer, although it was closed."));
		wI1.close();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.Z);
	}

	@Test
	void wireConnections()
	{
		// Nur ein Experiment, was über mehrere 'passive' Bausteine hinweg passieren würde

		t.reset();

		Wire a = new Wire(t, 1, 2);
		Wire b = new Wire(t, 1, 2);
		Wire c = new Wire(t, 1, 2);
		ReadWriteEnd aI = a.createReadWriteEnd();
		ReadWriteEnd bI = b.createReadWriteEnd();
		ReadWriteEnd cI = c.createReadWriteEnd();

		TestBitDisplay test = new TestBitDisplay(t, c.createReadOnlyEnd());
		TestBitDisplay test2 = new TestBitDisplay(t, a.createReadOnlyEnd());
		LongConsumer print = time -> System.out.format("Time %2d\n a: %s\n b: %s\n c: %s\n", time, a, b, c);

		cI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);

		cI.feedSignals(Bit.X);
		test.assertAfterSimulationIs(print, Bit.X);

		cI.feedSignals(Bit.X);
		cI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);

		new Connector(t, b.createReadWriteEnd(), c.createReadWriteEnd()).connect();
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

		new Connector(t, a.createReadWriteEnd(), b.createReadWriteEnd()).connect();
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

	private static void assertBitArrayEquals(BitVector actual, Bit... expected)
	{
		assertArrayEquals(expected, actual.getBits());
	}
}
