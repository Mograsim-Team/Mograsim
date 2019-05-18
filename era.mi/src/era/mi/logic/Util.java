package era.mi.logic;

import java.util.Arrays;

public final class Util
{

	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T[]... arrays)
	{
		if (arrays.length == 0)
			throw new IllegalArgumentException("Cannot concatenate 0 arrays.");

		int length = 0;
		for (T[] array : arrays)
			length += array.length;

		T[] newArray = Arrays.copyOf(arrays[0], length);
		int appendIndex = arrays[0].length;
		for (int i = 1; i < arrays.length; i++)
		{
			System.arraycopy(arrays[i], 0, newArray, appendIndex, arrays[i].length);
			appendIndex += arrays[i].length;
		}

		return newArray;
	}

//	@SuppressWarnings("unchecked")
//	public static <T> T[][] split(T[] array, int... lengths)
//	{
//		//TODO: implement array split again; This version contains an illegal cast
//		int totalLength = 0;
//		for(int length : lengths)
//			totalLength += length;
//		
//		if(totalLength != array.length)
//			throw new IllegalArgumentException(); //TODO: add proper error message
//		
//		Object[][] newArray = new Object[lengths.length][];
//		int splitIndex = 0;
//		for(int i = 0; i < lengths.length; i++)
//		{
//			System.arraycopy(array, splitIndex, newArray, 0, lengths[i]);
//			splitIndex += lengths[i];
//		}
//		
//		return (T[][]) newArray;
//	}

	public static Bit[] and(Bit[] a, Bit[] b)
	{
		return binBitOp(a, b, (bA, bB) -> Bit.and(bA, bB));
	}

	public static Bit[] or(Bit[] a, Bit[] b)
	{
		return binBitOp(a, b, (bA, bB) -> Bit.or(bA, bB));
	}

	public static Bit[] xor(Bit[] a, Bit[] b)
	{
		return binBitOp(a, b, (bA, bB) -> Bit.xor(bA, bB));
	}

	private static Bit[] binBitOp(Bit[] a, Bit[] b, BitOp op)
	{
		if (a.length != b.length)
			throw new IllegalArgumentException("Bit Arrays were not of equal length.");
		Bit[] out = new Bit[a.length];
		for (int i = 0; i < a.length; i++)
		{
			out[i] = op.execute(a[i], b[i]);
		}
		return out;
	}

	public static Bit[] not(Bit[] a)
	{
		Bit[] out = new Bit[a.length];
		for (int i = 0; i < a.length; i++)
		{
			out[i] = a[i].not();
		}
		return out;
	}

	/**
	 * uses the {@link Bit#combineWith(Bit)} method, does not create a new array, the result is stored in the first array.
	 * 
	 * @author Christian Femers
	 */
	public static Bit[] combineInto(Bit[] dest, Bit[] addition)
	{
		if (dest.length != addition.length)
			throw new IllegalArgumentException("Bit Arrays were not of equal length.");
		for (int i = 0; i < addition.length; i++)
		{
			dest[i] = dest[i].combineWith(addition[i]);
		}
		return dest;
	}

	interface BitOp
	{
		Bit execute(Bit a, Bit b);
	}
}
