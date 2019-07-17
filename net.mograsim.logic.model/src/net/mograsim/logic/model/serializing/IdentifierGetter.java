package net.mograsim.logic.model.serializing;

import java.util.function.Function;

import net.mograsim.logic.model.model.components.GUIComponent;
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
	 * Creates a new IdentifierGetter using "class:" concatenated with a component's / snippet's complete (canonical) class name as the
	 * default for all ID getter functions.
	 */
	public IdentifierGetter()
	{
		Function<Object, String> defaultSnippetIDGetter = c -> "class:" + c.getClass().getCanonicalName();
		this.componentIDs = defaultSnippetIDGetter::apply;
		this.symbolRendererIDs = defaultSnippetIDGetter::apply;
		this.outlineRendererIDs = defaultSnippetIDGetter::apply;
		this.highLevelStateHandlerIDs = defaultSnippetIDGetter::apply;
		this.subcomponentHighLevelStateHandlerIDs = defaultSnippetIDGetter::apply;
		this.atomicHighLevelStateHandlerIDs = defaultSnippetIDGetter::apply;
	}
}