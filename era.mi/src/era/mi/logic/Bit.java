package era.mi.logic;

import java.util.Arrays;

public enum Bit
{
	ONE, ZERO, Z, X;

	public static Bit and(Bit a, Bit b)
	{
		return a.and(b);
	}

	public Bit and(Bit other)
	{
		if (this == ZERO || other == ZERO)
			return ZERO;
		else if (this == other && this == ONE)
			return ONE;
		else if (this == X || other == X)
			return X;
		else
			return ZERO;
	}

	public static Bit or(Bit a, Bit b)
	{
		return a.or(b);
	}

	public Bit or(Bit other)
	{
		if (this == ONE || other == ONE)
			return ONE;
		else if (this == other && this == ZERO)
			return ZERO;
		else if (this == X || other == X)
			return X;
		else
			return ZERO;
	}

	public static Bit xor(Bit a, Bit b)
	{
		return a.xor(b);
	}

	public Bit xor(Bit other)
	{
		if(this == X || this == Z || other == X || other == Z)
			return Bit.X;
		else
			return this == other ? ZERO : ONE;
	}

	public Bit not()
	{
		switch (this)
			{
			case ONE:
				return Bit.ZERO;
			case ZERO:
				return Bit.ONE;
			default:
				return Bit.X;
			}
	}
	
	public Bit[] makeArray(int length)
	{
		Bit[] bits = new Bit[length];
		Arrays.fill(bits, this);
		return bits;
	}

	/**
	 * Rules for two bits that get directly connected<br>
	 * <code><table>
	 * <tbody>
	 * <tr><td><td>X<td>0<td>1<td>Z</tr>
	 * <tr><td>X<td>X<td>X<td>X<td>X</tr>
	 * <tr><td>0<td>X<td>0<td>X<td>0</tr>
	 * <tr><td>1<td>X<td>X<td>1<td>1</tr>
	 * <tr><td>Z<td>X<td>0<td>1<td>Z</tr>
	 * </tbody>
	 * </table><code>
	 * 
	 * @return the result according to the table
	 * 
	 * @author Christian Femers
	 */
	public Bit combineWith(Bit other)
	{
		if (this == other)
			return this;
		if (this == X || other == X)
			return X;
		if (other == Z)
			return this;
		if (this == Z)
			return other;
		return X;
	}

	public static Bit combine(Bit a, Bit b)
	{
		return a.combineWith(b);
	}
}
