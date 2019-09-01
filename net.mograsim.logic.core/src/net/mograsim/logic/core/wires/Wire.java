package net.mograsim.logic.core.wires;

import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.Z;

import java.util.ArrayList;
import java.util.Arrays;
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
	private BitVector cachedValues;
	public final int travelTime;
	private List<ReadEnd> attached = new ArrayList<>();
	public final int width;
	List<ReadWriteEnd> inputs = new ArrayList<>();
	Timeline timeline;
	private Bit[] bitsWithoutFusions;
	FusionedBit[] fusedBits;

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
		cachedValues = U.toVector(width);
		bitsWithoutFusions = cachedValues.getBits();
	}

	private void setNewValues(BitVector newValues)
	{
		cachedValues = newValues;
		notifyObservers();
	}

	private void invalidateCachedValuesForAllFusedWires()
	{
		invalidateCachedValues();
		if (fusedBits != null)
			for (FusionedBit fusion : fusedBits)
				if (fusion != null)
					fusion.invalidateCachedValuesForAllParticipatingWires();
	}

	private void invalidateCachedValues()
	{
		cachedValues = null;
		notifyObservers();
	}

	void recalculateValuesWithoutFusions()
	{
		Bit[] bits = new Bit[width];
		if (inputs.isEmpty())
			Arrays.fill(bits, U);
		else
		{
			System.arraycopy(inputs.get(0).getInputValues().getBits(), 0, bits, 0, width);
			for (int i = 1; i < inputs.size(); i++)
				Bit.join(bits, inputs.get(i).getInputValues().getBits());
		}
		bitsWithoutFusions = bits;
		if (fusedBits == null)
			setNewValues(BitVector.of(bits));
		else
			invalidateCachedValuesForAllFusedWires();
	}

	private void recalculatedCachedValues()
	{
		Bit[] bits;
		if (fusedBits == null)
			bits = bitsWithoutFusions;
		else
		{
			bits = new Bit[width];
			for (int i = 0; i < width; i++)
			{
				FusionedBit fusion = fusedBits[i];
				if (fusion == null)
					bits[i] = bitsWithoutFusions[i];
				else
					bits[i] = fusion.getValue();
			}
		}
		cachedValues = BitVector.of(bits);
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
		return getValues().isBinary();
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
		for (Bit bit : getValues())
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
		return getValues().getLSBit(index);
	}

	public BitVector getValues(int start, int end)
	{
		return getValues().subVector(start, end);
	}

	public BitVector getValues()
	{
		if (cachedValues == null)
			recalculatedCachedValues();
		return cachedValues;
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
		recalculateValuesWithoutFusions();
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
			recalculateValuesWithoutFusions();
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

//		void registerCloseObserver(LogicObserver ob)
//		{
//			closeObserver.add(ob);
//		}
//		
//		void deregisterCloseObserver(LogicObserver ob)
//		{
//			closeObserver.remove(ob);
//		}

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
				Wire.this.recalculateValuesWithoutFusions();
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
			Wire.this.recalculateValuesWithoutFusions();
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
			boolean modified = false;
			for (ReadWriteEnd wireEnd : inputs)
			{
				if (wireEnd == this)
					continue;
				modified = true;
				mutator.join(wireEnd.inputValues);
			}
			if (!modified)
				mutator.join(BitVector.of(Bit.Z, width));
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
				Wire.this.recalculateValuesWithoutFusions();
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
		return String.format("wire %s value: %s inputs: %s", name, getValues(), inputs);
	}

	public static ReadEnd[] extractEnds(Wire[] w)
	{
		ReadEnd[] inputs = new ReadEnd[w.length];
		for (int i = 0; i < w.length; i++)
			inputs[i] = w[i].createReadWriteEnd();
		return inputs;
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
		// TODO checks
		for (int i = 0; i < width; i++)
			fuse(a, b, fromA + i, fromB + i);
//		ReadWriteEnd rA = a.createReadWriteEnd(), rB = b.createReadWriteEnd();
//		rA.registerObserver(x -> rB.feedSignals(fromB, rA.wireValuesExcludingMe().subVector(fromA, fromA + width)));
//		rB.registerObserver(x -> rA.feedSignals(fromA, rB.wireValuesExcludingMe().subVector(fromB, fromB + width)));
//
//		rA.setValues(0, BitVector.of(Bit.Z, fromA));
//		rB.setValues(0, BitVector.of(Bit.Z, fromB));
//		rA.setValues(fromA + width, BitVector.of(Bit.Z, a.width - width - fromA));
//		rB.setValues(fromB + width, BitVector.of(Bit.Z, b.width - width - fromB));
//
//		rA.notifyObservers();
//		rB.notifyObservers();
	}

	/**
	 * Fuses one bit of two wires together. If this bit changes in one Wire, the other is changed accordingly immediately. Warning: The bits
	 * are permanently fused together.
	 * 
	 * @param a    The {@link Wire} to be (partially) fused with b
	 * @param b    The {@link Wire} to be (partially) fused with a
	 * @param bitA The bit of {@link Wire} a to be fused
	 * @param bitB The bit of {@link Wire} b to be fused
	 */
	private static void fuse(Wire a, Wire b, int bitA, int bitB)
	{
		if (a.fusedBits == null)
			a.fusedBits = new FusionedBit[a.width];
		if (b.fusedBits == null)
			b.fusedBits = new FusionedBit[b.width];
		FusionedBit oldFusionA = a.fusedBits[bitA];
		FusionedBit oldFusionB = b.fusedBits[bitB];
		if (oldFusionA == null)
			if (oldFusionB == null)
			{
				FusionedBit fusion = new FusionedBit();
				fusion.addParticipatingWireBit(a, bitA);
				fusion.addParticipatingWireBit(b, bitB);
			} else
				oldFusionB.addParticipatingWireBit(a, bitA);
		else if (oldFusionB == null)
			oldFusionA.addParticipatingWireBit(b, bitB);
		else
			oldFusionA.mergeOtherIntoThis(oldFusionB);
	}

	private static class FusionedBit
	{
		private final List<WireBit> participatingWireBits;

		public FusionedBit()
		{
			this.participatingWireBits = new ArrayList<>();
		}

		public void addParticipatingWireBit(Wire w, int bit)
		{
			addParticipatingWireBit(new WireBit(w, bit));
		}

		private void addParticipatingWireBit(WireBit wb)
		{
			wb.wire.fusedBits[wb.bit] = this;
			participatingWireBits.add(wb);
			wb.wire.invalidateCachedValuesForAllFusedWires();
		}

		public void mergeOtherIntoThis(FusionedBit other)
		{
			for (WireBit wb : other.participatingWireBits)
				addParticipatingWireBit(wb);
		}

		public void invalidateCachedValuesForAllParticipatingWires()
		{
			for (WireBit wb : participatingWireBits)
				wb.wire.invalidateCachedValues();
		}

		public Bit getValue()
		{
			if (participatingWireBits.isEmpty())
				return Bit.U;
			Bit result = null;
			for (WireBit wb : participatingWireBits)
				if (!wb.wire.inputs.isEmpty())
				{
					Bit bit = wb.wire.bitsWithoutFusions[wb.bit];
					result = result == null ? bit : result.join(bit);
				}
			return result == null ? U : result;
		}
	}

	private static class WireBit
	{
		public final Wire wire;
		public final int bit;

		public WireBit(Wire wire, int bit)
		{
			this.wire = wire;
			this.bit = bit;
		}
	}
}