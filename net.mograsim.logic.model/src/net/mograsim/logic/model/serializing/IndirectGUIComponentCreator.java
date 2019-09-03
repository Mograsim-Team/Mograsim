package net.mograsim.logic.model.serializing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	private static final Map<String, JsonObject> componentCache = new HashMap<>();

	private static final ResourceLoader defaultResourceLoader;
	static
	{
		defaultResourceLoader = ClassLoaderBasedResourceLoader.create(IndirectGUIComponentCreator.class.getClassLoader());
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
		if (!checkIDIsValidResolvedID(associatedComponentID))
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
		if (id == null)
			throw new NullPointerException("Component ID is null");
		if (componentCache.containsKey(id))
			return loadComponentFromJsonObject(model, id, name, componentCache.get(id));
		String resolvedID = resolveID(id);
		if (resolvedID == null)
			throw new IllegalArgumentException("Unknown standard ID or illegal resolved ID: " + id);
		String[] parts = resolvedID.split(":");
		String firstPart = parts[0];
		if (firstPart.equals("jsonfile"))
		{
			JsonObject jsonContents;
			try
			{
				// don't use parts[1], because the path could contain ':'
				jsonContents = JsonHandler.readJson(resolvedID.substring("jsonfile:".length()), JsonObject.class);
			}
			catch (IOException e)
			{
				throw new UncheckedIOException("Error loading JSON file", e);
			}
			return loadComponentFromJsonObject(model, id, name, jsonContents);
		}
		ResourceLoader loader;
		String resTypeID;
		String resID;
		if (firstPart.equals("resloader"))
		{
			String loaderID = parts[1];
			loader = resourceLoaders.get(loaderID);
			if (loader == null)
				tryLoadResourceLoader(loaderID);
			loader = resourceLoaders.get(loaderID);
			if (loader == null)
				throw new IllegalArgumentException(
						"Unknown resource loader: " + loaderID + " (but class was found. Probably the static initializer is missing)");
			resTypeID = parts[2];
			resID = parts[3];
		} else
		{
			loader = defaultResourceLoader;
			resTypeID = parts[0];
			resID = parts[1];
		}
		if (resTypeID.equals("jsonres"))
		{
			JsonObject jsonContents;
			try
			{
				@SuppressWarnings("resource") // jsonStream is closed in JsonHandler
				InputStream jsonStream = Objects.requireNonNull(loader.loadResource(resID), "Error loading JSON resource: Not found");
				jsonContents = JsonHandler.readJson(jsonStream, JsonObject.class);
			}
			catch (IOException e)
			{
				throw new UncheckedIOException("Error loading JSON resource", e);
			}
			return loadComponentFromJsonObject(model, id, name, jsonContents);
		} else if (resTypeID.equals("class"))
		{
			ComponentSupplier componentSupplier = componentSuppliers.get(resID);
			if (componentSupplier == null)
				try
				{
					loader.loadClass(resID);
				}
				catch (@SuppressWarnings("unused") ClassNotFoundException e)
				{
					throw new IllegalArgumentException("Unknown component supplier: " + resID);
				}
			componentSupplier = componentSuppliers.get(resID);
			if (componentSupplier == null)
				throw new IllegalArgumentException(
						"Unknown component supplier: " + resID + " (but class was found. Probably the static initializer is missing)");
			return componentSupplier.create(model, params, name);
		} else
			throw new IllegalStateException("Unknown resource type ID: " + resTypeID);
	}

	public static String resolveID(String id)
	{
		if (checkIDIsValidResolvedID(id))
			return id;
		return standardComponentIDs.get(id);
	}

	private static boolean checkIDIsValidResolvedID(String id)
	{
		return id.matches("jsonfile:(.+)|(resloader:([^:]+):)?(jsonres|class):[^:]+");
	}

	private static SubmodelComponent loadComponentFromJsonObject(ViewModelModifiable model, String id, String name, JsonObject jsonContents)
	{
		componentCache.putIfAbsent(id, jsonContents);
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

	private static void tryLoadResourceLoader(String loaderClassName)
	{
		CodeSnippetSupplier.tryInvokeStaticInitializer(loaderClassName, "Error loading resoruce loader %s: %s\n");
	}

	public static interface ComponentSupplier
	{
		public GUIComponent create(ViewModelModifiable model, JsonElement params, String name);
	}
}