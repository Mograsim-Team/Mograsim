package net.mograsim.logic.ui.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.util.JsonHandler;

public class IndirectGUIComponentCreator
{
	private static final Map<String, String> standardComponentIDs = new HashMap<>();

	private static final Map<String, ComponentProvider> componentProviders = new HashMap<>();

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("./standardComponentIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			Map<String, String> tmp = JsonHandler.readJson(s, Map.class);
			// don't use putAll to apply sanity checks
			tmp.forEach((st, id) ->
			{
				try
				{
					addStandardComponentID(st, id);
				}
				catch (IllegalArgumentException e)
				{
					System.err.println("Component ID mapping contained illegal entry: " + e.getMessage());
				}
			});
		}
		catch (IOException e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping: " + e.getMessage());
		}
	}

	public static void addStandardComponentID(String standardComponentID, String associatedComponentID)
	{
		if (!associatedComponentID.startsWith("file:") && !associatedComponentID.startsWith("class:"))
			throw new IllegalArgumentException("Unrecognized component ID format: " + associatedComponentID);
		standardComponentIDs.put(standardComponentID, associatedComponentID);
	}

	public static void setComponentProvider(String className, ComponentProvider componentProvider)
	{
		componentProviders.put(className, componentProvider);
	}

	public static GUIComponent createComponent(ViewModelModifiable model, String id, JsonElement params)
	{
		if (id != null)
		{
			String resolvedID;
			if (id.startsWith("class:") || id.startsWith("file:"))
				resolvedID = id;
			else
				resolvedID = standardComponentIDs.get(id);
			if (resolvedID.startsWith("class:"))
			{
				String className = resolvedID.substring(6);
				tryLoadComponentClass(className);
				ComponentProvider componentProvider = componentProviders.get(className);
				if (componentProvider != null)
					return componentProvider.create(model, params);
			} else
				// we know id has to start with "file:" here
				// because standardComponentIDs only contains strings starting with "class:" or "file:"
				return SubmodelComponentDeserializer.create(model, resolvedID.substring(5));
		}
		throw new RuntimeException("Could not get component provider for ID " + id);
	}

	private static void tryLoadComponentClass(String componentClassName)
	{
		CodeSnippetSupplier.tryInvokeStaticInitializer(componentClassName, "Error loading component class %s: %s\n");
	}

	public static interface ComponentProvider
	{
		public GUIComponent create(ViewModelModifiable model, JsonElement params);
	}
}