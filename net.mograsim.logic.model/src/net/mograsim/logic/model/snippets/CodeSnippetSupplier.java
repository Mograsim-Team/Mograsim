package net.mograsim.logic.model.snippets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CodeSnippetSupplier<C, S>
{
	private final Map<String, String> standardSnippetIDClassNames = new HashMap<>();
	private final Set<String> standardSnippetIDSetUnmodifiable = Collections.unmodifiableSet(standardSnippetIDClassNames.keySet());
	private final Map<String, SnippetDefinintion<C, ?, S>> snippetSuppliersForClassNames = new HashMap<>();
	private final SnippetDefinintion<C, ?, S> defaultSnippetSupplier;

	public CodeSnippetSupplier(SnippetDefinintion<C, ?, S> defaultSnippetSupplier)
	{
		this.defaultSnippetSupplier = defaultSnippetSupplier;
	}

	public void addStandardSnippetID(String standardSnippetID, String associatedSnippetClassName)
	{
		standardSnippetIDClassNames.put(standardSnippetID, associatedSnippetClassName);
	}

	public Set<String> getStandardSnippetIDs()
	{
		return standardSnippetIDSetUnmodifiable;
	}

	public void setSnippetSupplier(String id, SnippetDefinintion<C, ?, S> snippetSupplier)
	{
		snippetSuppliersForClassNames.put(id, snippetSupplier);
	}

	// TODO report errors
	public SnippetDefinintion<C, ?, S> getSnippetSupplier(String id)
	{
		if (id != null)
		{
			String snippetClassName;
			if (id.startsWith("class:"))
				snippetClassName = id.substring(6);
			else
				snippetClassName = standardSnippetIDClassNames.get(id);
			if (snippetClassName != null)
			{
				tryLoadSnippetClass(snippetClassName);
				SnippetDefinintion<C, ?, S> snippetSupplier = snippetSuppliersForClassNames.get(snippetClassName);
				if (snippetSupplier != null)
					return snippetSupplier;
			}
			System.err.println("Couldn't load snippet " + id + "; using default");
		}
		if (defaultSnippetSupplier == null)
			throw new IllegalArgumentException("No default snippet set");
		return defaultSnippetSupplier;
	}

	// static helpers

	private static void tryLoadSnippetClass(String snippetClassName)
	{
		tryInvokeStaticInitializer(snippetClassName, "Error getting snippet class: %s: %s\n");
	}

	public static void tryInvokeStaticInitializer(String className, String errorMessageFormat)
	{
		try
		{
			Class.forName(className, true, CodeSnippetSupplier.class.getClassLoader());
		}
		catch (ClassNotFoundException e)
		{
			System.err.printf(errorMessageFormat, className, "ClassNotFoundException thrown: " + e.getMessage());
		}
	}

}