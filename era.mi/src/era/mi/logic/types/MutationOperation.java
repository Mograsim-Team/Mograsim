package era.mi.logic.types;

import java.util.function.BiFunction;

import era.mi.logic.types.BitVector.BitVectorMutator;

@FunctionalInterface
public interface MutationOperation extends BiFunction<BitVectorMutator, BitVector, BitVectorMutator>
{

}
