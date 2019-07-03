package net.mograsim.logic.ui.serializing.snippets;

import com.google.gson.JsonElement;

import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;

public interface RendererProvider
{
	public Renderer create(DeserializedSubmodelComponent component, JsonElement params);
}