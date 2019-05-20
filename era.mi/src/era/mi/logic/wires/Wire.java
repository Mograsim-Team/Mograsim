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
public class Wire
{
	private Bit[] values;
	public final int travelTime;
	private List<WireObserver> observers = new ArrayList<WireObserver>();
	public final int length;
	private List<WireEnd> inputs = new ArrayList<WireEnd>();

	public Wire(int length, int travelTime)
	{
		if (length < 1)
			throw new IllegalArgumentException(
					String.format("Tried to create an array of wires with length %d, but a length of less than 1 makes no sense.", length));
		this.length = length;
		this.travelTime = travelTime;
		initValues();
	}

	private void initValues()
	{
		values = Bit.U.makeArray(length);
	}

	private void recalculateSingleInput()
	{
		WireEnd input = inputs.get(0);
		if (!Arrays.equals(input.getInputValues(), values))
		{
			Bit[] oldValues = values.clone();
			System.arraycopy(input.getInputValues(), 0, values, 0, length);
			notifyObservers(oldValues);
		}
	}

	private void recalculateMultipleInputs()
	{
		Iterator<WireEnd> it = inputs.iterator();
		Bit[] newValues = it.next().inputValues.clone();

		while (it.hasNext())
		{
			WireEnd input = it.next();
			Bit[] bits = input.getInputValues();
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
	 * The {@link Wire} is interpreted as an unsigned integer with n bits.
	 * 
	 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or <code>Bit.ZERO</code> (they do not all have to have the same
	 *         value), not <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is returned otherwise.
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
	 * The {@link Wire} is interpreted as an unsigned integer with n bits.
	 * 
	 * @return The unsigned value of the {@link Wire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
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
	 * The {@link Wire} is interpreted as a signed integer with n bits.
	 * 
	 * @return The signed value of the {@link Wire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
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

	public Bit getValue()
	{
		return getValue(0);
	}

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

	public Bit[] getValues()
	{
		return values.clone();
	}

	/**
	 * Adds an {@link WireObserver}, who will be notified when the value of the {@link Wire} is updated.
	 * 
	 * @param ob The {@link WireObserver} to be notified of changes.
	 * @return true if the given {@link WireObserver} was not already registered, false otherwise
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean addObserver(WireObserver ob)
	{
		return observers.add(ob);
	}

	private void notifyObservers(Bit[] oldValues)
	{
		for (WireObserver o : observers)
			o.update(this, oldValues);
	}

	/**
	 * Create and register a {@link WireEnd} object, which is tied to this {@link Wire}.
	 */
	public WireEnd createEnd()
	{
		return new WireEnd();
	}

	private void registerInput(WireEnd toRegister)
	{
		inputs.add(toRegister);
	}

	/**
	 * A {@link WireEnd} feeds a constant signal into the {@link Wire} it is tied to. The combination of all inputs determines the
	 * {@link Wire}s final value. X dominates all other inputs Z does not affect the final value, unless there are no other inputs than Z 0
	 * and 1 turn into X when they are mixed
	 * 
	 * @author Fabian Stemmler
	 */
	public class WireEnd
	{
		private boolean open;
		private Bit[] inputValues;

		private WireEnd()
		{
			super();
			open = true;
			initValues();
			registerInput(this);
		}

		private void initValues()
		{
			inputValues = Bit.Z.makeArray(length);
		}

		/**
		 * Sets the wires values. This takes up time, as specified by the {@link Wire}s travel time.
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
				throw new IllegalArgumentException(
						String.format("Attempted to input %d bits instead of %d bits.", newValues.length, length));
		}

		/**
		 * Sets values of a subarray of wires. This takes up time, as specified by the {@link Wire}s travel time.
		 * 
		 * @param newValues   The new values the wires should take on.
		 * @param startingBit The first index of the subarray of wires.
		 * 
		 * @author Fabian Stemmler
		 */
		public void feedSignals(int startingBit, Bit... newValues)
		{
			if (!open)
				throw new RuntimeException("Attempted to write to closed WireArrayEnd.");
			Simulation.TIMELINE.addEvent((e) -> setValues(startingBit, newValues), travelTime);
		}

		private void setValues(int startingBit, Bit... newValues)
		{
			int exclLastIndex = startingBit + newValues.length;
			if (length < exclLastIndex)
				throw new ArrayIndexOutOfBoundsException(
						String.format("Attempted to input bits from index %d to %d when there are only %d wires.", startingBit,
								exclLastIndex - 1, length));
			if (!Arrays.equals(inputValues, startingBit, exclLastIndex, newValues, 0, newValues.length))
			{
				System.arraycopy(newValues, 0, inputValues, startingBit, newValues.length);
				Wire.this.recalculate();
			}
		}

		/**
		 * @return The value (of bit 0) the {@link WireEnd} is currently feeding into the associated {@link Wire}.
		 */
		public Bit getInputValue()
		{
			return getInputValue(0);
		}

		/**
		 * @return The value which the {@link WireEnd} is currently feeding into the associated {@link Wire} at the indexed {@link Bit}.
		 */
		public Bit getInputValue(int index)
		{
			return inputValues[index];
		}

		/**
		 * @return A copy (safe to modify) of the values the {@link WireEnd} is currently feeding into the associated {@link Wire}.
		 */
		public Bit[] getInputValues()
		{
			return getInputValues(0, length);
		}

		public Bit[] getInputValues(int start, int end)
		{
			int length = end - start;
			Bit[] bits = new Bit[length];
			System.arraycopy(inputValues, start, bits, 0, length);
			return bits;
		}

		/**
		 * {@link WireEnd} now feeds Z into the associated {@link Wire}.
		 */
		public void clearSignals()
		{
			feedSignals(Bit.Z.makeArray(length));
		}

		public Bit[] wireValuesExcludingMe()
		{
			Bit[] bits = Bit.Z.makeArray(length);
			for (WireEnd wai : inputs)
			{
				if (wai == this)
					continue;
				Util.combineInto(bits, wai.getInputValues());
			}
			return bits;
		}

		/**
		 * Included for convenient use on {@link Wire}s of length 1.
		 * 
		 * @return The value of bit 0.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit getValue()
		{
			return Wire.this.getValue();
		}

		/**
		 * @param index Index of the requested bit.
		 * @return The value of the indexed bit.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit getValue(int index)
		{
			return Wire.this.getValue(index);
		}

		/**
		 * @param index Index of the requested bit.
		 * @return The value of the indexed bit.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit[] getValues()
		{
			return Wire.this.getValues();
		}

		/**
		 * @param start Start of the wanted segment. (inclusive)
		 * @param end   End of the wanted segment. (exclusive)
		 * @return The values of the segment of {@link Bit}s indexed.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit[] getValues(int start, int end)
		{
			return Wire.this.getValues(start, end);
		}

		/**
		 * The {@link Wire} is interpreted as an unsigned integer with n bits.
		 * 
		 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or <code>Bit.ZERO</code> (they do not all have to have the
		 *         same value), not <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is returned otherwise.
		 * 
		 * @author Fabian Stemmler
		 */
		public boolean hasNumericValue()
		{
			return Wire.this.hasNumericValue();
		}

		/**
		 * The {@link Wire} is interpreted as an unsigned integer with n bits.
		 * 
		 * @return The unsigned value of the {@link Wire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
		 * 
		 * @author Fabian Stemmler
		 */
		public long getUnsignedValue()
		{
			return Wire.this.getUnsignedValue();
		}

		/**
		 * The {@link Wire} is interpreted as a signed integer with n bits.
		 * 
		 * @return The signed value of the {@link Wire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
		 * 
		 * @author Fabian Stemmler
		 */
		public long getSignedValue()
		{
			return Wire.this.getSignedValue();
		}

		@Override
		public String toString()
		{
			return Arrays.toString(values);
			// return String.format("%s \nFeeding: %s", WireArray.this.toString(), Arrays.toString(inputValues));
		}

		public void disconnect()
		{
			inputs.remove(this);
			open = false;
		}

		public int length()
		{
			return length;
		}

		public boolean addObserver(WireObserver ob)
		{
			return Wire.this.addObserver(ob);
		}

		public Wire getWire()
		{
			return Wire.this;
		}
	}

	@Override
	public String toString()
	{
		return String.format("wire 0x%08x value: %s inputs: %s", hashCode(), Arrays.toString(values), inputs);
		// Arrays.toString(values), inputs.stream().map(i -> Arrays.toString(i.inputValues)).reduce((s1, s2) -> s1 + s2)
	}

	public static WireEnd[] extractEnds(Wire[] w)
	{
		WireEnd[] inputs = new WireEnd[w.length];
		for (int i = 0; i < w.length; i++)
			inputs[i] = w[i].createEnd();
		return inputs;
	}
}