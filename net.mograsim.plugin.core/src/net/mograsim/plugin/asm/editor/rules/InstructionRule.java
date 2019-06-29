package net.mograsim.plugin.asm.editor.rules;

import java.util.Set;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

import net.mograsim.plugin.AsmOps;

public class InstructionRule extends WordRule
{
	public InstructionRule(IToken defaultToken)
	{
		this(defaultToken, false);
	}

	public InstructionRule(IToken defaultToken, boolean ignoreCase)
	{
		super(new InstructionDetector(), defaultToken, ignoreCase);
		AsmOps.addListener(this::update);
	}

	void update(Set<String> words)
	{
		fWords.clear();
		words.forEach(s -> fWords.put(s, fDefaultToken));
	}

	static class InstructionDetector implements IWordDetector
	{
		@Override
		public boolean isWordStart(char c)
		{
			return Character.isJavaIdentifierStart(c);
		}

		@Override
		public boolean isWordPart(char c)
		{
			return Character.isJavaIdentifierPart(c);
		}
	}
}
