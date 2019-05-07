package era.mi.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an array of wires that can store n bits of information.
 * @author Fabian Stemmler
 *
 */
public class WireArray
{
	private Bit[] values;
	private final int travelTime;
	private List<WireArrayObserver> observers = new ArrayList<>();//<WireArrayObserver>();
	private final int length;
	
	public WireArray(int length, int travelTime)
	{
		if(length < 1)
			throw new IllegalArgumentException("Tried to create an array of wires with length " + length + ", but a length of less than 1 makes no sense.");
		this.length = length;
		this.travelTime = travelTime;
		initValues();
	}
	
	private void initValues()
	{
		values = new Bit[length];
		for(int i = 0; i < length; i++)
			values[i] = Bit.X;
	}
	
	/**
	 * Sets the wires values. This takes up time, as specified by the {@link WireArray}s travel time.
	 * @param newValues The new values the wires should take on.
	 * 
	 * @author Fabian Stemmler
	 */
	public void feedSignals(Bit... newValues)
	{
		Simulation.TIMELINE.addEvent((e) -> setValues(newValues), travelTime);
	}
	
	private void setValues(Bit... newValues)
	{
		if(length != newValues.length)
			throw new IllegalArgumentException(String.format("Unexpected length for input array. Length was %d but expected %d", newValues.length, length)); //TODO: Proper handling
		if(!Arrays.equals(values, newValues))
		{
			values = newValues.clone();
			notifyObservers();
		}
	}
	
	/**
	 * Sets values of a subarray of wires. This takes up time, as specified by the {@link WireArray}s travel time.
	 * @param newValues The new values the wires should take on.
	 * @param startingBit The first index of the subarray of wires.
	 * 
	 * @author Fabian Stemmler
	 */
	public void feedSignals(int startingBit, Bit... newValues)
	{
		Simulation.TIMELINE.addEvent((e) -> setValues(startingBit, newValues), travelTime);
	}
	
	private void setValues(int startingBit, Bit... newValues)
	{
		if(length < startingBit + newValues.length)
			throw new IllegalArgumentException(); //TODO: Proper handling	
		if(!Arrays.equals(values, startingBit, startingBit + newValues.length, newValues, 0, newValues.length))
		{
			System.arraycopy(newValues, 0, values, startingBit, newValues.length);
			notifyObservers();
		}
	}
	
	/**
	 * The WireArray is interpreted as an unsigned integer with n bits.
	 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or <code>Bit.ZERO</code> (they do not all have to have the same value), not <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is returned otherwise.
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean hasNumericValue()
	{
		for(Bit b : values)
		{
			if(b != Bit.ZERO && b != Bit.ONE)
				return false;
		}
		return true;
	}
	
	/**
	 * The WireArray is interpreted as an unsigned integer with n bits.
	 * @return The unsigned value of the {@link WireArray}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
	 * 
	 * @author Fabian Stemmler
	 */
	public int getUnsignedValue()
	{
		int val = 0;
		int mask = 1;
		for(int i = 0; i < length; i++)
		{
			switch(values[i])
			{
			default:
			case Z:
			case X:
				return 0; //TODO: Proper handling for getUnsignedValue(), if not all bits are 1 or 0; Random number?
			case ONE:
				val |= mask;
				break;
			case ZERO:
			}
			mask = mask << 1;
		}
		return val;
	}
	
	/**
	 * The WireArray is interpreted as a signed integer with n bits.
	 * @return The signed value of the {@link WireArray}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
	 * 
	 * @author Fabian Stemmler
	 */
	public int getSignedValue()
	{
		int val = getUnsignedValue();
		int mask = 1 << (length - 1);
		if((mask & val) != 0)
		{
			int shifts = 32 - length;
			return (val << shifts) >> shifts;
		}
		return val;
	}
	
	/**
	 * Included for convenient use on {@link WireArray}s of length 1.
	 * @return The value of bit 0.
	 * 
	 * @author Fabian Stemmler
	 */
	public Bit getValue()
	{
		return getValue(0);
	}
	
	/**
	 * 
	 * @param index Index of the requested bit.
	 * @return The value of the indexed bit.
	 * 
	 * @author Fabian Stemmler
	 */
	public Bit getValue(int index)
	{
		//TODO: ArrayIndexOutOfBoundsException handling for accessing single bit in WireArray
		return values[index];
	}
	
	public Bit[] getValues(int start, int end)
	{
		int length = end - start;
		Bit[] bits = new Bit[length];
		System.arraycopy(values, start, bits, 0, length);		
		return bits;
	}
	
	
	/**
	 * @return An array of length n containing the values of the n bits in the {@link WireArray}. Can be safely modified.
	 * 
	 * @author Fabian Stemmler
	 */
	public Bit[] getValues()
	{
		return values.clone();
	}

	public int length()
	{
		return length;
	}
	
	
	/**
	 * Adds an {@link WireArrayObserver}, who will be notified when the value of the {@link WireArray} is updated.
	 * @param ob The {@link WireArrayObserver} to be notified of changes.
	 * @return true if the given {@link WireArrayObserver} was not already registered, false otherwise
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean addObserver(WireArrayObserver ob)
	{
		return observers.add(ob);
	}
	
	private void notifyObservers()
	{
		for(WireArrayObserver o : observers)
			o.update(this);
	}
}
