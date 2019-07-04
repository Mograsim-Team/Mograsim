package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;

import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.logic.ui.serializing.snippets.outlinerenderers.DefaultOutlineRendererProvider;
import net.mograsim.logic.ui.serializing.snippets.symbolrenderers.DefaultSymbolRendererProvider;
import net.mograsim.logic.ui.util.JsonHandler;

public class CodeSnippetSupplier
{
	private static final Map<String, String> standardSnippetIDClassNames = new HashMap<>();

	private static final Map<String, RendererProvider> outlineRendererProvidersForComponentClassNames = new HashMap<>();
	private static final Map<String, RendererProvider> symbolRendererProvidersForComponentClassNames = new HashMap<>();

	private static final RendererProvider defaultOutlineRendererProvider;
	private static final RendererProvider defaultSymbolRendererProvider;
	static
	{
		defaultOutlineRendererProvider = new DefaultOutlineRendererProvider();
		defaultSymbolRendererProvider = new DefaultSymbolRendererProvider();
	}

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("./standardSnippetIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			Map<String, String> tmp = JsonHandler.readJson(s, Map.class);
			standardSnippetIDClassNames.putAll(tmp);
		}
		catch (IOException e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping: " + e.getMessage());
		}
	}

	public static void addStandardSnippetID(String standardSnippetID, String associatedSnippetClassName)
	{
		standardSnippetIDClassNames.put(standardSnippetID, associatedSnippetClassName);
	}

	public static void setOutlineRendererProvider(String id, RendererProvider outlineRendererProvider)
	{
		outlineRendererProvidersForComponentClassNames.put(id, outlineRendererProvider);
	}

	public static void setSymbolRendererProvider(String id, RendererProvider symbolRendererProvider)
	{
		symbolRendererProvidersForComponentClassNames.put(id, symbolRendererProvider);
	}

	public static Renderer createOutlineRenderer(String id, DeserializedSubmodelComponent component, JsonElement params)
	{
		return getSnippet(id, outlineRendererProvidersForComponentClassNames, defaultOutlineRendererProvider).create(component, params);
	}

	public static Renderer createSymbolRenderer(String id, DeserializedSubmodelComponent component, JsonElement params)
	{
		return getSnippet(id, symbolRendererProvidersForComponentClassNames, defaultSymbolRendererProvider).create(component, params);
	}

	// TODO report errors
	private static <C> C getSnippet(String id, Map<String, C> specializedCodeMap, C defaultSnippet)
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
				C specializedCode = specializedCodeMap.get(snippetClassName);
				if (specializedCode != null)
					return specializedCode;
			}
		}
		System.err.println("Couldn't load snippet " + id + "; using default");
		return defaultSnippet;
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