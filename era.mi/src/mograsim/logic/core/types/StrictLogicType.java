package mograsim.logic.core.types;

/**
 * Interface for types that support the basic logic operations only among themselves, making it mathematically closed
 * 
 * @author Christian Femers
 * @see LogicType
 *
 * @param <T> the logic type itself to make the operations closed
 */
public interface StrictLogicType<T extends StrictLogicType<T>> extends LogicType<T, T>
{
	// this is just a matter of type parameters
}
