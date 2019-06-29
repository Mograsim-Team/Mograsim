package net.mograsim.logic.core.types;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * stdlogic according to IEEE 1164
 */
public enum Bit implements StrictLogicType<Bit>
{
	U("U"), X("X"), ZERO("0"), ONE("1"), Z("Z");

	private final String symbol;

	private Bit(String symbol)
	{
		this.symbol = symbol;
	}

	public boolean isBinary()
	{
		return this == ONE || this == ZERO;
	}

	@Override
	public Bit and(Bit other)
	{
		return fromTable(AND_TABLE, this, other);
	}

	@Override
	public Bit or(Bit other)
	{
		return fromTable(OR_TABLE, this, other);
	}

	@Override
	public Bit xor(Bit other)
	{
		return fromTable(XOR_TABLE, this, other);
	}

	@Override
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

	public BitVector toVector(int length)
	{
		return BitVector.of(this, length);
	}

	@Override
	public Bit join(Bit other)
	{
		return fromTable(JOIN_TABLE, this, other);
	}

	@Override
	public String toString()
	{
		return getSymbol();
	}

	public String getSymbol()
	{
		return symbol;
	}

	public static Bit lastBitOf(int value)
	{
		return values()[2 + (value & 1)];
	}

	public static Bit parse(String s)
	{
		Bit bit = SYMBOL_MAP.get(s);
		Objects.requireNonNull(bit, "No Bit found for symbol " + s);
		return bit;
	}

	public static Bit parse(String s, int symbolPosition)
	{
		return parse(s.substring(symbolPosition, symbolPosition + 1));
	}

	private static Bit fromTable(Bit[][] table, Bit a, Bit b)
	{
		return table[a.ordinal()][b.ordinal()];
	}

	static final Map<String, Bit> SYMBOL_MAP = Map.of(U.symbol, U, X.symbol, X, ZERO.symbol, ZERO, ONE.symbol, ONE, Z.symbol, Z);

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