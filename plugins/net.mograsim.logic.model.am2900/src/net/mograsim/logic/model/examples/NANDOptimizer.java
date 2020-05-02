package net.mograsim.logic.model.examples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NANDOptimizer
{
	public static void main(String[] args)
	{
		System.out.println(optimize(3, Set.of(0b001, 0b010, 0b011, 0b100), Set.of(0b000)));
	}

	private static String optimize(int bitCount, Set<Integer> combinationsRequiredZero, Set<Integer> combinationsRequiredOne)
	{
		BigInteger maskZeroes = combinationsRequiredZero.stream().map(BigInteger.ONE::shiftLeft).reduce(BigInteger.ZERO, BigInteger::or);
		BigInteger maskOnes = combinationsRequiredOne.stream().map(BigInteger.ONE::shiftLeft).reduce(BigInteger.ZERO, BigInteger::or);
		if (maskZeroes.and(maskOnes).compareTo(BigInteger.ZERO) != 0)
			throw new IllegalArgumentException("A combination is required to be both 1 and 0");
		BigInteger mask = maskZeroes.or(maskOnes);
		BigInteger target = maskOnes;

		return optimize(bitCount, mask, target);
	}

	public static String optimize(int bitCount, BigInteger mask, BigInteger target)
	{
		long start = System.currentTimeMillis();
		Map<BigInteger, String> atomicCombinations = generateAtomicCombinations(bitCount);

		for (Entry<BigInteger, String> e : atomicCombinations.entrySet())
			if (check(e.getKey(), target, mask))
				return e.getValue();

		List<Map<BigInteger, String>> mapPool = new ArrayList<>();
		mapPool.add(new HashMap<>());

		for (int gateCount = 1;; gateCount++)
		{
			mapPool.add(new HashMap<>());
			System.out.println("Checking gateCount " + gateCount + "...");
			String optimized = optimize(atomicCombinations, mask, target, gateCount, mapPool.toArray(Map[]::new));
			if (optimized != null)
			{
				long stop = System.currentTimeMillis();
				System.out.println("Took " + (stop - start));
				return optimized;
			}
		}
	}

	@SuppressWarnings("null")
	private static String optimize(Map<BigInteger, String> availableCombinations, BigInteger mask, BigInteger target, int remainingGates,
			Map<BigInteger, String>[] mapPool)
	{
		final boolean deeperLevelExists = remainingGates != 1;
		Map<BigInteger, String> availableCombinationsCopy;
		if (deeperLevelExists)
		{
			availableCombinationsCopy = mapPool[remainingGates];
			availableCombinationsCopy.putAll(availableCombinations);
		} else
			availableCombinationsCopy = null;
		for (Entry<BigInteger, String> source1 : availableCombinations.entrySet())
			for (Entry<BigInteger, String> source2 : availableCombinations.entrySet())
			{
				BigInteger nand = nand(source1.getKey(), source2.getKey());
				if (availableCombinations.containsKey(nand))
					continue;
				String name = '(' + source1.getValue() + ' ' + source2.getValue() + ')';
				if (deeperLevelExists)
				{
					availableCombinationsCopy.put(nand, name);
					String result = optimize(availableCombinationsCopy, mask, target, remainingGates - 1, mapPool);
					if (result != null)
						return result;
					availableCombinationsCopy.remove(nand);
				} else if (check(nand, target, mask))
					return name;
			}

		if (deeperLevelExists)
			availableCombinationsCopy.clear();
		return null;
	}

	private static boolean check(BigInteger combination, BigInteger target, BigInteger mask)
	{
		return combination.xor(target).and(mask).compareTo(BigInteger.ZERO) == 0;
	}

	private static Map<BigInteger, String> generateAtomicCombinations(int bitCount)
	{
		Map<BigInteger, String> atomicCombinations = new HashMap<>();
		for (int bit = 0; bit < bitCount; bit++)
		{
			BigInteger atomicCombination = BigInteger.ZERO;
			for (int i = 1 << bit; i < 1 << bitCount; i += 2 << bit)
				for (int j = 0; j < 1 << bit; j++)
					atomicCombination = atomicCombination.or(BigInteger.ONE.shiftLeft(i + j));
			atomicCombinations.put(atomicCombination, Integer.toString(bit));
		}
		return atomicCombinations;
	}

	private static BigInteger nand(BigInteger a, BigInteger b)
	{
		return b.and(a).not();
	}
}
