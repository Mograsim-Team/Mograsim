package net.mograsim.logic.model.am2900;

public final class TestUtil
{
	private TestUtil()
	{

	}

	/**
	 * Transforms the last four bits of an int to a string that contains the binary ('1' and '0') representation of the 4 bits
	 * 
	 * @author Christian Femers
	 */
	public static String to4bitBin(int x)
	{
		StringBuilder sb = new StringBuilder(4);
		sb.append((x & 0b1000) == 0 ? '0' : '1');
		sb.append((x & 0b0100) == 0 ? '0' : '1');
		sb.append((x & 0b0010) == 0 ? '0' : '1');
		sb.append((x & 0b0001) == 0 ? '0' : '1');
		return sb.toString();
	}

	/**
	 * Transforms the given boolean to a string that contains the binary ('1' and '0') representation of the bit
	 * 
	 * @author Christian Femers
	 */
	public static String to1bitBin(boolean bitIsSet)
	{
		return bitIsSet ? "1" : "0";
	}

	/**
	 * Transforms the given int to a string that contains the binary ('1' and '0') representation of the int. "0" is only returned when the
	 * int is equal to zero.
	 * 
	 * @author Christian Femers
	 */
	public static String to1bitBin(int someInt)
	{
		return someInt != 0 ? "1" : "0";
	}

	/**
	 * Transforms a 4 bit signed integer (-8 to 7) to a int representing the same number. (Adding leading 1-bits if the 4 bit int is
	 * negative)
	 * 
	 * @author Christian Femers
	 */
	public static int signed4ToSigned32(int signed4bit)
	{
		if ((signed4bit & 0b1000) > 0)
			return signed4bit | 0xFF_FF_FF_F0;
		return signed4bit & 0x00_00_00_0F;
	}

	/**
	 * Transforms a 16 bit signed integer (-32768 to 32767 - a short) to a int representing the same number. (Adding leading 1-bits if the
	 * 16 bit int is negative)
	 * 
	 * @author Christian Femers
	 */
	public static int signed16ToSigned32(int signed16bit)
	{
		return (short) signed16bit;
	}

	/**
	 * Transforms the last n bits of an int to a string that contains the binary ('1' and '0') representation of the n bits
	 * 
	 * @author Christian Femers
	 */
	public static String toNbitString(int x, int n)
	{
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
		{
			sb.append((x >> i) & 1);
		}
		return sb.reverse().toString();
	}
}
