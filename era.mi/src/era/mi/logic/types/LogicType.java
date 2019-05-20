package era.mi.logic.types;

/**
 * Interface for types that support the basic logic operations
 *
 * @author Christian Femers
 *
 * @param <T> the logic type itself, to make the operations closed in T
 * @param <S> the operand type, may be the same as T, see {@link StrictLogicType}
 */
public interface LogicType<T extends LogicType<T, S>, S>
{
	/**
	 * Determines the result when two signals meet each other directly, also called resolution (IEEE 1164)
	 * 
	 * For example:
	 * 
	 * <pre>
	 * 0 joined 0 == 0
	 * 1 joined 0 == X
	 * 0 joined 1 == X
	 * 1 joined 1 == 1
	 * </pre>
	 * 
	 * @param t the second logic signal
	 * @return the resulting value
	 * @author Christian Femers
	 */
	T join(S t);

	/**
	 * Classical logic AND
	 * 
	 * For example:
	 * 
	 * <pre>
	 * 0 AND 0 == 0
	 * 1 AND 0 == 0
	 * 0 AND 1 == 0
	 * 1 AND 1 == 1
	 * </pre>
	 * 
	 * @param t the second logic signal
	 * @return the resulting value
	 * @author Christian Femers
	 */
	T and(S t);

	/**
	 * Classical logic OR
	 *
	 * For example:
	 * 
	 * <pre>
	 * 0 OR 0 == 0
	 * 1 OR 0 == 1
	 * 0 OR 1 == 1
	 * 1 OR 1 == 1
	 * </pre>
	 * 
	 * @param t the second logic signal
	 * @return the resulting value
	 * @author Christian Femers
	 */
	T or(S t);

	/**
	 * Classical logic XOR (exclusive OR)
	 * 
	 * For example:
	 * 
	 * <pre>
	 * 0 XOR 0 == 0
	 * 1 XOR 0 == 1
	 * 0 XOR 1 == 1
	 * 1 XOR 1 == 0
	 * </pre>
	 * 
	 * @param t the second logic signal
	 * @return the resulting value
	 * @author Christian Femers
	 */
	T xor(S t);

	/**
	 * Classical logic NOT (logical negation)
	 * 
	 * For example:
	 * 
	 * <pre>
	 * NOT 0 == 1
	 * NOT 1 == 0
	 * </pre>
	 * 
	 * @return the resulting value
	 * @author Christian Femers
	 */
	T not();

	/**
	 * {@link #and(Object) AND} and then {@link #not() NOT}
	 * 
	 * @author Christian Femers
	 */
	default T nand(S t)
	{
		return and(t).not();
	}

	/**
	 * {@link #or(Object) OR} and then {@link #not() NOT}
	 * 
	 * @author Christian Femers
	 */
	default T nor(S t)
	{
		return or(t).not();
	}

	/**
	 * {@link #xor(Object) XOR} and then {@link #not() NOT}<br>
	 * Used to determine equality (alias parity, consistency)
	 * 
	 * @author Christian Femers
	 */
	default T xnor(S t)
	{
		return xor(t).not();
	}
}
