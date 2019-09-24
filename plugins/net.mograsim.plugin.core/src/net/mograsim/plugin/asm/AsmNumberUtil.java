package net.mograsim.plugin.asm;

import static java.lang.String.format;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AsmNumberUtil
{

	private AsmNumberUtil()
	{

	}

	private static final String sgnPat = "([-+]?)";
	private static final String binPat = "((?:[01]+_)*[01]+)";
	private static final String octPat = "((?:[0-7]+_)*[0-7]+)";
	private static final String decPat = "((?:[0-9]+_)*[0-9]+)";
	private static final String hexPat = "((?:[0-9a-f]+_)*[0-9a-f]+)";

	private static final String preferredBinPat = "%1$s0b%2$s";
	private static final String preferredOctPat = "%s%sq";
	private static final String preferredDecPat = "%s%s";
	private static final String preferredHexPat = "%1$s0x%2$s";

	static final Pattern numberBin = Pattern.compile(format(preferredBinPat + "|%1$s%2$sb", sgnPat, binPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberOct = Pattern.compile(format(preferredOctPat, sgnPat, octPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberDec = Pattern.compile(format(preferredDecPat, sgnPat, decPat));
	static final Pattern numberHex = Pattern.compile(format(preferredHexPat + "|%1$s%2$sh", sgnPat, hexPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberFloat = Pattern.compile(format("%1$s%2$s(?:\\.%2$s)?(?:e%1$s%2$s)?", sgnPat, decPat),
			Pattern.CASE_INSENSITIVE);

	public static boolean isPrefix(CharSequence cs, Pattern p)
	{
		Matcher m = p.matcher(cs);
		return m.matches() || m.hitEnd();
	}

	public static boolean isBinary(CharSequence cs)
	{
		return numberBin.matcher(cs).matches();
	}

	public static boolean isOctal(CharSequence cs)
	{
		return numberOct.matcher(cs).matches();
	}

	public static boolean isDecimal(CharSequence cs)
	{
		return numberDec.matcher(cs).matches();
	}

	public static boolean isHexadecimal(CharSequence cs)
	{
		return numberHex.matcher(cs).matches();
	}

	public static boolean isFloatingPoint(CharSequence cs)
	{
		return numberFloat.matcher(cs).matches();
	}

	public static boolean isNumber(CharSequence cs)
	{
		return getType(cs) != NumberType.NONE;
	}

	public static boolean quickCheckForNumber(CharSequence cs)
	{
		if (cs.length() == 0 || !isStart(cs.charAt(0)))
			return false;
		return cs.length() == 1 || isPart(cs.charAt(1));
	}

	public static boolean isStart(int c)
	{
		return isDigit(c) || c == '+' || c == '-';
	}

	public static boolean isDigit(int c)
	{
		return ('0' <= c && c <= '9') || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f');
	}

	public static boolean isPart(int c)
	{
		return isDigit(c) || isMarker(Character.toLowerCase(c));
	}

	public static boolean isMarker(int lowerCase)
	{
		switch (lowerCase)
		{
		case '.':
		case '_':
		case '+':
		case '-':
		case 'e':
		case 'x':
		case 'b':
		case 'q':
		case 'h':
			return true;
		default:
			return false;
		}
	}

	public static NumberType getType(CharSequence cs)
	{
		if (!quickCheckForNumber(cs))
			return NumberType.NONE;
		if (isDecimal(cs))
			return NumberType.DECIMAL;
		if (isHexadecimal(cs))
			return NumberType.HEXADECIMAL;
		if (isBinary(cs))
			return NumberType.BINARY;
		if (isOctal(cs))
			return NumberType.OCTAL;
		if (isFloatingPoint(cs))
			return NumberType.FLOATINGPOINT;
		return NumberType.NONE;
	}

	/**
	 * Checks if the {@link CharSequence} is a prefix of a valid number, and returns the NumberType it is a prefix of. If a String is a
	 * prefix of multiple different {@link NumberType}s, the one with the smallest radix is returned. If no valid {@link NumberType} is
	 * found, {@link NumberType#NONE} is returned.
	 * 
	 * @param cs The potential prefix
	 * @return The type the {@link CharSequence} is a prefix of
	 */
	public static NumberType prefixOfType(CharSequence cs)
	{
		if (isPrefix(cs, numberBin))
			return NumberType.BINARY;
		if (isPrefix(cs, numberOct))
			return NumberType.OCTAL;
		if (isPrefix(cs, numberDec))
			return NumberType.DECIMAL;
		if (isPrefix(cs, numberHex))
			return NumberType.HEXADECIMAL;
		if (isPrefix(cs, numberFloat))
			return NumberType.FLOATINGPOINT;
		return NumberType.NONE;
	}

	private static CharSequence extractSignAndDigits(NumberType type, CharSequence cs)
	{
		return type.numberPattern.matcher(cs).replaceAll("$1$2").replaceAll("_", "");
	}

	/**
	 * Computes the {@link BigInteger} value of a {@link CharSequence}, by determining the {@link NumberType} and parsing the number with
	 * the determined radix.
	 * 
	 * @throws NumberFormatException if the {@link CharSequence} does not match any of the expected number patterns
	 */
	public static BigInteger valueOf(CharSequence cs)
	{
		NumberType type = getType(cs);
		if (NumberType.NONE.equals(type))
			throw new NumberFormatException();
		return new BigInteger(extractSignAndDigits(type, cs).toString(), type.radix);
	}

	/**
	 * Formats a {@link BigInteger} in accordance with the pattern associated with the supplied {@link NumberType}
	 */
	public static String toString(BigInteger number, NumberType type)
	{
		return toString(number, type, 0);
	}

	/**
	 * Formats a {@link BigInteger} in accordance with the pattern associated with the supplied {@link NumberType}
	 */
	public static String toString(BigInteger number, NumberType type, int minBits)
	{
		String pattern;
		switch (type)
		{
		case BINARY:
			pattern = preferredBinPat;
			break;
		case OCTAL:
			pattern = preferredOctPat;
			break;
		default:
		case DECIMAL:
			pattern = preferredDecPat;
			break;
		case HEXADECIMAL:
			pattern = preferredHexPat;
		}

		int digits;

		switch (type.radix)
		{
		case 2:
			digits = minBits;
			break;
		case 8:
			digits = minBits / 3;
			break;
		case 16:
			digits = minBits / 4;
			break;
		default:
			digits = 0;
		}

		String numberString, sign;
		if (number.signum() < 0)
		{
			sign = "-";
			numberString = number.abs().toString(type.radix);
		} else
		{
			sign = "";
			numberString = number.toString(type.radix);
		}
		numberString = "0".repeat(Integer.max(0, digits - numberString.length())) + numberString;
		return String.format(pattern, sign, numberString);
	}

	public enum NumberType
	{
		NONE(-1, null), BINARY(2, AsmNumberUtil.numberBin), OCTAL(8, AsmNumberUtil.numberOct), DECIMAL(10, AsmNumberUtil.numberDec),
		HEXADECIMAL(16, AsmNumberUtil.numberHex), FLOATINGPOINT(10, AsmNumberUtil.numberFloat);

		public final int radix;
		final Pattern numberPattern;

		NumberType(int radix, Pattern numberPattern)
		{
			this.radix = radix;
			this.numberPattern = numberPattern;
		}
	}
}
