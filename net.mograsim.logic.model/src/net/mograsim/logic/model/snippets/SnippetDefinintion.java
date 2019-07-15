package net.mograsim.logic.model.snippets;

import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public interface SnippetDefinintion<C, P, S>
{
	public Class<P> getParamClass();

	public S create(C context, P params);

	public default S create(C context, JsonElement params)
	{
		return create(context, new Gson().fromJson(params, getParamClass()));
	}

	public static <C, P, S> SnippetDefinintion<C, P, S> create(Class<P> paramClass, BiFunction<C, P, S> supplier)
	{
		return new SnippetDefinintion<>()
		{
			@Override
			public Class<P> getParamClass()
			{
				return paramClass;
			}

			@Override
			public S create(C context, P params)
			{
				return supplier.apply(context, params);
			}
		};
	}
}