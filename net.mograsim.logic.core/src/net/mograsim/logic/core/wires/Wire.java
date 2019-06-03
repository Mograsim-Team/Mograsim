package net.mograsim.logic.core.wires;

import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.Z;

import java.util.ArrayList;
import java.util.List;

import net.mograsim.logic.core.LogicObservable;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;

/**
 * Represents an array of wires that can store n bits of information.
 * 
 * @author Fabian Stemmler
 *
 */
public class Wire
{
	public final String name;
	private BitVector values;
	public final int travelTime;
	private List<ReadEnd> attached = new ArrayList<>();
	public final int length;
	List<ReadWriteEnd> inputs = new ArrayList<>();
	Timeline timeline;

	public Wire(Timeline timeline, int length, int travelTime)
	{
		this(timeline, length, travelTime, null);
	}

	public Wire(Timeline timeline, int length, int travelTime, String name)
	{
		if (length < 1)
			throw new IllegalArgumentException(
					String.format("Tried to create an array of wires with length %d, but a length of less than 1 makes no sense.", length));
		this.name = name;
		this.timeline = timeline;
		this.length = length;
		this.travelTime = travelTime;
		initValues();
	}

	private void initValues()
	{
		values = U.toVector(length);
	}

	private void recalculateSingleInput()
	{
		setNewValues(inputs.get(0).getInputValues());
	}

	private void recalculateMultipleInputs()
	{
		BitVectorMutator mutator = BitVectorMutator.empty();
		for (ReadWriteEnd wireArrayEnd : inputs)
			mutator.join(wireArrayEnd.getInputValues());
		setNewValues(mutator.get());
	}

	private void setNewValues(BitVector newValues)
	{
		if (values.equals(newValues))
			return;
//		BitVector oldValues = values;
		values = newValues;
		notifyObservers();
	}

	void recalculate()
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
		for (Bit bit : values)
		{
			switch (bit)
			{
			default:
			case Z:
			case X:
				return 0; // TODO: Proper handling for getUnsignedValue(), if not all bits are 1 or 0;
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
		return values.getBit(index);
	}

	public BitVector getValues(int start, int end)
	{
		return values.subVector(start, end);
	}

	public BitVector getValues()
	{
		return values;
	}

	/**
	 * Adds an {@link LogicObserver}, who will be notified when the value of the {@link Wire} is updated.
	 * 
	 * @param ob The {@link LogicObserver} to be notified of changes.
	 * @return true if the given {@link LogicObserver} was not already registered, false otherwise
	 * 
	 * @author Fabian Stemmler
	 */
	void attachEnd(ReadEnd end)
	{
		attached.add(end);
	}

	void detachEnd(ReadEnd end)
	{
		attached.remove(end);
	}

	private void notifyObservers()
	{
		attached.forEach(r -> r.update());
	}

	/**
	 * Create and register a {@link ReadWriteEnd} object, which is tied to this {@link Wire}. This {@link ReadWriteEnd} can be written to.
	 */
	public ReadWriteEnd createReadWriteEnd()
	{
		return new ReadWriteEnd();
	}

	/**
	 * Create a {@link ReadEnd} object, which is tied to this {@link Wire}. This {@link ReadEnd} cannot be written to.
	 */
	public ReadEnd createReadOnlyEnd()
	{
		return new ReadEnd();
	}

	void registerInput(ReadWriteEnd toRegister)
	{
		inputs.add(toRegister);
	}

	/**
	 * A {@link ReadEnd} feeds a constant signal into the {@link Wire} it is tied to. The combination of all inputs determines the
	 * {@link Wire}s final value. X dominates all other inputs Z does not affect the final value, unless there are no other inputs than Z 0
	 * and 1 turn into X when they are mixed
	 * 
	 * @author Fabian Stemmler
	 */
	public class ReadEnd implements LogicObservable
	{
		private List<LogicObserver> observers = new ArrayList<>();

		ReadEnd()
		{
			super();
			Wire.this.attachEnd(this);
		}

		public void update()
		{
			notifyObservers();
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
		public BitVector getValues()
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
		public BitVector getValues(int start, int end)
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
			return Wire.this.toString();
		}

		public void close()
		{
			inputs.remove(this);
			detachEnd(this);
			recalculate();
		}

		public int length()
		{
			return length;
		}

		public Wire getWire()
		{
			return Wire.this;
		}

		@Override
		public void registerObserver(LogicObserver ob)
		{
			observers.add(ob);
		}

		@Override
		public void deregisterObserver(LogicObserver ob)
		{
			observers.remove(ob);
		}

		@Override
		public void notifyObservers()
		{
			observers.forEach(ob -> ob.update(this));
		}
	}

	public class ReadWriteEnd extends ReadEnd
	{
		private boolean open;
		private BitVector inputValues;

		ReadWriteEnd()
		{
			super();
			open = true;
			initValues();
			registerInput(this);
		}

		private void initValues()
		{
			inputValues = U.toVector(length);
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
			feedSignals(BitVector.of(newValues));
		}

		public void feedSignals(BitVector newValues)
		{
			if (newValues.length() != length)
				throw new IllegalArgumentException(
						String.format("Attempted to input %d bits instead of %d bits.", newValues.length(), length));
			if (!open)
				throw new RuntimeException("Attempted to write to closed WireArrayEnd.");
			timeline.addEvent(e -> setValues(newValues), travelTime);
		}

		/**
		 * Sets values of a subarray of wires. This takes up time, as specified by the {@link Wire}s travel time.
		 * 
		 * @param bitVector   The new values the wires should take on.
		 * @param startingBit The first index of the subarray of wires.
		 * 
		 * @author Fabian Stemmler
		 */
		public void feedSignals(int startingBit, BitVector bitVector)
		{
			if (!open)
				throw new RuntimeException("Attempted to write to closed WireArrayEnd.");
			timeline.addEvent(e -> setValues(startingBit, bitVector), travelTime);
		}

		private void setValues(int startingBit, BitVector newValues)
		{
			// index check covered in equals
			if (!inputValues.equalsWithOffset(newValues, startingBit))
			{
				Bit[] vals = inputValues.getBits();
				System.arraycopy(newValues.getBits(), 0, vals, startingBit, newValues.length());
				inputValues = BitVector.of(vals);
				Wire.this.recalculate();
			}
		}

		private void setValues(BitVector newValues)
		{
			if (inputValues.equals(newValues))
				return;
			inputValues = newValues;
			Wire.this.recalculate();
		}

		/**
		 * @return The value (of bit 0) the {@link ReadEnd} is currently feeding into the associated {@link Wire}.
		 */
		public Bit getInputValue()
		{
			return getInputValue(0);
		}

		/**
		 * @return The value which the {@link ReadEnd} is currently feeding into the associated {@link Wire} at the indexed {@link Bit}.
		 */
		public Bit getInputValue(int index)
		{
			return inputValues.getBit(index);
		}

		/**
		 * @return A copy (safe to modify) of the values the {@link ReadEnd} is currently feeding into the associated {@link Wire}.
		 */
		public BitVector getInputValues()
		{
			return getInputValues(0, length);
		}

		public BitVector getInputValues(int start, int end)
		{
			return inputValues.subVector(start, end);
		}

		/**
		 * {@link ReadEnd} now feeds Z into the associated {@link Wire}.
		 */
		public void clearSignals()
		{
			feedSignals(Z.toVector(length));
		}

		public BitVector wireValuesExcludingMe()
		{
			BitVectorMutator mutator = BitVectorMutator.empty();
			for (ReadWriteEnd wireEnd : inputs)
			{
				if (wireEnd == this)
					continue;
				mutator.join(wireEnd.inputValues);
			}
			return mutator.get();
		}

		@Override
		public String toString()
		{
			return inputValues.toString();
		}
	}

	@Override
	public String toString()
	{
		String name = this.name == null ? String.format("0x%08x", hashCode()) : this.name;
		return String.format("wire %s value: %s inputs: %s", name, values, inputs);
	}

	public static ReadEnd[] extractEnds(Wire[] w)
	{
		ReadEnd[] inputs = new ReadEnd[w.length];
		for (int i = 0; i < w.length; i++)
			inputs[i] = w[i].createReadWriteEnd();
		return inputs;
	}
}