package net.mograsim.logic.ui.serializing.snippets;

import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;

public interface SnippetSupplier<P, S>
{
	public Class<P> getParamClass();

	public S create(SubmodelComponent component, P params);

	public default S create(SubmodelComponent component, JsonElement params)
	{
		return create(component, new Gson().fromJson(params, getParamClass()));
	}

	public static <P, S> SnippetSupplier<P, S> create(Class<P> paramClass, BiFunction<SubmodelComponent, P, S> supplier)
	{
		return new SnippetSupplier<>()
		{
			@Override
			public Class<P> getParamClass()
			{
				return paramClass;
			}

			@Override
			public S create(SubmodelComponent component, P params)
			{
				return supplier.apply(component, params);
			}
		};
	}
}