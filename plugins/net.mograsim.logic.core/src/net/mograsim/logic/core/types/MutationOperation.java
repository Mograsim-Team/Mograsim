package net.mograsim.logic.core.types;

import java.util.function.BiFunction;

import net.mograsim.logic.core.types.BitVector.BitVectorMutator;

@FunctionalInterface
public interface MutationOperation extends BiFunction<BitVectorMutator, BitVector, BitVectorMutator>
{
	// no changes necessary, only for convenience and readability
}
