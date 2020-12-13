package net.mograsim.logic.model.verilog.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class IdentifierGenerator
{
	private final Function<String, String> sanitizer;
	private final Set<String> usedIdentifiers;

	public IdentifierGenerator()
	{
		this(Function.identity());
	}

	public IdentifierGenerator(Function<String, String> sanitizer)
	{
		this.usedIdentifiers = new HashSet<>();
		this.sanitizer = sanitizer;
	}

	public IdentifierGenerator(Collection<String> forbiddenIDs)
	{
		this(forbiddenIDs, Function.identity());
	}

	public IdentifierGenerator(Collection<String> forbiddenIDs, Function<String, String> sanitizer)
	{
		this.usedIdentifiers = new HashSet<>(forbiddenIDs);
		this.sanitizer = sanitizer;
	}

	public String generateID(String hint)
	{
		String sanitized = sanitizer.apply(hint);
		if (usedIdentifiers.add(sanitized))
			return sanitized;

		String idBase = sanitized.isEmpty() ? "_" : sanitized;
		for (int i = 0;; i++)
			if (usedIdentifiers.add(idBase + i))
				return idBase + i;
	}
}
