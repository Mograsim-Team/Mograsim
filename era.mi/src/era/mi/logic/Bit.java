package era.mi.logic;


public enum Bit
{
	ONE, ZERO, Z, X;
	
	public static Bit and(Bit a, Bit b)
	{
		return a.and(b);
	}
	
	public Bit and(Bit other)
	{
		if(equals(Bit.ZERO) || other.equals(Bit.ZERO))
			return Bit.ZERO;
		else if(equals(other) && equals(Bit.ONE))
			return Bit.ONE;
		else
			return Bit.X;
	}
	
	public static Bit or(Bit a, Bit b)
	{
		return a.or(b);
	}
	
	public Bit or(Bit other)
	{
		if(equals(Bit.ONE) || other.equals(Bit.ONE))
			return Bit.ONE;
		else if(equals(other) && equals(Bit.ZERO))
			return Bit.ZERO;
		else
			return Bit.X;
	}
	
	public static Bit xor(Bit a, Bit b)
	{
		return a.xor(b);
	}
	
	public Bit xor(Bit other)
	{
		//I'm uncertain how this should behave for cases where one value is neither 1 nor 0.
		//TODO: Implement xor
		return Bit.X;
	}
	
	public Bit not()
	{
		switch(this)
		{
		case ONE:
			return Bit.ZERO;
		case ZERO:
			return Bit.ONE;
		default:
			return Bit.X;
		}
	}
}
