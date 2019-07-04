package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.logic.ui.serializing.snippets.outlinerenderers.DefaultOutlineRendererProvider;
import net.mograsim.logic.ui.serializing.snippets.symbolrenderers.DefaultSymbolRendererProvider;
import net.mograsim.logic.ui.util.JsonHandler;

public class CodeSnippetSupplier<S>
{
	// public static members
	public static final CodeSnippetSupplier<RendererProvider> symbolRendererProviderSupplier;
	public static final CodeSnippetSupplier<RendererProvider> outlineRendererProviderSupplier;

	static
	{
		symbolRendererProviderSupplier = new CodeSnippetSupplier<>(new DefaultSymbolRendererProvider());
		outlineRendererProviderSupplier = new CodeSnippetSupplier<>(new DefaultOutlineRendererProvider());
	}

	// per-instance members

	private final Map<String, String> standardSnippetIDClassNames = new HashMap<>();
	private final Map<String, S> snippetProvidersForClassNames = new HashMap<>();
	private final S defaultSnippetProvider;

	private CodeSnippetSupplier(S defaultSnippetProvider)
	{
		this.defaultSnippetProvider = defaultSnippetProvider;
	}

	public void addStandardSnippetID(String standardSnippetID, String associatedSnippetClassName)
	{
		standardSnippetIDClassNames.put(standardSnippetID, associatedSnippetClassName);
	}

	public void setSnippetProvider(String id, S snippetProvider)
	{
		snippetProvidersForClassNames.put(id, snippetProvider);
	}

	// TODO report errors
	public S getSnippetProvider(String id)
	{
		if (id != null)
		{
			String snippetProviderClassName;
			if (id.startsWith("class:"))
				snippetProviderClassName = id.substring(6);
			else
				snippetProviderClassName = standardSnippetIDClassNames.get(id);
			if (snippetProviderClassName != null)
			{
				tryLoadSnippetClass(snippetProviderClassName);
				S snippetProvider = snippetProvidersForClassNames.get(snippetProviderClassName);
				if (snippetProvider != null)
					return snippetProvider;
			}
		}
		System.err.println("Couldn't load snippet " + id + "; using default");
		return defaultSnippetProvider;
	}

	// static helpers

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("./standardSnippetIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			SnippetIDClassNames tmp = JsonHandler.readJson(s, SnippetIDClassNames.class);
			tmp.standardOutlineRendererProviders.forEach(outlineRendererProviderSupplier::addStandardSnippetID);
			tmp.standardSymbolRendererProviders.forEach(symbolRendererProviderSupplier::addStandardSnippetID);
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping: ");
			e.printStackTrace();
		}
	}

	private static class SnippetIDClassNames
	{
		public Map<String, String> standardOutlineRendererProviders;
		public Map<String, String> standardSymbolRendererProviders;
	}

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