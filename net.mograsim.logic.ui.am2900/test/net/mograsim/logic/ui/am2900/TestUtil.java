package net.mograsim.logic.ui.am2900;

public final class TestUtil
{
	private TestUtil()
	{

	}

	/**
	 * Transforms the last four bits of an int to a string that contains the binary ('1' and '0') representation of the 4 bits
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
}
