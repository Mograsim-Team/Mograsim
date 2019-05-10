package era.mi.logic.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.function.LongConsumer;

import org.junit.jupiter.api.Test;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.components.Mux2;
import era.mi.logic.components.gates.AndGate;
import era.mi.logic.components.gates.NotGate;
import era.mi.logic.components.gates.OrGate;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

class ComponentTest
{

//	@Test
//	void circuitExampleTest()
//	{
//		Simulation.TIMELINE.reset();
//		WireArray a = new WireArray(1, 1), b = new WireArray(1, 1), c = new WireArray(1, 10), d = new WireArray(2, 1), e = new WireArray(1, 1),
//				f = new WireArray(1, 1), g = new WireArray(1, 1), h = new WireArray(2, 1), i = new WireArray(2, 1), j = new WireArray(1, 1), k = new WireArray(1, 1);
//		new AndGate(1, a, b, f);
//		new NotGate(1, f, g);
//		new Merger2(h, c, g);
//		new Mux(1, h, d, e, i);
//		new Splitter(i, k, j);
//		
//		a.createInput().feedSignals(Bit.ZERO);
//		b.createInput().feedSignals(Bit.ONE);
//		c.createInput().feedSignals(Bit.ZERO);
//		d.createInput().feedSignals(Bit.ONE, Bit.ONE);
//		e.createInput().feedSignals(Bit.ONE);
//		
//		while(Simulation.TIMELINE.hasNext())
//		{
//			Simulation.TIMELINE.executeNext();
//		}
//		
//		assertEquals(Simulation.TIMELINE.getSimulationTime(), 14);
//		assertEquals(Bit.ONE, j.getValue());
//		assertEquals(Bit.ZERO, k.getValue());
//	}
//
//	@Test
//	void splitterTest()
//	{
//		Simulation.TIMELINE.reset();
//		WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), in = new WireArray(8, 1);
//		in.createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO,Bit.ONE, Bit.ZERO, Bit.ONE);
//		new Splitter(in, a, b, c);
//		
//		while(Simulation.TIMELINE.hasNext())
//		{
//			Simulation.TIMELINE.executeNext();
//		}
//		
//		assertTrue(Arrays.equals(a.getValues(), new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO }));
//		assertTrue(Arrays.equals(b.getValues(), new Bit[] { Bit.ONE, Bit.ZERO }));
//		assertTrue(Arrays.equals(c.getValues(), new Bit[] { Bit.ONE, Bit.ZERO, Bit.ONE }));
//	}
//	
//	@Test
//	void mergerTest()
//	{
//		Simulation.TIMELINE.reset();
//		WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), out = new WireArray(8, 1);
//		a.createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
//		b.createInput().feedSignals(Bit.ONE, Bit.ZERO);
//		c.createInput().feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);
//		
//		new Merger2(out, a, b, c);
//		
//		while(Simulation.TIMELINE.hasNext())
//		{
//			Simulation.TIMELINE.executeNext();
//		}
//		
//		assertTrue(Arrays.equals(out.getValues(), new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE }));
//	}
	
	@Test
	void muxTest()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(1, 3), b = new WireArray(1, 2), select = new WireArray(1, 1), out = new WireArray(1, 1);
		WireArrayInput selectIn = select.createInput();
		
		selectIn.feedSignals(Bit.ZERO);
		a.createInput().feedSignals(Bit.ONE);
		b.createInput().feedSignals(Bit.ZERO);
		
		new Mux2(1, out, select, a, b);
		assertEquals(Bit.Z, out.getValue());
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}

		assertEquals(Bit.ONE, out.getValue());
		selectIn.feedSignals(Bit.ONE);
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(out.getValue(), Bit.ZERO);
	}

	@Test
	void andTest()
	{
		Simulation.TIMELINE.reset();
		AndGate gate = new AndGate(1, new WireArray(4, 1), new WireArray(4, 1), new WireArray(4, 1));
		gate.getA().createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		gate.getB().createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		assertTrue(Arrays.equals(gate.getOut().getValues(), new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ZERO }));
	}
	
	@Test
	void orTest()
	{
		Simulation.TIMELINE.reset();
		OrGate gate = new OrGate(1, new WireArray(4, 1), new WireArray(4, 1), new WireArray(4, 1));
		gate.getA().createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		gate.getB().createInput().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertTrue(Arrays.equals(gate.getOut().getValues(), new Bit[] { Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ONE }));
	}
	
	@Test
	void rsLatchCircuitTest()
	{
		Simulation.TIMELINE.reset();
		WireArray r = new WireArray(1, 1), s = new WireArray(1, 1), t1 = new WireArray(1, 15), t2 = new WireArray(1, 1), q = new WireArray(1, 1),
				nq = new WireArray(1, 1);
		
		new OrGate(1, r, nq, t2);
		new OrGate(1, s, q, t1);
		new NotGate(1, t2, q);
		new NotGate(1, t1, nq);
	
		WireArrayInput sIn = s.createInput(), rIn = r.createInput();
		
		sIn.feedSignals(Bit.ONE);
		rIn.feedSignals(Bit.ZERO);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(q.getValue(), Bit.ONE);
		assertEquals(nq.getValue(), Bit.ZERO);
		
		sIn.feedSignals(Bit.ZERO);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(q.getValue(), Bit.ONE);
		assertEquals(nq.getValue(), Bit.ZERO);
		
		rIn.feedSignals(Bit.ONE);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(q.getValue(), Bit.ZERO);
		assertEquals(nq.getValue(), Bit.ONE);
	}
	
	@Test
	void numericValueTest()
	{
		Simulation.TIMELINE.reset();
		
		WireArray a = new WireArray(4, 1);
		a.createInput().feedSignals(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(a.getUnsignedValue(), 15);
		assertEquals(a.getSignedValue(), -1);
	}
	
	@Test
	void multipleInputs()
	{
		Simulation.TIMELINE.reset();
		WireArray w = new WireArray(2, 1);
		WireArrayInput wI1 = w.createInput(), wI2 = w.createInput();
		wI1.feedSignals(Bit.ONE, Bit.Z);
		wI2.feedSignals(Bit.Z, Bit.X);
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		assertTrue(Arrays.equals(w.getValues(), new Bit[] { Bit.ONE, Bit.X }));
		
		wI2.feedSignals(Bit.ZERO, Bit.Z);
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		assertTrue(Arrays.equals(w.getValues(), new Bit[] { Bit.X, Bit.Z }));
		
		wI2.feedSignals(Bit.Z, Bit.Z);
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		assertTrue(Arrays.equals(w.getValues(), new Bit[] { Bit.ONE, Bit.Z }));
		
		wI2.feedSignals(Bit.ONE, Bit.Z);
		w.addObserver((i) -> fail("WireArray notified observer, although value did not change."));
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		assertTrue(Arrays.equals(w.getValues(), new Bit[] { Bit.ONE, Bit.Z }));
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
		LongConsumer print = time -> System.out.format("Time %2d\n   %s\n   %s\n   %s\n", time, a, b, c);

		cI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);

		cI.feedSignals(Bit.X);
		test.assertAfterSimulationIs(print, Bit.X);

		cI.feedSignals(Bit.X);
		cI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(print, Bit.Z);

		Connector c1 = new Connector(b, c);
		test.assertAfterSimulationIs(print, Bit.Z);
		System.out.println("ONE");
		bI.feedSignals(Bit.ONE);
		test.assertAfterSimulationIs(print, Bit.ONE);
		System.out.println("ZERO");
		bI.feedSignals(Bit.ZERO);
		test.assertAfterSimulationIs(print, Bit.ZERO);
		System.out.println("Z");
		bI.feedSignals(Bit.Z);
		test.assertAfterSimulationIs(Bit.Z);
	}

	private static void assertBitArrayEquals(Bit[] actual, Bit... expected)
	{
		assertArrayEquals(expected, actual);
	}
}
