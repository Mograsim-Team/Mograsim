package net.mograsim.machine.standard.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.MainMemoryDefinition;

class WordAddressableMemoryTest {
	
	private Timeline t = new Timeline(10);

	@Test
	public void wordAddressableMemoryLargeTest()
	{
		CoreWire rW = new CoreWire(t, 1, 2);
		CoreWire data = new CoreWire(t, 16, 2);
		CoreWire address = new CoreWire(t, 64, 2);
		CoreWire clock = new CoreWire(t, 1, 2);
		ReadWriteEnd rWI = rW.createReadWriteEnd();
		ReadWriteEnd dataI = data.createReadWriteEnd();
		ReadWriteEnd addressI = address.createReadWriteEnd();
		ReadWriteEnd clockI = clock.createReadWriteEnd();

		@SuppressWarnings("unused")
		CoreWordAddressableMemory memory = new CoreWordAddressableMemory(t, 4, MainMemoryDefinition.create(64, 16, 4096L, Long.MAX_VALUE), data.createReadWriteEnd(),
				rW.createReadOnlyEnd(), address.createReadOnlyEnd(), clock.createReadOnlyEnd());

		clockI.feedSignals(Bit.ONE);
		
		Random r = new Random();
		for (long j = 1; j > 0; j *= 2)
		{
			for (int i = 0; i < 50; i++)
			{
				String sAddress = String.format("%64s", BigInteger.valueOf(4096 + i + j).toString(2)).replace(' ', '0');
				sAddress = new StringBuilder(sAddress).reverse().toString();
				BitVector bAddress = BitVector.parse(sAddress);
				addressI.feedSignals(bAddress);
				t.executeAll();
				String random = BigInteger.valueOf(Math.abs(r.nextInt())).toString(5);
				random = random.substring(Integer.max(0, random.length() - 16));
				random = String.format("%16s", random).replace(' ', '0');
				random = random.replace('2', 'X').replace('3', 'Z').replace('4', 'U');
				BitVector vector = BitVector.parse(random);
				dataI.feedSignals(vector);
				rWI.feedSignals(Bit.ZERO);
				t.executeAll();
				rWI.feedSignals(Bit.ONE);
				t.executeAll();
				dataI.clearSignals();
				t.executeAll();

				assertEquals(dataI.getValues(), vector);
			}
		}
	}

}
