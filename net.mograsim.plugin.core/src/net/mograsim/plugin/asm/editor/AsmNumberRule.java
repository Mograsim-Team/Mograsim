package net.mograsim.plugin.asm.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import net.mograsim.plugin.asm.AsmNumberUtil;

public class AsmNumberRule implements IRule
{

	/** The token to be returned when this rule is successful */
	protected IToken fToken;

	/**
	 * Creates a rule which will return the specified token when a numerical sequence is detected.
	 *
	 * @param token the token to be returned
	 */
	public AsmNumberRule(IToken token)
	{
		Assert.isNotNull(token);
		fToken = token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		int i = 1;
		int c = scanner.read();
		if (!AsmNumberUtil.isStart(c))
			return abort(scanner, i);
		StringBuilder sb = new StringBuilder();
		sb.appendCodePoint(c);
		while (true)
		{
			c = scanner.read();
			i++;
			if (AsmNumberUtil.isPart(c))
				sb.appendCodePoint(c);
			else
				break;
		}
		if (!AsmNumberUtil.isNumber(sb))
			return abort(scanner, i);
		scanner.unread();
		return fToken;
	}

	private static IToken abort(ICharacterScanner scanner, int i)
	{
		for (int j = 0; j < i; j++)
			scanner.unread();
		return Token.UNDEFINED;
	}

}
