package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent;

import java.util.function.Consumer;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.serializing.JSONSerializable;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;

public interface SubcomponentHighLevelStateHandler extends JSONSerializable
{
	/**
	 * Gets the current value of the given high level state of the subcomponent represented by this SubcomponentHighLevelStateHandler.<br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	public Object getHighLevelState(String subStateID);

	/**
	 * Sets the given high level state of the subcomponent represented by this SubcomponentHighLevelStateHandler to the given value.<br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	public void setHighLevelState(String subStateID, Object newState);

	public static class SubcomponentHighLevelStateHandlerParams
	{
		public String id;
		public JsonElement params;
	}

	public void addListener(String subStateID, Consumer<Object> stateChanged);

	public void removeListener(String subStateID, Consumer<Object> stateChanged);
}