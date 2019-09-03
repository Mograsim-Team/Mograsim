package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.CodeSnippetSupplier;
import net.mograsim.logic.model.util.JsonHandler;

public class IndirectGUIComponentCreator
{
	private static final Map<String, String> standardComponentIDs = new HashMap<>();
	private static final Map<String, String> standardComponentIDsUnmodifiable = Collections.unmodifiableMap(standardComponentIDs);

	private static final Map<String, ComponentSupplier> componentSuppliers = new HashMap<>();
	private static final Map<String, ResourceLoader> resourceLoaders = new HashMap<>();

	static
	{
		loadStandardComponentIDs(IndirectGUIComponentCreator.class.getResourceAsStream("standardComponentIDMapping.json"));
	}

	public static void loadStandardComponentIDs(InputStream standardComponentIdMappingStream)
	{
		try (InputStream s = standardComponentIdMappingStream)
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
		if (!associatedComponentID.matches("(file|class|resource):.+"))
			throw new IllegalArgumentException("Unrecognized component ID format: " + associatedComponentID);
		standardComponentIDs.put(standardComponentID, associatedComponentID);
	}

	public static Map<String, String> getStandardComponentIDs()
	{
		return standardComponentIDsUnmodifiable;
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
			String resolvedID = resolveID(id);
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
				} else if (params != null && !JsonNull.INSTANCE.equals(params))
					throw new IllegalArgumentException("Can't give params to a component deserialized from a JSON file");
				if (resolvedID.startsWith("resource:"))
				{
					String[] parts = resolvedID.split(":");
					if (parts.length != 3)
						throw new IllegalArgumentException("invaild resource id: " + resolvedID);
					String rLoadID = parts[1];
					String resID = parts[2];
					try
					{
						ResourceLoader loader;
						if (!resourceLoaders.containsKey(rLoadID))
						{
							Class<?> c = Class.forName(rLoadID);
							if (ResourceLoader.class.isAssignableFrom(c))
								loader = (ResourceLoader) c.getConstructor().newInstance();
							else
								loader = (ResourceLoader) Objects.requireNonNull(c.getMethod("resourceLoader").invoke(null));
							resourceLoaders.put(rLoadID, loader);
						} else
						{
							loader = Objects.requireNonNull(resourceLoaders.get(parts[1]));
						}
						if (resID.endsWith(".json"))
						{
							JsonObject jsonContents = JsonHandler.readJson(loader.loadResource(resID), JsonObject.class);
							return loadComponentFromJsonObject(model, id, name, jsonContents);
						}
						loader.loadClass(resID);
						ComponentSupplier componentSupplier = componentSuppliers.get(resID);
						if (componentSupplier != null)
							return componentSupplier.create(model, params, name);
						throw new IllegalArgumentException("Component supplier not found for ID " + id + " (class cannot initialize?)");
					}
					catch (IOException e)
					{
						throw new UncheckedIOException(e);
					}
					catch (ClassCastException | ReflectiveOperationException e)
					{
						throw new IllegalArgumentException("class not found / invaild resource loader specified:" + parts[1], e);
					}
				} else if (resolvedID.startsWith("file:"))
				{
					try
					{
						String filename = resolvedID.substring(5);
						JsonObject jsonContents = JsonHandler.readJson(filename, JsonObject.class);
						return loadComponentFromJsonObject(model, id, name, jsonContents);
					}
					catch (IOException e)
					{
						throw new UncheckedIOException(e);
					}
				} else
				{
					throw new IllegalArgumentException("unable to resolve/interpret id" + resolvedID);
				}
			}
		}
		throw new RuntimeException("Could not get component supplier for ID " + id);
	}

	public static String resolveID(String id)
	{
		if (id.matches("(file|class|resource):.+"))
			return id;
		return standardComponentIDs.get(id);
	}

	private static SubmodelComponent loadComponentFromJsonObject(ViewModelModifiable model, String id, String name, JsonObject jsonContents)
	{
		SerializablePojo jsonContentsAsSerializablePojo = JsonHandler.parser.fromJson(jsonContents, SerializablePojo.class);
		if (jsonContentsAsSerializablePojo.version == null)
			return LegacySubmodelComponentSerializer.deserialize(model,
					JsonHandler.parser.fromJson(jsonContents, LegacySubmodelComponentParams.class), name, id, null);
		return SubmodelComponentSerializer.deserialize(model, JsonHandler.parser.fromJson(jsonContents, SubmodelComponentParams.class),
				name, id, null);
	}

	public static void registerResourceLoader(ResourceLoader resourceLoader)
	{
		registerResourceLoader(resourceLoader, resourceLoader.getClass());
	}

	public static void registerResourceLoader(ResourceLoader resourceLoader, Class<?> reference)
	{
		resourceLoaders.put(reference.getName(), Objects.requireNonNull(resourceLoader));
	}

	public static void registerResourceLoader(ResourceLoader resourceLoader, String reference)
	{
		resourceLoaders.put(reference, Objects.requireNonNull(resourceLoader));
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