package era.mi.logic;

import java.util.Arrays;

/**
 * stdlogic according to IEEE 1164
 */
public enum Bit
{
	U, X, ZERO, ONE, Z;

	public static Bit and(Bit a, Bit b)
	{
		return a.and(b);
	}

	public Bit and(Bit other)
	{
		return fromTable(AND_TABLE, this, other);
	}

	public static Bit or(Bit a, Bit b)
	{
		return a.or(b);
	}

	public Bit or(Bit other)
	{
		return fromTable(OR_TABLE, this, other);
	}

	public static Bit xor(Bit a, Bit b)
	{
		return a.xor(b);
	}

	public Bit xor(Bit other)
	{
		return fromTable(XOR_TABLE, this, other);
	}

	public Bit not()
	{
		switch (this)
		{
		case U:
			return U;
		case ONE:
			return ZERO;
		case ZERO:
			return ONE;
		default:
			return X;
		}
	}

	public Bit[] makeArray(int length)
	{
		Bit[] bits = new Bit[length];
		Arrays.fill(bits, this);
		return bits;
	}

	public Bit combineWith(Bit other)
	{
		return fromTable(JOIN_TABLE, this, other);
	}

	public static Bit combine(Bit a, Bit b)
	{
		return a.combineWith(b);
	}

	private static Bit fromTable(Bit[][] table, Bit a, Bit b)
	{
		return table[a.ordinal()][b.ordinal()];
	}

	// @formatter:off
	private static final Bit[][] JOIN_TABLE = 
		{ { U, U, U,    U,   U    }, 
		  { U, X, X,    X,   X    }, 
		  { U, X, ZERO, X,   ZERO },
		  { U, X, X,    ONE, ONE  }, 
		  { U, X, ZERO, ONE, Z    } };

	private static final Bit[][] AND_TABLE = 
		{ { U,    U,    ZERO, U,    U    }, 
		  { U,    X,    ZERO, X,    X    },
		  { ZERO, ZERO, ZERO, ZERO, ZERO }, 
		  { U,    X,    ZERO, ONE,  X    }, 
		  { U,    X,    ZERO, X,    X    } };

	private static final Bit[][] OR_TABLE =
    	{ { U,   U,   U,    ONE, U    },    
    	  { U,   X,   X,    ONE, X    },    
    	  { U,   X,   ZERO, ONE, X    },    
    	  { ONE, ONE, ONE,  ONE, ONE  },    
    	  { U,   X,   X,    ONE, X    } };
	
	private static final Bit[][] XOR_TABLE =
    	{ { U, U, U,    U,    U },    
    	  { U, X, X,    X,    X },    
    	  { U, X, ZERO, ONE,  X },    
    	  { U, X, ONE,  ZERO, X },    
    	  { U, X, X,    X,    X } }; 
	// @formatter:on
}