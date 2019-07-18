package net.mograsim.logic.model.snippets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.DefaultHighLevelStateHandler;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.DefaultSymbolRenderer;
import net.mograsim.logic.model.util.JsonHandler;

public class SubmodelComponentSnippetSuppliers
{
	public static final CodeSnippetSupplier<SubmodelComponent, Renderer> symbolRendererSupplier;
	public static final CodeSnippetSupplier<SubmodelComponent, Renderer> outlineRendererSupplier;
	public static final CodeSnippetSupplier<SubmodelComponent, HighLevelStateHandler> highLevelStateHandlerSupplier;

	static
	{
		symbolRendererSupplier = new CodeSnippetSupplier<>(SnippetDefinintion.create(Void.class, DefaultSymbolRenderer::new));
		outlineRendererSupplier = new CodeSnippetSupplier<>(SnippetDefinintion.create(Void.class, DefaultOutlineRenderer::new));
		highLevelStateHandlerSupplier = new CodeSnippetSupplier<>(SnippetDefinintion.create(Void.class, DefaultHighLevelStateHandler::new));
	}

	static
	{
		try (InputStream s = SubmodelComponentSnippetSuppliers.class.getResourceAsStream("standardSnippetIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			SnippetIDClassNames tmp = JsonHandler.readJson(s, SnippetIDClassNames.class);
			tmp.standardSymbolRendererSuppliers.forEach(symbolRendererSupplier::addStandardSnippetID);
			tmp.standardOutlineRendererSuppliers.forEach(outlineRendererSupplier::addStandardSnippetID);
			tmp.standardHighLevelStateHandlerSuppliers.forEach(highLevelStateHandlerSupplier::addStandardSnippetID);
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping:");
			e.printStackTrace();
		}
	}

	private static class SnippetIDClassNames
	{
		public Map<String, String> standardSymbolRendererSuppliers;
		public Map<String, String> standardOutlineRendererSuppliers;
		public Map<String, String> standardHighLevelStateHandlerSuppliers;
	}

	private SubmodelComponentSnippetSuppliers()
	{
		throw new UnsupportedOperationException();
	}
}