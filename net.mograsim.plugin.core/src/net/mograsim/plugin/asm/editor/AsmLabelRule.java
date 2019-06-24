package net.mograsim.plugin.asm.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class AsmLabelRule implements IRule
{

	/** The token to be returned when this rule is successful */
	protected IToken fToken;

	/**
	 * Creates a rule which will return the specified token when a numerical sequence is detected.
	 *
	 * @param token the token to be returned
	 */
	public AsmLabelRule(IToken token)
	{
		Assert.isNotNull(token);
		fToken = token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		int c = scanner.read();
		int i = 1;
		if (Character.isJavaIdentifierStart(c))
		{
			do
			{
				c = scanner.read();
				i++;
			} while (Character.isJavaIdentifierPart(c));
			if (c == ':')
				return fToken;
			return abort(scanner, i);
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	private static IToken abort(ICharacterScanner scanner, int i)
	{
		for (int j = 0; j < i; j++)
			scanner.unread();
		return Token.UNDEFINED;
	}
}