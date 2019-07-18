package net.mograsim.logic.core.types;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Immutable class representing a {@link Bit}Vector
 *
 * @author Christian Femers
 *
 */
public final class BitVector implements StrictLogicType<BitVector>, Iterable<Bit>, RandomAccess
{
	public static final BitVector SINGLE_U = new BitVector(Bit.U);
	public static final BitVector SINGLE_X = new BitVector(Bit.X);
	public static final BitVector SINGLE_0 = new BitVector(Bit.ZERO);
	public static final BitVector SINGLE_1 = new BitVector(Bit.ONE);
	public static final BitVector SINGLE_Z = new BitVector(Bit.Z);

	private static final BitVector[] SINGLE_BIT_MAPPING = { SINGLE_U, SINGLE_X, SINGLE_0, SINGLE_1, SINGLE_Z };

	private final Bit[] bits;

	private BitVector(Bit single)
	{
		Objects.requireNonNull(single);
		bits = new Bit[] { single };
	}

	private BitVector(Bit[] bits)
	{
		this.bits = Objects.requireNonNull(bits); // do this first to "catch" bits==null before the foreach loop
		for (Bit bit : bits)
			if (bit == null)
				throw new NullPointerException();
	}

	public static BitVector of(Bit... bits)
	{
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].ordinal()];
		return new BitVector(bits.clone());
	}

	public static BitVector of(Bit bit, int length)
	{
		if (length == 1)
			return SINGLE_BIT_MAPPING[bit.ordinal()];
		return new BitVector(bit.makeArray(length));
	}

	public BitVectorMutator mutator()
	{
		return BitVectorMutator.of(this);
	}

	/**
	 * Returns the most significant bit at <code>bitIndex</code>. (leftmost bit of a binary number at the given index)
	 */
	public Bit getMSBit(int bitIndex)
	{
		return bits[bitIndex];
	}

	/**
	 * Returns the least significant bit at <code>bitIndex</code>. (rightmost bit of a binary number at the given index)
	 */
	public Bit getLSBit(int bitIndex)
	{
		return bits[bits.length - bitIndex - 1];
	}

	public Bit[] getBits()
	{
		return bits.clone();
	}

	@Override
	public BitVector join(BitVector t)
	{
		checkCompatibility(t);
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].join(t.bits[0]).ordinal()];
		return new BitVector(binOp(bits.clone(), t.bits, Bit::join));
	}

	@Override
	public BitVector and(BitVector t)
	{
		checkCompatibility(t);
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].and(t.bits[0]).ordinal()];
		return new BitVector(binOp(bits.clone(), t.bits, Bit::and));
	}

	@Override
	public BitVector or(BitVector t)
	{
		checkCompatibility(t);
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].or(t.bits[0]).ordinal()];
		return new BitVector(binOp(bits.clone(), t.bits, Bit::or));
	}

	@Override
	public BitVector xor(BitVector t)
	{
		checkCompatibility(t);
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].xor(t.bits[0]).ordinal()];
		return new BitVector(binOp(bits.clone(), t.bits, Bit::xor));
	}

	@Override
	public BitVector not()
	{
		if (bits.length == 1)
			return SINGLE_BIT_MAPPING[bits[0].not().ordinal()];
		return new BitVector(unOp(bits.clone(), Bit::not));
	}

	public int length()
	{
		return bits.length;
	}

	public BitVector concat(BitVector other)
	{
		Bit[] newBits = Arrays.copyOf(bits, length() + other.length());
		System.arraycopy(other.bits, 0, newBits, length(), other.length());
		return new BitVector(newBits);
	}

	public BitVector subVector(int start)
	{
		return new BitVector(Arrays.copyOfRange(bits, start, length()));
	}

	public BitVector subVector(int start, int end)
	{
		return new BitVector(Arrays.copyOfRange(bits, start, end));
	}

	private void checkCompatibility(BitVector bv)
	{
		if (length() != bv.length())
			throw new IllegalArgumentException(format("BitVector length does not match: %d and %d", length(), bv.length()));
	}

	static Bit[] binOp(Bit[] dest, Bit[] second, BinaryOperator<Bit> op)
	{
		if (dest == null)
			return second.clone();
		for (int i = 0; i < dest.length; i++)
		{
			dest[i] = op.apply(dest[i], second[i]);
		}
		return dest;
	}

	static Bit[] unOp(Bit[] dest, UnaryOperator<Bit> op)
	{
		if (dest == null)
			return null;
		for (int i = 0; i < dest.length; i++)
		{
			dest[i] = op.apply(dest[i]);
		}
		return dest;
	}

	/**
	 * Class for comfortable and efficient manipulation of {@link BitVector}s, similar to {@link StringBuilder}
	 *
	 * @author Christian Femers
	 */
	public static final class BitVectorMutator implements LogicType<BitVectorMutator, BitVector>
	{
		private Bit[] bits;

		private BitVectorMutator(Bit[] bits)
		{
			this.bits = bits;
		}

		static BitVectorMutator of(BitVector bv)
		{
			return new BitVectorMutator(bv.getBits());
		}

		/**
		 * Returns a new mutator of the specified length, <b>with all bits set to <code>null</code></b>. Use with care!
		 */
		public static BitVectorMutator ofLength(int length)
		{
			return new BitVectorMutator(new Bit[length]);
		}

		/**
		 * Returns an empty mutator which has no bits set and will simply copy the values from the first binary operation performed.
		 */
		public static BitVectorMutator empty()
		{
			return new BitVectorMutator(null);
		}

		/**
		 * Produces the resulting, immutable {@link BitVector}<br>
		 * 
		 * @throws IllegalStateException if the mutator is (still) empty
		 */
		public BitVector toBitVector()
		{
			if (bits == null)
				throw new IllegalStateException("cannot create a BitVector from an empty mutator");
			return new BitVector(bits);
		}

		@Override
		public BitVectorMutator join(BitVector t)
		{
			checkCompatibility(t);
			bits = binOp(bits, t.bits, Bit::join);
			return this;
		}

		@Override
		public BitVectorMutator and(BitVector t)
		{
			checkCompatibility(t);
			bits = binOp(bits, t.bits, Bit::and);
			return this;
		}

		@Override
		public BitVectorMutator or(BitVector t)
		{
			checkCompatibility(t);
			bits = binOp(bits, t.bits, Bit::or);
			return this;
		}

		@Override
		public BitVectorMutator xor(BitVector t)
		{
			checkCompatibility(t);
			bits = binOp(bits, t.bits, Bit::xor);
			return this;
		}

		@Override
		public BitVectorMutator not()
		{
			unOp(bits, Bit::not);
			return this;
		}

		/**
		 * Set the most significant bit at <code>bitIndex</code>. (leftmost bit of a binary number at the given index)
		 */
		public void setMSBit(int bitIndex, Bit bit)
		{
			bits[bitIndex] = bit;
		}

		/**
		 * Set the least significant bit at <code>bitIndex</code>. (rightmost bit of a binary number at the given index)
		 */
		public void setLSBit(int bitIndex, Bit bit)
		{
			bits[bits.length - bitIndex - 1] = bit;
		}

		/**
		 * Returns the most significant bit at <code>bitIndex</code>. (leftmost bit of a binary number at the given index)
		 */
		public Bit getMSBit(int bitIndex)
		{
			return bits[bitIndex];
		}

		/**
		 * Returns the least significant bit at <code>bitIndex</code>. (rightmost bit of a binary number at the given index)
		 */
		public Bit getLSBit(int bitIndex)
		{
			return bits[bits.length - bitIndex - 1];
		}

		public int length()
		{
			return bits.length;
		}

		private void checkCompatibility(BitVector bv)
		{
			if (bits != null && bits.length != bv.length())
				throw new IllegalArgumentException(format("BitVector length does not match: %d and %d", bits.length, bv.length()));
		}
	}

	/**
	 * @see Arrays#hashCode(Object[])
	 */
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(bits);
	}

	/**
	 * Does test for equality of values/content
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof BitVector))
			return false;
		BitVector other = (BitVector) obj;
		return Arrays.equals(bits, other.bits);
	}

	/**
	 * Does test for equality of values/content, shifting the other BitVector by <code>offset</code> to the right.<br>
	 * Therefore <code>offset + other.length() <= this.length()</code> needs to be true.
	 * 
	 * @throws ArrayIndexOutOfBoundsException if <code>offset + other.length() > this.length()</code>
	 * 
	 * @see Object#equals(Object)
	 */
	public boolean equalsWithOffset(BitVector other, int offset)
	{
		if (other == null)
			return false;
		return Arrays.equals(bits, offset, offset + other.length(), other.bits, 0, other.length());
	}

	/**
	 * All {@link Bit}s symbols concatenated together (MSB first)
	 * 
	 * @see #parse(String)
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(bits.length);
		for (Bit bit : bits)
			sb.append(bit);
		return sb.toString();
	}

	/**
	 * Parses a String containing solely {@link Bit} symbols (MSB first)
	 * 
	 * @see #toString()
	 */
	public static BitVector parse(String s)
	{
		Bit[] values = new Bit[s.length()];
		for (int i = 0; i < s.length(); i++)
		{
			values[i] = Bit.parse(s, i);
		}
		return new BitVector(values);
	}

	/**
	 * Iterate over the {@link Bit}s of the BitVector <b>from MSB to LSB</b> (left to right).
	 */
	@Override
	public Iterator<Bit> iterator()
	{
		return new Iterator<>()
		{
			private int pos = 0;

			@Override
			public Bit next()
			{
				if (!hasNext())
					throw new NoSuchElementException();
				return getMSBit(pos++);
			}

			@Override
			public boolean hasNext()
			{
				return pos != length();
			}
		};
	}
}
