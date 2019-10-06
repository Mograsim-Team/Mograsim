package net.mograsim.plugin.util;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares Strings respecting integers that appear in the strings with positions in common.<br>
 * Note that 0003 , 03 and 3 are considered to be at the same level; however if there is no further difference, lexicographic ordering is
 * applied to ensure the comparator meets the {@link Comparator contract} (this will sort "foor_02" before "foo_2").
 *
 * @author Christian Femers
 *
 */
public class NumberRespectingStringComparator implements Comparator<String>
{
	public static final NumberRespectingStringComparator CASE_SENSITIVE = new NumberRespectingStringComparator();

	private static final Pattern PATTERN = Pattern.compile("\\d+", Pattern.DOTALL);

	private final Comparator<CharSequence> comp = CharSequence::compare;

	private NumberRespectingStringComparator()
	{
	}

	@Override
	public int compare(String o1, String o2)
	{
		Matcher m1 = PATTERN.matcher(o1);
		Matcher m2 = PATTERN.matcher(o2);
		int pos1 = 0;
		int pos2 = 0;
		while (m1.find() && m2.find() && m1.start() - pos1 == m2.start() - pos2)
		{
			int charsCompRes = comp.compare(o1.subSequence(pos1, m1.start()), o2.subSequence(pos2, m2.start()));
			if (charsCompRes != 0)
				return charsCompRes;

			BigInteger num1 = new BigInteger(m1.group());
			BigInteger num2 = new BigInteger(m2.group());
			int compRes = num1.compareTo(num2);
			if (compRes != 0)
				return compRes;

			pos1 = m1.end();
			pos2 = m2.end();
		}
		int restCompRes = comp.compare(o1.substring(pos1), o2.substring(pos2));
		if (restCompRes != 0)
			return restCompRes;
		return comp.compare(o1, o2);
	}
}
