package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.serializing.snippets.Renderer;
import net.mograsim.logic.ui.serializing.snippets.RendererProvider;
import net.mograsim.logic.ui.util.JsonHandler;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class CodeSnippetSupplier
{
	private static final Map<String, String> standardSnippetIDClassNames = new HashMap<>();

	private static final Map<String, RendererProvider> outlineRendererProvidersForComponentClassNames = new HashMap<>();
	private static final Map<String, RendererProvider> symbolRendererProvidersForComponentClassNames = new HashMap<>();

	private static final RendererProvider defaultOutlineRendererProvider;
	private static final RendererProvider defaultSymbolRendererProvider;
	static
	{
		// TODO this code does not belong here
		defaultOutlineRendererProvider = (comp, params) -> (gc, visReg) ->
		{
			ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.foreground");
			if (fg != null)
				gc.setForeground(ColorManager.current().toColor(fg));
			gc.drawRectangle(comp.getBounds());
		};
		defaultSymbolRendererProvider = (comp, params) -> (gc, visReg) ->
		{
			ColorDefinition fg = Preferences.current().getColorDefinition("net.mograsim.logic.ui.color.text");
			if (fg != null)
				gc.setForeground(ColorManager.current().toColor(fg));
			String id = "TODO";// TODO add an ID of sorts to DeserializedSubmodelComponent
			Point idSize = gc.textExtent(id);
			Rectangle bounds = comp.getBounds();
			gc.drawText(id, bounds.x + (bounds.width - idSize.x) / 2, bounds.y + (bounds.height - idSize.y) / 2, true);
		};
	}

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("./mapping.json"))
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
		return defaultSnippet;
	}

	private static void tryLoadSnippetClass(String snippetClassName)
	{
		tryLoadClass(snippetClassName, "Error getting snippet code for component class: %s\n");
	}

	public static void tryLoadClass(String className, String errorMessageFormat)
	{
		try
		{
			CodeSnippetSupplier.class.getClassLoader().loadClass(className);
		}
		catch (@SuppressWarnings("unused") ClassNotFoundException e)
		{
			System.err.printf(errorMessageFormat, className);
		}
	}
}