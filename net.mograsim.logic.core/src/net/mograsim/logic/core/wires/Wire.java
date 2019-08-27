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
	public final int width;
	List<ReadWriteEnd> inputs = new ArrayList<>();
	Timeline timeline;

	public Wire(Timeline timeline, int width, int travelTime)
	{
		this(timeline, width, travelTime, null);
	}

	public Wire(Timeline timeline, int width, int travelTime, String name)
	{
		if (width < 1)
			throw new IllegalArgumentException(
					String.format("Tried to create an array of wires with width %d, but a width of less than 1 makes no sense.", width));
		this.name = name;
		this.timeline = timeline;
		this.width = width;
		this.travelTime = travelTime;
		initValues();
	}

	private void initValues()
	{
		values = U.toVector(width);
	}

	private void setNewValues(BitVector newValues)
	{
		if (values.equals(newValues))
			return;
		values = newValues;
		notifyObservers();
	}

	void recalculate()
	{
		if (inputs.isEmpty())
			setNewValues(U.toVector(width));
		else
		{
			BitVectorMutator mutator = BitVectorMutator.empty();
			for (ReadWriteEnd wireArrayEnd : inputs)
				mutator.join(wireArrayEnd.getInputValues());
			setNewValues(mutator.toBitVector());
		}
	}

	/**
	 * Forces a Wire to take on specific values. If the new values differ from the old ones, the observers of the Wire will be notified.
	 * WARNING! Use this with care! The preferred way of writing the values is ReadWriteEnd.feedSignals(BitVector)
	 * 
	 * @param values The values the <code>Wire</code> will have immediately after this method is called
	 */
	public void forceValues(BitVector values)
	{
		setNewValues(values);
	}

	/**
	 * The {@link Wire} is interpreted as an unsigned integer with n bits.
	 * 
	 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or <code>Bit.ZERO</code> (they do not all have to have the same
	 *         value), not <code>Bit.U</code>, <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is returned otherwise.
	 * 
	 * @author Fabian Stemmler
	 */
	public boolean hasNumericValue()
	{
		return values.isBinary();
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
		long mask = 1 << (width - 1);
		if ((mask & val) != 0)
		{
			int shifts = 64 - width;
			return (val << shifts) >> shifts;
		}
		return val;
	}

	/**
	 * Returns the least significant bit (LSB)
	 */
	public Bit getValue()
	{
		return getValue(0);
	}

	/**
	 * Returns the least significant bit (LSB) of the given index
	 */
	public Bit getValue(int index)
	{
		return values.getLSBit(index);
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
	boolean attachEnd(ReadEnd end)
	{
		return attached.add(end);
	}

	void detachEnd(ReadEnd end)
	{
		attached.remove(end);
	}

	private void notifyObservers()
	{
		attached.forEach(ReadEnd::update);
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
		 * Included for convenient use on {@link Wire}s of width 1.
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

		public int width()
		{
			return width;
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
		private boolean isWriting;
		private BitVector inputValues;

		ReadWriteEnd()
		{
			super();
			open = true;
			isWriting = true;
			initValues();
			registerInput(this);
		}

		private void initValues()
		{
			inputValues = U.toVector(width);
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
			if (newValues.length() != width)
				throw new IllegalArgumentException(
						String.format("Attempted to input %d bits instead of %d bits.", newValues.length(), width));
			if (!open)
				throw new IllegalStateException("Attempted to write to closed WireArrayEnd.");
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
				throw new IllegalStateException("Attempted to write to closed WireArrayEnd.");
			timeline.addEvent(e -> setValues(startingBit, bitVector), travelTime);
		}

		/**
		 * Sets the values that are being fed into the {@link Wire}. The preferred way of setting {@link ReadWriteEnd} values is via
		 * feedValues(...) with a delay.
		 */
		void setValues(int startingBit, BitVector newValues)
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

		/**
		 * Sets the values that are being fed into the {@link Wire}. The preferred way of setting {@link ReadWriteEnd} values is via
		 * feedValues(...) with a delay.
		 */
		void setValues(BitVector newValues)
		{
			if (inputValues.equals(newValues))
				return;
			inputValues = newValues;
			Wire.this.recalculate();
		}

		/**
		 * @return The value (of bit 0) the {@link ReadEnd} is currently feeding into the associated {@link Wire}.Returns the least
		 *         significant bit (LSB)
		 */
		public Bit getInputValue()
		{
			return getInputValue(0);
		}

		/**
		 * @return The value which the {@link ReadEnd} is currently feeding into the associated {@link Wire} at the indexed {@link Bit}.
		 *         Returns the least significant bit (LSB)
		 * 
		 */
		public Bit getInputValue(int index)
		{
			return inputValues.getLSBit(index);
		}

		/**
		 * @return A copy (safe to modify) of the values the {@link ReadEnd} is currently feeding into the associated {@link Wire}.
		 */
		public BitVector getInputValues()
		{
			return inputValues;
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
			feedSignals(Z.toVector(width));
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
			return mutator.toBitVector();
		}

		@Override
		public String toString()
		{
			return inputValues.toString();
		}

		@Override
		public void close()
		{
			super.close();
			open = false;
		}

		void setWriting(boolean isWriting)
		{
			if (this.isWriting != isWriting)
			{
				this.isWriting = isWriting;
				if (isWriting)
					inputs.add(this);
				else
					inputs.remove(this);
				Wire.this.recalculate();
			}
		}

		boolean isWriting()
		{
			return isWriting;
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

	// TODO Fix ReadWriteEnd feeding signals to entire Wire (Z) instead of only selected Bits
	/**
	 * Fuses the selected bits of two wires together. If the bits change in one Wire, the other is changed accordingly immediately. Warning:
	 * The bits are permanently fused together.
	 * 
	 * @param a     The {@link Wire} to be (partially) fused with b
	 * @param b     The {@link Wire} to be (partially) fused with a
	 * @param fromA The first bit of {@link Wire} a to be fused
	 * @param fromB The first bit of {@link Wire} b to be fused
	 * @param width The amount of bits to fuse
	 */
	public static void fuse(Wire a, Wire b, int fromA, int fromB, int width)
	{
		ReadWriteEnd rA = a.createReadWriteEnd(), rB = b.createReadWriteEnd();
		rA.setWriting(false);
		rB.setWriting(false);
		rA.setValues(BitVector.of(Bit.Z, a.width));
		rB.setValues(BitVector.of(Bit.Z, b.width));
		Fusion aF = new Fusion(rB, fromA, fromB, width), bF = new Fusion(rA, fromB, fromA, width);
		rA.registerObserver(aF);
		rB.registerObserver(bF);
		aF.update(rA);
		bF.update(rB);
	}

	/**
	 * 
	 * Fuses two wires together. If the bits change in one Wire, the other is changed accordingly immediately. Warning: The bits are
	 * permanently fused together.
	 * 
	 * @param a The {@link Wire} to be fused with b
	 * @param b The {@link Wire} to be fused with a
	 */
	public static void fuse(Wire a, Wire b)
	{
		fuse(a, b, 0, 0, a.width);
	}

	private static class Fusion implements LogicObserver
	{
		private ReadWriteEnd target;
		int fromSource, fromTarget, width;

		public Fusion(ReadWriteEnd target, int fromSource, int fromTarget, int width)
		{
			this.target = target;
			this.fromSource = fromSource;
			this.fromTarget = fromTarget;
			this.width = width;
		}

		@Override
		public void update(LogicObservable initiator)
		{
			ReadWriteEnd source = (ReadWriteEnd) initiator;
			if (source.getWire().inputs.size() - (source.isWriting() ? 1 : 0) == 0)
				target.setWriting(false);
			else
			{
				target.setWriting(true);
				BitVector targetInput = source.wireValuesExcludingMe().subVector(fromSource, fromSource + width);
				target.setValues(fromTarget, targetInput);
			}
		}
	}
}