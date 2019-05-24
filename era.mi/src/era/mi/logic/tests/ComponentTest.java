package era.mi.logic.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.LongConsumer;

import org.junit.jupiter.api.Test;

import era.mi.logic.Simulation;
import era.mi.logic.components.Connector;
import era.mi.logic.components.Demux;
import era.mi.logic.components.Merger;
import era.mi.logic.components.Mux;
import era.mi.logic.components.Splitter;
import era.mi.logic.components.TriStateBuffer;
import era.mi.logic.components.gates.AndGate;
import era.mi.logic.components.gates.NotGate;
import era.mi.logic.components.gates.OrGate;
import era.mi.logic.components.gates.XorGate;
import era.mi.logic.types.Bit;
import era.mi.logic.types.BitVector;
import era.mi.logic.wires.Wire;
import era.mi.logic.wires.Wire.ReadEnd;
import era.mi.logic.wires.Wire.ReadWriteEnd;

class ComponentTest
{

	@Test
	void circuitExampleTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(1, 1), b = new Wire(1, 1), c = new Wire(1, 10), d = new Wire(2, 1), e = new Wire(1, 1), f = new Wire(1, 1),
				g = new Wire(1, 1), h = new Wire(2, 1), i = new Wire(2, 1), j = new Wire(1, 1), k = new Wire(1, 1);
		new AndGate(1, f.createEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		new NotGate(1, f.createReadOnlyEnd(), g.createEnd());
		new Merger(h.createEnd(), c.createReadOnlyEnd(), g.createReadOnlyEnd());
		new Mux(1, i.createEnd(), e.createReadOnlyEnd(), h.createReadOnlyEnd(), d.createReadOnlyEnd());
		new Splitter(i.createReadOnlyEnd(), k.createEnd(), j.createEnd());

		a.createEnd().feedSignals(Bit.ZERO);
		b.createEnd().feedSignals(Bit.ONE);
		c.createEnd().feedSignals(Bit.ZERO);
		d.createEnd().feedSignals(Bit.ONE, Bit.ONE);
		e.createEnd().feedSignals(Bit.ZERO);

		Simulation.TIMELINE.executeAll();

		assertEquals(Bit.ONE, j.getValue());
		assertEquals(Bit.ZERO, k.getValue());
	}

	@Test
	void splitterTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(3, 1), b = new Wire(2, 1), c = new Wire(3, 1), in = new Wire(8, 1);
		in.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		new Splitter(in.createReadOnlyEnd(), a.createEnd(), b.createEnd(), c.createEnd());

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void mergerTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(3, 1), b = new Wire(2, 1), c = new Wire(3, 1), out = new Wire(8, 1);
		a.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
		b.createEnd().feedSignals(Bit.ONE, Bit.ZERO);
		c.createEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		new Merger(out.createEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void triStateBufferTest()
	{
		Wire a = new Wire(1, 1), b = new Wire(1, 1), en = new Wire(1, 1), notEn = new Wire(1, 1);
		new NotGate(1, en.createReadOnlyEnd(), notEn.createEnd());
		new TriStateBuffer(1, a.createReadOnlyEnd(), b.createEnd(), en.createReadOnlyEnd());
		new TriStateBuffer(1, b.createReadOnlyEnd(), a.createEnd(), notEn.createReadOnlyEnd());

		ReadWriteEnd enI = en.createEnd(), aI = a.createEnd(), bI = b.createEnd();
		enI.feedSignals(Bit.ONE);
		aI.feedSignals(Bit.ONE);
		bI.feedSignals(Bit.Z);

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
		Wire a = new Wire(4, 3), b = new Wire(4, 6), c = new Wire(4, 4), select = new Wire(2, 5), out = new Wire(4, 1);
		ReadWriteEnd selectIn = select.createEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		a.createEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		c.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		new Mux(1, out.createEnd(), select.createReadOnlyEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		selectIn.feedSignals(Bit.ZERO, Bit.ONE);
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		selectIn.feedSignals(Bit.ONE, Bit.ONE);
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);

	}

	@Test
	void demuxTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(4, 3), b = new Wire(4, 6), c = new Wire(4, 4), select = new Wire(2, 5), in = new Wire(4, 1);
		ReadWriteEnd selectIn = select.createEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		in.createEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);

		new Demux(1, in.createReadOnlyEnd(), select.createReadOnlyEnd(), a.createEnd(), b.createEnd(), c.createEnd());
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		selectIn.feedSignals(Bit.ZERO, Bit.ONE);
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);

		selectIn.feedSignals(Bit.ONE, Bit.ONE);
		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U, Bit.U);
		assertBitArrayEquals(c.getValues(), Bit.Z, Bit.Z, Bit.Z, Bit.Z);

	}

	@Test
	void andTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(4, 1), b = new Wire(4, 3), c = new Wire(4, 1);
		new AndGate(1, c.createEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void orTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(4, 1), b = new Wire(4, 3), c = new Wire(4, 1);
		new OrGate(1, c.createEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void xorTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(3, 1), b = new Wire(3, 2), c = new Wire(3, 1), d = new Wire(3, 1);
		new XorGate(1, d.createEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);
		b.createEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ZERO, Bit.ONE, Bit.ONE);
	}

	@Test
	void notTest()
	{
		Simulation.TIMELINE.reset();
		Wire a = new Wire(3, 1), b = new Wire(3, 2);
		new NotGate(1, a.createReadOnlyEnd(), b.createEnd());
		a.createEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);

		Simulation.TIMELINE.executeAll();

		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void rsLatchCircuitTest()
	{
		Simulation.TIMELINE.reset();
		Wire r = new Wire(1, 1), s = new Wire(1, 1), t1 = new Wire(1, 15), t2 = new Wire(1, 1), q = new Wire(1, 1), nq = new Wire(1, 1);

		new OrGate(1, t2.createEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd());
		new OrGate(1, t1.createEnd(), s.createReadOnlyEnd(), q.createReadOnlyEnd());
		new NotGate(1, t2.createReadOnlyEnd(), q.createEnd());
		new NotGate(1, t1.createReadOnlyEnd(), nq.createEnd());

		ReadWriteEnd sIn = s.createEnd(), rIn = r.createEnd();

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

		Wire a = new Wire(4, 1);
		a.createEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE);

		Simulation.TIMELINE.executeAll();

		assertEquals(15, a.getUnsignedValue());
		assertEquals(-1, a.getSignedValue());
	}

	@Test
	void multipleInputs()
	{
		Simulation.TIMELINE.reset();
		Wire w = new Wire(2, 1);
		ReadWriteEnd wI1 = w.createEnd(), wI2 = w.createEnd();
		wI1.feedSignals(Bit.ONE, Bit.Z);
		wI2.feedSignals(Bit.Z, Bit.X);
		Simulation.TIMELINE.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.X);

		wI2.feedSignals(Bit.ZERO, Bit.Z);
		Simulation.TIMELINE.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.X, Bit.Z);

		wI2.feedSignals(Bit.Z, Bit.Z);
		Simulation.TIMELINE.executeAll();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.Z);

		wI2.feedSignals(Bit.ONE, Bit.Z);
		ReadEnd rE = w.createReadOnlyEnd();
		rE.addObserver((i, oldValues) -> fail("WireEnd notified observer, although value did not change."));
		Simulation.TIMELINE.executeAll();
		rE.close();
		wI1.feedSignals(Bit.X, Bit.X);
		Simulation.TIMELINE.executeAll();
		wI1.addObserver((i, oldValues) -> fail("WireEnd notified observer, although it was closed."));
		wI1.close();
		assertBitArrayEquals(w.getValues(), Bit.ONE, Bit.Z);
	}

	@Test
	void wireConnections()
	{
		// Nur ein Experiment, was über mehrere 'passive' Bausteine hinweg passieren würde

		Simulation.TIMELINE.reset();

		Wire a = new Wire(1, 2);
		Wire b = new Wire(1, 2);
		Wire c = new Wire(1, 2);
		ReadWriteEnd aI = a.createEnd();
		ReadWriteEnd bI = b.createEnd();
		ReadWriteEnd cI = c.createEnd();

		TestBitDisplay test = new TestBitDisplay(c.createReadOnlyEnd());
		TestBitDisplay test2 = new TestBitDisplay(a.createReadOnlyEnd());
		LongConsumer print = time -> System.out.format("Time %2d\n   a: %s\n   b: %s\n   c: %s\n", time, a, b, c);

		cI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);

		cI.feedSignals(Bit.X);
		test.assertAfterSimulationIs(print, Bit.X);

		cI.feedSignals(Bit.X);
		cI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);

		new Connector(b.createEnd(), c.createEnd()).connect();
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

		new Connector(a.createEnd(), b.createEnd()).connect();
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
