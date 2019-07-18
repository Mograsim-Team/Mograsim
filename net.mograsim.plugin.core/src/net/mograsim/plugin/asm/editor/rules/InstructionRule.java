package net.mograsim.plugin.asm.editor.rules;

import java.util.Objects;
import java.util.Set;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

import net.mograsim.plugin.AsmOps;

public class InstructionRule extends WordRule
{
	private final IToken instToken;

	public InstructionRule(IToken defaultToken, IToken instToken)
	{
		this(defaultToken, instToken, false);
	}

	public InstructionRule(IToken defaultToken, IToken instToken, boolean ignoreCase)
	{
		super(new InstructionDetector(), Objects.requireNonNull(defaultToken), ignoreCase);
		this.instToken = Objects.requireNonNull(instToken);
		AsmOps.addListener(this::update);
	}

	void update(Set<String> words)
	{
		fWords.clear();
		words.forEach(s -> fWords.put(s, instToken));
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
