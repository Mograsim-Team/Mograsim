package era.mi.logic.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.WireArray;
import era.mi.logic.components.Merger2;
import era.mi.logic.components.Mux;
import era.mi.logic.components.Splitter;
import era.mi.logic.components.gates.AndGate;
import era.mi.logic.components.gates.NotGate;
import era.mi.logic.components.gates.OrGate;

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
		new Merger2(h, c, g);
		new Mux(1, h, d, e, i);
		new Splitter(i, k, j);
		
		a.feedSignals(Bit.ZERO);
		b.feedSignals(Bit.ONE);
		c.feedSignals(Bit.ZERO);
		d.feedSignals(Bit.ONE, Bit.ONE);
		e.feedSignals(Bit.ONE);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(Simulation.TIMELINE.getSimulationTime(), 14);
		assertEquals(Bit.ONE, j.getValue());
		assertEquals(Bit.ZERO, k.getValue());
	}

	@Test
	void splitterTest()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), in = new WireArray(8, 1);
		in.feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO,Bit.ONE, Bit.ZERO, Bit.ONE);
		new Splitter(in, a, b, c);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertTrue(Arrays.equals(a.getValues(), new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO }));
		assertTrue(Arrays.equals(b.getValues(), new Bit[] { Bit.ONE, Bit.ZERO }));
		assertTrue(Arrays.equals(c.getValues(), new Bit[] { Bit.ONE, Bit.ZERO, Bit.ONE }));
	}
	
	@Test
	void mergerTest()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(3, 1), b = new WireArray(2, 1), c = new WireArray(3, 1), out = new WireArray(8, 1);
		a.feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO);
		b.feedSignals(Bit.ONE, Bit.ZERO);
		c.feedSignals(Bit.ONE, Bit.ZERO, Bit.ONE);
		
		new Merger2(out, a, b, c);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertTrue(Arrays.equals(out.getValues(), new Bit[] { Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE }));
	}
	
	@Test
	void muxTest()
	{
		Simulation.TIMELINE.reset();
		WireArray a = new WireArray(1, 1), b = new WireArray(1, 1), select = new WireArray(1, 1), out = new WireArray(1, 1);
		
		select.feedSignals(Bit.ONE);
		a.feedSignals(Bit.ONE);
		b.feedSignals(Bit.ZERO);
		
		new Mux(1, a, b, select, out);
		assertEquals(out.getValue(), Bit.X);
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}

		assertEquals(out.getValue(), Bit.ONE);
		select.feedSignals(Bit.ZERO);
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
		gate.getA().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		gate.getB().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		
		
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
		gate.getA().feedSignals(Bit.ONE, Bit.ONE, Bit.ZERO, Bit.ZERO);
		gate.getB().feedSignals(Bit.ZERO, Bit.ONE, Bit.ZERO, Bit.ONE);
		
		
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
	
		s.feedSignals(Bit.ONE);
		r.feedSignals(Bit.ZERO);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(q.getValue(), Bit.ONE);
		assertEquals(nq.getValue(), Bit.ZERO);
		
		s.feedSignals(Bit.ZERO);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(q.getValue(), Bit.ONE);
		assertEquals(nq.getValue(), Bit.ZERO);
		
		r.feedSignals(Bit.ONE);
		
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
		a.feedSignals(Bit.ONE, Bit.ONE, Bit.ONE, Bit.ONE);
		
		while(Simulation.TIMELINE.hasNext())
		{
			Simulation.TIMELINE.executeNext();
		}
		
		assertEquals(a.getUnsignedValue(), 15);
		assertEquals(a.getSignedValue(), -1);
	}
}
