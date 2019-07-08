package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.SnippetSupplier;
import net.mograsim.logic.ui.serializing.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.ui.serializing.snippets.symbolrenderers.DefaultSymbolRenderer;
import net.mograsim.logic.ui.util.JsonHandler;

public class CodeSnippetSupplier<S>
{
	// public static members
	public static final CodeSnippetSupplier<Renderer> symbolRendererSupplier;
	public static final CodeSnippetSupplier<Renderer> outlineRendererSupplier;

	static
	{
		symbolRendererSupplier = new CodeSnippetSupplier<>(SnippetSupplier.create(Void.class, DefaultSymbolRenderer::new));
		outlineRendererSupplier = new CodeSnippetSupplier<>(SnippetSupplier.create(Void.class, DefaultOutlineRenderer::new));
	}

	// per-instance members

	private final Map<String, String> standardSnippetIDClassNames = new HashMap<>();
	private final Map<String, SnippetSupplier<?, S>> snippetSuppliersForClassNames = new HashMap<>();
	private final SnippetSupplier<?, S> defaultSnippetSupplier;

	private CodeSnippetSupplier(SnippetSupplier<?, S> defaultSnippetSupplier)
	{
		this.defaultSnippetSupplier = defaultSnippetSupplier;
	}

	public void addStandardSnippetID(String standardSnippetID, String associatedSnippetClassName)
	{
		standardSnippetIDClassNames.put(standardSnippetID, associatedSnippetClassName);
	}

	public void setSnippetSupplier(String id, SnippetSupplier<?, S> snippetSupplier)
	{
		snippetSuppliersForClassNames.put(id, snippetSupplier);
	}

	// TODO report errors
	public SnippetSupplier<?, S> getSnippetSupplier(String id)
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
				SnippetSupplier<?, S> snippetSupplier = snippetSuppliersForClassNames.get(snippetClassName);
				if (snippetSupplier != null)
					return snippetSupplier;
			}
			System.err.println("Couldn't load snippet " + id + "; using default");
		}
		return defaultSnippetSupplier;
	}

	// static helpers

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("./standardSnippetIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			SnippetIDClassNames tmp = JsonHandler.readJson(s, SnippetIDClassNames.class);
			tmp.standardOutlineRendererSuppliers.forEach(outlineRendererSupplier::addStandardSnippetID);
			tmp.standardSymbolRendererSuppliers.forEach(symbolRendererSupplier::addStandardSnippetID);
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping: ");
			e.printStackTrace();
		}
	}

	private static class SnippetIDClassNames
	{
		public Map<String, String> standardOutlineRendererSuppliers;
		public Map<String, String> standardSymbolRendererSuppliers;
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