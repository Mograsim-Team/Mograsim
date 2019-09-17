package net.mograsim.machine.standard.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.LongStream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.core.wires.CoreWire.ReadWriteEnd;
import net.mograsim.machine.MainMemoryDefinition;

class WordAddressableMemoryTest
{

	private Timeline t = new Timeline(10);

	@ParameterizedTest(name = "seed = {0}")
	@MethodSource("seedsForRandom")
	public void wordAddressableMemoryLargeTest(long seed)
	{
		CoreWire rW = new CoreWire(t, 1, 2);
		CoreWire data = new CoreWire(t, 16, 2);
		CoreWire address = new CoreWire(t, 64, 2);
		ReadWriteEnd rWI = rW.createReadWriteEnd();
		ReadWriteEnd dataI = data.createReadWriteEnd();
		ReadWriteEnd addressI = address.createReadWriteEnd();

		MainMemoryDefinition definition = MainMemoryDefinition.create(64, 16, 4096L, Long.MAX_VALUE);
		CoreWordAddressableMemory memory = new CoreWordAddressableMemory(t, 4, definition, data.createReadWriteEnd(),
				rW.createReadOnlyEnd(), address.createReadOnlyEnd());
		memory.setMemory(new WordAddressableMemory(definition));

		Random r = new Random(seed);
		for (long j = 1; j > 0; j *= 2)
		{
			for (int i = 0; i < 100; i++)
			{
				BitVector bAddress = BitVector.from(4096 + i + j, 64);
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

				assertEquals(vector, dataI.getValues(), "seed=" + seed + ", j=" + j + ", i=" + i);
			}
		}
	}

	public static LongStream seedsForRandom()
	{
		return LongStream.range(0, 20);
	}
}
