package net.mograsim.logic.model.snippets;

import java.util.function.BiFunction;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.util.JsonHandler;

public interface SnippetDefinintion<C, P, S>
{
	public Class<P> getParamClass();

	public S create(C context, P params);

	public default S create(C context, JsonElement params)
	{
		Class<P> paramClass = getParamClass();
		if (paramClass.equals(Void.class))
		{
			if (params != null)
				throw new IllegalArgumentException("Params given where none were expected");
			return create(context, (P) null);
		}
		return create(context, JsonHandler.fromJson(params, getParamClass()));
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