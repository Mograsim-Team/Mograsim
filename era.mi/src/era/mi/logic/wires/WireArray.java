package era.mi.logic.wires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.Util;

/**
 * Represents an array of wires that can store n bits of information.
 * 
 * @author Fabian Stemmler
 *
 */
public class WireArray
{
	private Bit[] values;
	public final int travelTime;
	private List<WireArrayObserver> observers = new ArrayList<WireArrayObserver>();
	public final int length;
	private List<WireArrayInput> inputs = new ArrayList<WireArrayInput>();

	public WireArray(int length, int travelTime)
	{
		if (length < 1)
			throw new IllegalArgumentException(String.format("Tried to create an array of wires with length %d, but a length of less than 1 makes no sense.", length));
		this.length = length;
		this.travelTime = travelTime;
		initValues();
	}

	private void initValues()
	{
		values = Bit.Z.makeArray(length);
	}

	private void recalculateSingleInput()
	{
		WireArrayInput input = inputs.get(0);
		if (!Arrays.equals(input.getValues(), values))
		{
			Bit[] oldValues = values.clone();
			System.arraycopy(input.getValues(), 0, values, 0, length);
			notifyObservers(oldValues);
		}
	}

	private void recalculateMultipleInputs()
	{
		Iterator<WireArrayInput> it = inputs.iterator();
		Bit[] newValues = it.next().inputValues.clone();

		while (it.hasNext())
		{
			WireArrayInput input = it.next();
			Bit[] bits = input.getValues();
			for (int i = 0; i < length; i++)
			{
				if (Bit.Z.equals(bits[i]) || newValues[i].equals(bits[i]))
					continue;
				else if (Bit.Z.equals(newValues[i]))
					newValues[i] = bits[i];
				else
					newValues[i] = Bit.X;
			}
		}

		if (!Arrays.equals(newValues, values))
		{
			Bit[] oldValues = values;
			values = newValues;
			notifyObservers(oldValues);
		}
	}

	private void recalculate()
	{
		switch (inputs.size())
		{
		case 0:
			return;
		case 1:
			recalculateSingleInput();
			break;
		default:
			recalculateMultipleInputs();
		}
	}

	/**
	 * The WireArray is interpreted as an unsigned integer with n bits.
	 * 
	 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or
	 *         <code>Bit.ZERO</code> (they do not all have to have the same value),
	 *         not <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is
	 *         returned otherwise.
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean hasNumericValue()
	{
		for (Bit b : values)
		{
			if (b != Bit.ZERO && b != Bit.ONE)
				return false;
		}
		return true;
	}

	/**
	 * The WireArray is interpreted as an unsigned integer with n bits.
	 * 
	 * @return The unsigned value of the {@link WireArray}'s bits, where value 0
	 *         corresponds with 2^0, value 1 is 2^1 and so on.
	 * 
	 * @author Fabian Stemmler
	 */
	public long getUnsignedValue()
	{
		long val = 0;
		long mask = 1;
		for (int i = 0; i < length; i++)
		{
			switch (values[i])
			{
			default:
			case Z:
			case X:
				return 0; // TODO: Proper handling for getUnsignedValue(), if not all bits are 1 or 0;
			// Random number?
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
	 * 
	 * @return The signed value of the {@link WireArray}'s bits, where value 0
	 *         corresponds with 2^0, value 1 is 2^1 and so on.
	 * 
	 * @author Fabian Stemmler
	 */
	public long getSignedValue()
	{
		long val = getUnsignedValue();
		long mask = 1 << (length - 1);
		if ((mask & val) != 0)
		{
			int shifts = 64 - length;
			return (val << shifts) >> shifts;
		}
		return val;
	}

	/**
	 * Included for convenient use on {@link WireArray}s of length 1.
	 * 
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
	 * @return An array of length n containing the values of the n bits in the
	 *         {@link WireArray}. Can be safely modified.
	 * 
	 * @author Fabian Stemmler
	 */
	public Bit[] getValues()
	{
		return values.clone();
	}

	/**
	 * Adds an {@link WireArrayObserver}, who will be notified when the value of the
	 * {@link WireArray} is updated.
	 * 
	 * @param ob The {@link WireArrayObserver} to be notified of changes.
	 * @return true if the given {@link WireArrayObserver} was not already
	 *         registered, false otherwise
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean addObserver(WireArrayObserver ob)
	{
		return observers.add(ob);
	}

	private void notifyObservers(Bit[] oldValues)
	{
		for (WireArrayObserver o : observers)
			o.update(this, oldValues);
	}

	/**
	 * Create and register a {@link WireArrayInput} object, which is tied to this
	 * {@link WireArray}.
	 */
	public WireArrayInput createInput()
	{
		return new WireArrayInput(this);
	}

	private void registerInput(WireArrayInput toRegister)
	{
		inputs.add(toRegister);
	}

	/**
	 * A {@link WireArrayInput} feeds a constant signal into the {@link WireArray}
	 * it is tied to. The combination of all inputs determines the
	 * {@link WireArray}s final value. X dominates all other inputs Z does not
	 * affect the final value, unless there are no other inputs than Z 0 and 1 turn
	 * into X when they are mixed
	 * 
	 * @author Fabian Stemmler
	 */
	public class WireArrayInput
	{
		public final WireArray owner;
		private Bit[] inputValues;

		private WireArrayInput(WireArray owner)
		{
			super();
			this.owner = owner;
			initValues();
			owner.registerInput(this);
		}

		private void initValues()
		{
			inputValues = Bit.Z.makeArray(length);
		}

		/**
		 * Sets the wires values. This takes up time, as specified by the
		 * {@link WireArray}s travel time.
		 * 
		 * @param newValues The new values the wires should take on.
		 * 
		 * @author Fabian Stemmler
		 */
		public void feedSignals(Bit... newValues)
		{
			if (newValues.length == length)
			{
				feedSignals(0, newValues);
			} else
				throw new IllegalArgumentException(String.format("Attempted to input %o bits instead of %o bits.", newValues.length, length));
		}

		/**
		 * Sets values of a subarray of wires. This takes up time, as specified by the
		 * {@link WireArray}s travel time.
		 * 
		 * @param newValues   The new values the wires should take on.
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
			int exclLastIndex = startingBit + newValues.length;
			if (length < exclLastIndex)
				throw new ArrayIndexOutOfBoundsException(String.format("Attempted to input bits from index %o to %o when there are only %o wires.", startingBit, exclLastIndex - 1, length));
			if (!Arrays.equals(inputValues, startingBit, exclLastIndex, newValues, 0, newValues.length))
			{
				System.arraycopy(newValues, 0, inputValues, startingBit, newValues.length);
				owner.recalculate();
			}
		}

		/**
		 * Returns a copy (safe to modify) of the values the {@link WireArrayInput} is currently feeding into the associated {@link WireArray}.
		 */
		public Bit[] getValues()
		{
			return inputValues.clone();
		}

		/**
		 * {@link WireArrayInput} now feeds Z into the associated {@link WireArray}.
		 */
		public void clearSignals()
		{
			feedSignals(Bit.Z.makeArray(length));
		}

		public Bit[] wireValuesExcludingMe() 
		{
			Bit[] bits = Bit.Z.makeArray(length);
			for (WireArrayInput wai : inputs) 
			{
				if(wai == this)
					continue;
				Util.combineInto(bits, wai.getValues());
			}
			return bits;
		}
		
		@Override
		public String toString()
		{
			return Arrays.toString(inputValues);
		}
	}

	@Override
	public String toString()
	{
		return String.format("wire 0x%08x value: %s inputs: %s", hashCode(), Arrays.toString(values), inputs);
	}

	public static WireArrayInput[] extractInputs(WireArray[] w)
	{
		WireArrayInput[] inputs = new WireArrayInput[w.length];
		for (int i = 0; i < w.length; i++)
			inputs[i] = w[i].createInput();
		return inputs;
	}
}