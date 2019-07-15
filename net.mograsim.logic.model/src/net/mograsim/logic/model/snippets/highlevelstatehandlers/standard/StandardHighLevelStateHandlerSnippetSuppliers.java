package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.mograsim.logic.model.snippets.CodeSnippetSupplier;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler;
import net.mograsim.logic.model.util.JsonHandler;

public class StandardHighLevelStateHandlerSnippetSuppliers
{
	public static final CodeSnippetSupplier<HighLevelStateHandlerContext, AtomicHighLevelStateHandler> atomicHandlerSupplier;
	public static final CodeSnippetSupplier<HighLevelStateHandlerContext, SubcomponentHighLevelStateHandler> subcomponentHandlerSupplier;

	static
	{
		atomicHandlerSupplier = new CodeSnippetSupplier<>(null);
		subcomponentHandlerSupplier = new CodeSnippetSupplier<>(null);
	}

	static
	{
		// TODO update standardSnippetIDMapping.json
		try (InputStream s = StandardHighLevelStateHandlerSnippetSuppliers.class.getResourceAsStream("./standardSnippetIDMapping.json"))
		{
			if (s == null)
				throw new IOException("Resource not found");
			SnippetIDClassNames tmp = JsonHandler.readJson(s, SnippetIDClassNames.class);
			tmp.standardSubcomponentHandlerSuppliers.forEach(subcomponentHandlerSupplier::addStandardSnippetID);
			tmp.standardAtomicHandlerSuppliers.forEach(atomicHandlerSupplier::addStandardSnippetID);
		}
		catch (Exception e)
		{
			System.err.println("Failed to initialize standard snippet ID mapping: ");
			e.printStackTrace();
		}
	}

	private static class SnippetIDClassNames
	{
		public Map<String, String> standardSubcomponentHandlerSuppliers;
		public Map<String, String> standardAtomicHandlerSuppliers;
	}

	private StandardHighLevelStateHandlerSnippetSuppliers()
	{
		throw new UnsupportedOperationException();
	}
}
