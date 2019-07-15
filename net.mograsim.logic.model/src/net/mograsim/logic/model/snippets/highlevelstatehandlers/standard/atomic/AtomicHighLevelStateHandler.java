package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import com.google.gson.JsonObject;

import net.mograsim.logic.model.snippets.HighLevelStateHandler;

public interface AtomicHighLevelStateHandler
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
		public JsonObject params;
	}
}