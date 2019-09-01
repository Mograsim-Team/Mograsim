package net.mograsim.logic.model.serializing;

import static net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers.highLevelStateHandlerSupplier;
import static net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers.outlineRendererSupplier;
import static net.mograsim.logic.model.snippets.SubmodelComponentSnippetSuppliers.symbolRendererSupplier;
import static net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier;
import static net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.snippets.CodeSnippetSupplier;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic.AtomicHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.SubcomponentHighLevelStateHandler;

//TODO better name
public class IdentifierGetter
{
	// TODO save IDs for everything than can be deserialized / serialized (=every JSONSerializable?)
	public Function<GUIComponent, String> componentIDs;
	public Function<Renderer, String> symbolRendererIDs;
	public Function<Renderer, String> outlineRendererIDs;
	public Function<HighLevelStateHandler, String> highLevelStateHandlerIDs;
	public Function<SubcomponentHighLevelStateHandler, String> subcomponentHighLevelStateHandlerIDs;
	public Function<AtomicHighLevelStateHandler, String> atomicHighLevelStateHandlerIDs;

	/**
	 * Creates a new IdentifierGetter using the following as the default for all ID getter functions: <br>
	 * Define the verbose ID as <code>"class:" + canonicalClassName</code>.<br>
	 * If there is a standard ID mapping to this verbose ID recorded in the matching {@link CodeSnippetSupplier}, use this ID; if not, use
	 * the verbose ID.
	 */
	public IdentifierGetter()
	{
		componentIDs = generateStandardIDFunction(IndirectGUIComponentCreator.getStandardComponentIDs());
		symbolRendererIDs = generateStandardIDFunction(symbolRendererSupplier);
		outlineRendererIDs = generateStandardIDFunction(outlineRendererSupplier);
		highLevelStateHandlerIDs = generateStandardIDFunction(highLevelStateHandlerSupplier);
		atomicHighLevelStateHandlerIDs = generateStandardIDFunction(atomicHandlerSupplier);
		subcomponentHighLevelStateHandlerIDs = generateStandardIDFunction(subcomponentHandlerSupplier);
	}

	private static <T> Function<T, String> generateStandardIDFunction(CodeSnippetSupplier<?, T> snippetSupplier)
	{
		return generateStandardIDFunction(snippetSupplier.getStandardSnippetIDs());
	}

	private static <T> Function<T, String> generateStandardIDFunction(Map<String, String> standardComponentIDs)
	{
		return t ->
		{
			String verboseID = "class:" + t.getClass().getCanonicalName();
			return standardComponentIDs.entrySet().stream().filter(e -> e.getValue().equals(verboseID)).map(Entry::getKey).findAny()
					.orElse(verboseID);
		};
	}
}