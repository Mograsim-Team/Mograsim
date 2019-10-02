package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.function.Consumer;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.serializing.JSONSerializable;
import net.mograsim.logic.model.snippets.HighLevelStateHandler;

public interface AtomicHighLevelStateHandler extends JSONSerializable
{
	/**
	 * Gets the current value of the atomic high level state represented by this AtomicHighLevelStateHandler.<br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	public Object getHighLevelState();

	/**
	 * Sets the atomic high level state represented by this AtomicHighLevelStateHandler to the given value.<br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @author Daniel Kirschten
	 */
	public void setHighLevelState(Object newState);

	public static class AtomicHighLevelStateHandlerParams
	{
		public String id;
		public JsonElement params;
	}

	public void addListener(Consumer<Object> stateChanged);

	public void removeListener(Consumer<Object> stateChanged);
}