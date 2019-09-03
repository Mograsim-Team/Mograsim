package net.mograsim.logic.model.snippets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.model.serializing.ReflectionHelper;

public class CodeSnippetSupplier<C, S>
{
	private final Map<String, String> standardSnippetIDClassNames = new HashMap<>();
	private final Map<String, String> standardSnippetIDClassNamesUnmodifiable = Collections.unmodifiableMap(standardSnippetIDClassNames);
	private final Map<String, SnippetDefinintion<C, ?, S>> snippetSuppliersForClassNames = new HashMap<>();
	private final SnippetDefinintion<C, ?, S> defaultSnippetSupplier;

	public CodeSnippetSupplier(SnippetDefinintion<C, ?, S> defaultSnippetSupplier)
	{
		this.defaultSnippetSupplier = defaultSnippetSupplier;
	}

	public void addStandardSnippetID(String standardSnippetID, String associatedSnippetID)
	{
		if (!associatedSnippetID.startsWith("class:"))
			throw new IllegalArgumentException("Unrecognized snippet ID format: " + associatedSnippetID);
		standardSnippetIDClassNames.put(standardSnippetID, associatedSnippetID);
	}

	public Map<String, String> getStandardSnippetIDs()
	{
		return standardSnippetIDClassNamesUnmodifiable;
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
			String resolvedID = resolveID(id);
			if (resolvedID != null)
			{
				String snippetClassName = resolvedID.substring(6);
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

	public String resolveID(String id)
	{
		if (id.startsWith("class:"))
			return id;
		return standardSnippetIDClassNames.get(id);
	}

	// static helpers

	private static void tryLoadSnippetClass(String snippetClassName)
	{
		ReflectionHelper.tryInvokeStaticInitializer(snippetClassName, "Error getting snippet class: %s: %s\n");
	}
}