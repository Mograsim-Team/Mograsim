package net.mograsim.plugin.asm;

import static java.lang.String.format;

import java.math.BigInteger;
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
	static final Pattern numberBin = Pattern.compile(format("%1$s0b%2$s|%1$s%2$sb", sgnPat, binPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberOct = Pattern.compile(format("%s%sq", sgnPat, octPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberDec = Pattern.compile(format("%s%s", sgnPat, decPat));
	static final Pattern numberHex = Pattern.compile(format("%1$s0x%2$s|%1$s%2$sh", sgnPat, hexPat), Pattern.CASE_INSENSITIVE);
	static final Pattern numberFloat = Pattern.compile(format("%1$s%2$s(?:\\.%2$s)?(?:e%1$s%2$s)?", sgnPat, decPat),
			Pattern.CASE_INSENSITIVE);

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

	private static CharSequence extractSignAndDigits(NumberType type, CharSequence cs)
	{
		return type.numberPattern.matcher(cs).replaceAll("$1$2").replaceAll("_", "");
	}

	public static BigInteger valueOf(CharSequence cs)
	{
		NumberType type = getType(cs);
		if (NumberType.NONE.equals(type))
			throw new NumberFormatException();
		return new BigInteger(extractSignAndDigits(type, cs).toString(), type.radix);
	}

	public enum NumberType
	{
		NONE(-1, null), BINARY(2, AsmNumberUtil.numberBin), OCTAL(8, AsmNumberUtil.numberOct), DECIMAL(10, AsmNumberUtil.numberDec),
		HEXADECIMAL(16, AsmNumberUtil.numberHex), FLOATINGPOINT(10, AsmNumberUtil.numberFloat);

		public final int radix;
		private final Pattern numberPattern;

		NumberType(int radix, Pattern numberPattern)
		{
			this.radix = radix;
			this.numberPattern = numberPattern;
		}
	}
}
