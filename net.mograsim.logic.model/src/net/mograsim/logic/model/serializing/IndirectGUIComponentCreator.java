package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.snippets.CodeSnippetSupplier;
import net.mograsim.logic.model.util.JsonHandler;

public class IndirectGUIComponentCreator
{
	private static final Map<String, String> standardComponentIDs = new HashMap<>();
	private static final Set<String> standardComponentIDSetUnmodifiable = Collections.unmodifiableSet(standardComponentIDs.keySet());

	private static final Map<String, ComponentSupplier> componentSuppliers = new HashMap<>();

	static
	{
		try (InputStream s = IndirectGUIComponentCreator.class.getResourceAsStream("standardComponentIDMapping.json"))
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

	public static Set<String> getStandardComponentIDs()
	{
		return standardComponentIDSetUnmodifiable;
	}

	public static void setComponentSupplier(String className, ComponentSupplier componentSupplier)
	{
		componentSuppliers.put(className, componentSupplier);
	}

	public static GUIComponent createComponent(ViewModelModifiable model, String id)
	{
		return createComponent(model, id, (String) null);
	}

	public static GUIComponent createComponent(ViewModelModifiable model, String id, String name)
	{
		return createComponent(model, id, JsonNull.INSTANCE, name);
	}

	public static GUIComponent createComponent(ViewModelModifiable model, String id, JsonElement params)
	{
		return createComponent(model, id, params, null);
	}

	public static GUIComponent createComponent(ViewModelModifiable model, String id, JsonElement params, String name)
	{
		if (id != null)
		{
			String resolvedID;
			if (id.startsWith("class:") || id.startsWith("file:"))
				resolvedID = id;
			else
				resolvedID = standardComponentIDs.get(id);
			if (resolvedID != null)
			{
				if (resolvedID.startsWith("class:"))
				{
					String className = resolvedID.substring(6);
					tryLoadComponentClass(className);
					ComponentSupplier componentSupplier = componentSuppliers.get(className);
					if (componentSupplier != null)
						return componentSupplier.create(model, params, name);
					throw new IllegalArgumentException("Component supplier not found for ID " + id + " (resolved: " + resolvedID + ")");
				} else
				// we know id has to start with "file:" here
				// because standardComponentIDs only contains strings starting with "class:" or "file:"
				if (params != null && !JsonNull.INSTANCE.equals(params))
					throw new IllegalArgumentException("Can't give params to a component deserialized from a JSON file");
				try
				{
					return SubmodelComponentSerializer.deserialize(model, resolvedID.substring(5), name, id, null);
				}
				catch (IOException e)
				{
					throw new UncheckedIOException(e);
				}
			}
		}
		throw new RuntimeException("Could not get component supplier for ID " + id);
	}

	private static void tryLoadComponentClass(String componentClassName)
	{
		CodeSnippetSupplier.tryInvokeStaticInitializer(componentClassName, "Error loading component class %s: %s\n");
	}

	public static interface ComponentSupplier
	{
		public GUIComponent create(ViewModelModifiable model, JsonElement params, String name);
	}
}