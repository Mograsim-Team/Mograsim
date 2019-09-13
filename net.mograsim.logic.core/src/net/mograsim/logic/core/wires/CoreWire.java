package net.mograsim.logic.core.wires;

import static net.mograsim.logic.core.types.Bit.U;
import static net.mograsim.logic.core.types.Bit.Z;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class CoreWire
{
	public final String name;
	private BitVector cachedValues;
	public final int travelTime;
	private List<ReadEnd> attached = new ArrayList<>();
	public final int width;
	List<ReadWriteEnd> inputs = new ArrayList<>();
	Timeline timeline;
	private Bit[] bitsWithoutFusions;
	FusedBit[] fusedBits;

	public CoreWire(Timeline timeline, int width, int travelTime)
	{
		this(timeline, width, travelTime, null);
	}

	public CoreWire(Timeline timeline, int width, int travelTime, String name)
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
			for (FusedBit fusion : fusedBits)
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
				FusedBit fusion = fusedBits[i];
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
		bitsWithoutFusions = values.getBits();
		invalidateCachedValuesForAllFusedWires();
		notifyObservers();
	}

	/**
	 * The {@link CoreWire} is interpreted as an unsigned integer with n bits.
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
	 * The {@link CoreWire} is interpreted as an unsigned integer with n bits.
	 * 
	 * @return The unsigned value of the {@link CoreWire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
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
	 * The {@link CoreWire} is interpreted as a signed integer with n bits.
	 * 
	 * @return The signed value of the {@link CoreWire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
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
	 * Adds an {@link LogicObserver}, who will be notified when the value of the {@link CoreWire} is updated.
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
	 * Create and register a {@link ReadWriteEnd} object, which is tied to this {@link CoreWire}. This {@link ReadWriteEnd} can be written
	 * to.
	 */
	public ReadWriteEnd createReadWriteEnd()
	{
		return new ReadWriteEnd();
	}

	/**
	 * Create a {@link ReadEnd} object, which is tied to this {@link CoreWire}. This {@link ReadEnd} cannot be written to.
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
	 * A {@link ReadEnd} feeds a constant signal into the {@link CoreWire} it is tied to. The combination of all inputs determines the
	 * {@link CoreWire}s final value. X dominates all other inputs Z does not affect the final value, unless there are no other inputs than
	 * Z 0 and 1 turn into X when they are mixed
	 * 
	 * @author Fabian Stemmler
	 */
	public class ReadEnd implements LogicObservable
	{
		private List<LogicObserver> observers = new ArrayList<>();

		ReadEnd()
		{
			super();
			CoreWire.this.attachEnd(this);
		}

		public void update()
		{
			notifyObservers();
		}

		/**
		 * Included for convenient use on {@link CoreWire}s of width 1.
		 * 
		 * @return The value of bit 0.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit getValue()
		{
			return CoreWire.this.getValue();
		}

		/**
		 * @param index Index of the requested bit.
		 * @return The value of the indexed bit.
		 * 
		 * @author Fabian Stemmler
		 */
		public Bit getValue(int index)
		{
			return CoreWire.this.getValue(index);
		}

		public BitVector getValues()
		{
			return CoreWire.this.getValues();
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
			return CoreWire.this.getValues(start, end);
		}

		/**
		 * The {@link CoreWire} is interpreted as an unsigned integer with n bits.
		 * 
		 * @return <code>true</code> if all bits are either <code>Bit.ONE</code> or <code>Bit.ZERO</code> (they do not all have to have the
		 *         same value), not <code>Bit.X</code> or <code>Bit.Z</code>. <code>false</code> is returned otherwise.
		 * 
		 * @author Fabian Stemmler
		 */
		public boolean hasNumericValue()
		{
			return CoreWire.this.hasNumericValue();
		}

		/**
		 * The {@link CoreWire} is interpreted as an unsigned integer with n bits.
		 * 
		 * @return The unsigned value of the {@link CoreWire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
		 * 
		 * @author Fabian Stemmler
		 */
		public long getUnsignedValue()
		{
			return CoreWire.this.getUnsignedValue();
		}

		/**
		 * The {@link CoreWire} is interpreted as a signed integer with n bits.
		 * 
		 * @return The signed value of the {@link CoreWire}'s bits, where value 0 corresponds with 2^0, value 1 is 2^1 and so on.
		 * 
		 * @author Fabian Stemmler
		 */
		public long getSignedValue()
		{
			return CoreWire.this.getSignedValue();
		}

		@Override
		public String toString()
		{
			return CoreWire.this.toString();
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

		public CoreWire getWire()
		{
			return CoreWire.this;
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
		 * Sets the wires values. This takes up time, as specified by the {@link CoreWire}s travel time.
		 * 
		 * @param newValues The new values the wires should take on.
		 * 
		 * @author Fabian Stemmler
		 */
		public void feedSignals(Bit... newValues)
		{
			feedSignals(BitVector.of(newValues));
		}

		// TODO what if this is called multiple times at the same simulation time? (happens in component unit tests)
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
		 * Sets values of a subarray of wires. This takes up time, as specified by the {@link CoreWire}s travel time.
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
		 * Sets the values that are being fed into the {@link CoreWire}. The preferred way of setting {@link ReadWriteEnd} values is via
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
				CoreWire.this.recalculateValuesWithoutFusions();
			}
		}

		/**
		 * Sets the values that are being fed into the {@link CoreWire}. The preferred way of setting {@link ReadWriteEnd} values is via
		 * feedValues(...) with a delay.
		 */
		void setValues(BitVector newValues)
		{
			if (inputValues.equals(newValues))
				return;
			inputValues = newValues;
			CoreWire.this.recalculateValuesWithoutFusions();
		}

		/**
		 * @return The value (of bit 0) the {@link ReadEnd} is currently feeding into the associated {@link CoreWire}.Returns the least
		 *         significant bit (LSB)
		 */
		public Bit getInputValue()
		{
			return getInputValue(0);
		}

		/**
		 * @return The value which the {@link ReadEnd} is currently feeding into the associated {@link CoreWire} at the indexed {@link Bit}.
		 *         Returns the least significant bit (LSB)
		 * 
		 */
		public Bit getInputValue(int index)
		{
			return inputValues.getLSBit(index);
		}

		/**
		 * @return A copy (safe to modify) of the values the {@link ReadEnd} is currently feeding into the associated {@link CoreWire}.
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
		 * {@link ReadEnd} now feeds Z into the associated {@link CoreWire}.
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
				CoreWire.this.recalculateValuesWithoutFusions();
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

	public static ReadEnd[] extractEnds(CoreWire[] w)
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
	 * @param a The {@link CoreWire} to be fused with b
	 * @param b The {@link CoreWire} to be fused with a
	 */
	public static void fuse(CoreWire a, CoreWire b)
	{
		fuse(a, b, 0, 0, a.width);
	}

	/**
	 * Fuses the selected bits of two wires together. If the bits change in one Wire, the other is changed accordingly immediately. Warning:
	 * The bits are permanently fused together.
	 * 
	 * @param a     The {@link CoreWire} to be (partially) fused with b
	 * @param b     The {@link CoreWire} to be (partially) fused with a
	 * @param fromA The first bit of {@link CoreWire} a to be fused
	 * @param fromB The first bit of {@link CoreWire} b to be fused
	 * @param width The amount of bits to fuse
	 */
	public static void fuse(CoreWire a, CoreWire b, int fromA, int fromB, int width)
	{
		// iterate in this direction to be fail-fast (rely on the checks in fuse(Wire, Wire, int, int)
		for (int i = width - 1; i >= 0; i--)
			fuse(a, b, fromA + i, fromB + i);
	}

	/**
	 * Fuses one bit of two wires together. If this bit changes in one Wire, the other is changed accordingly immediately. Warning: The bits
	 * are permanently fused together.
	 * 
	 * @param a    The {@link CoreWire} to be (partially) fused with b
	 * @param b    The {@link CoreWire} to be (partially) fused with a
	 * @param bitA The bit of {@link CoreWire} a to be fused
	 * @param bitB The bit of {@link CoreWire} b to be fused
	 */
	public static void fuse(CoreWire a, CoreWire b, int bitA, int bitB)
	{
		if (bitA >= a.width)
			throw new IllegalArgumentException("No bit " + bitA + " in " + a + " (width " + a.width + ")");
		if (bitB >= b.width)
			throw new IllegalArgumentException("No bit " + bitB + " in " + b + " (width " + b.width + ")");
		if (a.fusedBits == null)
			a.fusedBits = new FusedBit[a.width];
		if (b.fusedBits == null)
			b.fusedBits = new FusedBit[b.width];
		FusedBit oldFusionA = a.fusedBits[bitA];
		FusedBit oldFusionB = b.fusedBits[bitB];
		if (oldFusionA == null)
			if (oldFusionB == null)
			{
				FusedBit fusion = new FusedBit();
				fusion.addParticipatingWireBit(a, bitA);
				fusion.addParticipatingWireBit(b, bitB);
			} else
				oldFusionB.addParticipatingWireBit(a, bitA);
		else if (oldFusionB == null)
			oldFusionA.addParticipatingWireBit(b, bitB);
		else
			oldFusionA.mergeOtherIntoThis(oldFusionB);
	}

	private static class FusedBit
	{
		private final Set<WireBit> participatingWireBits;

		public FusedBit()
		{
			this.participatingWireBits = new HashSet<>();
		}

		public void addParticipatingWireBit(CoreWire w, int bit)
		{
			addParticipatingWireBit(new WireBit(w, bit));
		}

		private void addParticipatingWireBit(WireBit wb)
		{
			wb.wire.fusedBits[wb.bit] = this;
			participatingWireBits.add(wb);
			wb.wire.invalidateCachedValuesForAllFusedWires();
		}

		public void mergeOtherIntoThis(FusedBit other)
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
		public final CoreWire wire;
		public final int bit;

		public WireBit(CoreWire wire, int bit)
		{
			this.wire = wire;
			this.bit = bit;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + bit;
			result = prime * result + ((wire == null) ? 0 : wire.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WireBit other = (WireBit) obj;
			if (bit != other.bit)
				return false;
			if (wire == null)
			{
				if (other.wire != null)
					return false;
			} else if (!wire.equals(other.wire))
				return false;
			return true;
		}
	}
}