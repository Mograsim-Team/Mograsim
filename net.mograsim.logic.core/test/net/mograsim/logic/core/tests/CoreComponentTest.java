package net.mograsim.logic.core.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.mograsim.logic.core.components.CoreDemux;
import net.mograsim.logic.core.components.CoreMux;
import net.mograsim.logic.core.components.CoreTriStateBuffer;
import net.mograsim.logic.core.components.CoreUnidirectionalMerger;
import net.mograsim.logic.core.components.CoreUnidirectionalSplitter;
import net.mograsim.logic.core.components.gates.CoreAndGate;
import net.mograsim.logic.core.components.gates.CoreNandGate;
import net.mograsim.logic.core.components.gates.CoreNorGate;
import net.mograsim.logic.core.components.gates.CoreNotGate;
import net.mograsim.logic.core.components.gates.CoreOrGate;
import net.mograsim.logic.core.components.gates.CoreXorGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadEnd;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;

@SuppressWarnings("unused")
class CoreComponentTest
{
	private Timeline t = new Timeline(11);

	@BeforeEach
	void resetTimeline()
	{
		t.reset();
	}

	@Test
	void circuitExampleTest()
	{
		CoreWire a = new CoreWire(t, 1, 1), b = new CoreWire(t, 1, 1), c = new CoreWire(t, 1, 10), d = new CoreWire(t, 2, 1), e = new CoreWire(t, 1, 1),
				f = new CoreWire(t, 1, 1), g = new CoreWire(t, 1, 1), h = new CoreWire(t, 2, 1), i = new CoreWire(t, 2, 1), j = new CoreWire(t, 1, 1),
				k = new CoreWire(t, 1, 1);
		new CoreAndGate(t, 1, f.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		new CoreNotGate(t, 1, f.createReadOnlyEnd(), g.createReadWriteEnd());
		new CoreUnidirectionalMerger(t, h.createReadWriteEnd(), c.createReadOnlyEnd(), g.createReadOnlyEnd());
		new CoreMux(t, 1, i.createReadWriteEnd(), e.createReadOnlyEnd(), h.createReadOnlyEnd(), d.createReadOnlyEnd());
		new CoreUnidirectionalSplitter(t, i.createReadOnlyEnd(), k.createReadWriteEnd(), j.createReadWriteEnd());

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
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 2, 1), c = new CoreWire(t, 3, 1), in = new CoreWire(t, 8, 1);
		in.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		new CoreUnidirectionalSplitter(t, in.createReadOnlyEnd(), a.createReadWriteEnd(), b.createReadWriteEnd(), c.createReadWriteEnd());

		t.executeAll();

		assertBitArrayEquals(a.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO);
		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void mergerTest()
	{
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 2, 1), c = new CoreWire(t, 3, 1), out = new CoreWire(t, 8, 1);
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		new CoreUnidirectionalMerger(t, out.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());

		t.executeAll();

		assertBitArrayEquals(out.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void fusionTest1()
	{
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 2, 1), c = new CoreWire(t, 3, 1), out = new CoreWire(t, 8, 1);
		CoreWire.fuse(a, out, 0, 0, a.width);
		CoreWire.fuse(b, out, 0, a.width, b.width);
		CoreWire.fuse(c, out, 0, a.width + b.width, c.width);
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
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 3, 1);
		CoreWire.fuse(a, b);
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
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 3, 1);
		a.createReadWriteEnd().feedSignals(Bit.Z, Bit.U, Bit.X);
		t.executeAll();
		CoreWire.fuse(a, b);
		t.executeAll();
		assertBitArrayEquals(b.getValues(), Bit.Z, Bit.U, Bit.X);
	}

	@Test
	void fusionTest4()
	{
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 3, 1);
		a.createReadWriteEnd();
		t.executeAll();

		CoreWire.fuse(a, b);
		t.executeAll();
		assertBitArrayEquals(b.getValues(), Bit.U, Bit.U, Bit.U);
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
		CoreWire a = new CoreWire(t, 1, 1), b = new CoreWire(t, 1, 1), en = new CoreWire(t, 1, 1), notEn = new CoreWire(t, 1, 1);
		new CoreNotGate(t, 1, en.createReadOnlyEnd(), notEn.createReadWriteEnd());
		new CoreTriStateBuffer(t, 1, a.createReadOnlyEnd(), b.createReadWriteEnd(), en.createReadOnlyEnd());
		new CoreTriStateBuffer(t, 1, b.createReadOnlyEnd(), a.createReadWriteEnd(), notEn.createReadOnlyEnd());

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
		CoreWire a = new CoreWire(t, 4, 3), b = new CoreWire(t, 4, 6), c = new CoreWire(t, 4, 4), select = new CoreWire(t, 2, 5), out = new CoreWire(t, 4, 1);
		ReadWriteEnd selectIn = select.createReadWriteEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);
		c.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		new CoreMux(t, 1, out.createReadWriteEnd(), select.createReadOnlyEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(),
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
		CoreWire a = new CoreWire(t, 4, 3), b = new CoreWire(t, 4, 6), c = new CoreWire(t, 4, 4), select = new CoreWire(t, 2, 5), in = new CoreWire(t, 4, 1);
		ReadWriteEnd selectIn = select.createReadWriteEnd();

		selectIn.feedSignals(Bit.ZERO, Bit.ZERO);
		in.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO);

		new CoreDemux(t, 1, in.createReadOnlyEnd(), select.createReadOnlyEnd(), a.createReadWriteEnd(), b.createReadWriteEnd(),
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
		CoreWire a = new CoreWire(t, 4, 1), b = new CoreWire(t, 4, 3), c = new CoreWire(t, 4, 1);
		new CoreAndGate(t, 1, c.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void orTest()
	{
		CoreWire a = new CoreWire(t, 4, 1), b = new CoreWire(t, 4, 3), c = new CoreWire(t, 4, 1);
		new CoreOrGate(t, 1, c.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(c.getValues(), Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE);
	}

	@Test
	void nandTest()
	{
		CoreWire a = new CoreWire(t, 4, 1), b = new CoreWire(t, 4, 3), c = new CoreWire(t, 4, 1), d = new CoreWire(t, 4, 1);
		new CoreNandGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ONE);
	}

	@Test
	void norTest()
	{
		CoreWire a = new CoreWire(t, 4, 1), b = new CoreWire(t, 4, 3), c = new CoreWire(t, 4, 1), d = new CoreWire(t, 4, 1);
		new CoreNorGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		b.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ZERO, Bit.ZERO, Bit.ONE, Bit.ZERO);
	}

	@Test
	void xorTest()
	{
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 3, 2), c = new CoreWire(t, 3, 1), d = new CoreWire(t, 3, 1);
		new CoreXorGate(t, 1, d.createReadWriteEnd(), a.createReadOnlyEnd(), b.createReadOnlyEnd(), c.createReadOnlyEnd());
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);
		b.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);
		c.createReadWriteEnd().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(d.getValues(), Bit.ZERO, Bit.ONE, Bit.ONE);
	}

	@Test
	void notTest()
	{
		CoreWire a = new CoreWire(t, 3, 1), b = new CoreWire(t, 3, 2);
		new CoreNotGate(t, 1, a.createReadOnlyEnd(), b.createReadWriteEnd());
		a.createReadWriteEnd().feedSignals(Bit.ZERO, Bit.ONE, Bit.ONE);

		t.executeAll();

		assertBitArrayEquals(b.getValues(), Bit.ONE, Bit.ZERO, Bit.ZERO);
	}

	@Test
	void rsLatchCircuitTest()
	{
		CoreWire r = new CoreWire(t, 1, 1), s = new CoreWire(t, 1, 1), t1 = new CoreWire(t, 1, 15), t2 = new CoreWire(t, 1, 1), q = new CoreWire(t, 1, 1),
				nq = new CoreWire(t, 1, 1);

		new CoreOrGate(t, 1, t2.createReadWriteEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd());
		new CoreOrGate(t, 1, t1.createReadWriteEnd(), s.createReadOnlyEnd(), q.createReadOnlyEnd());
		new CoreNotGate(t, 1, t2.createReadOnlyEnd(), q.createReadWriteEnd());
		new CoreNotGate(t, 1, t1.createReadOnlyEnd(), nq.createReadWriteEnd());

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
		CoreWire a = new CoreWire(t, 4, 1);
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

	// TODO: Adapt this test, now that update notifications are issued whenever any input to a wire changes
	@Disabled("Out of date")
	@Test
	void multipleInputs()
	{
		CoreWire w = new CoreWire(t, 2, 1);
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

	private static void assertBitArrayEquals(BitVector actual, Bit... expected)
	{
		assertArrayEquals(expected, actual.getBits());
	}
}
