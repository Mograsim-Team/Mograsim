package net.mograsim.logic.model.snippets;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.serializing.JSONSerializable;

/**
 * A high level state ID consists of parts separated by dots ('.').<br>
 * The last part (the part after the last dot) is called "atomic high level state ID". The parts before that part are called "subcomponent
 * ID"s.<br>
 * If there is no dot in a high level state ID, the whole high level state ID is called atomic.<br>
 * Note that subcomponent IDs don't have to correspond to actual subcomponents. For example, a RAM component may supply subcomponent IDs
 * "c0000", "c0001" ... "cFFFF" without actually having a subcomponent for each cell. It also is allowed to delegate an atomic high level
 * state ID to a subcomponent.
 * 
 * @author Daniel Kirschten
 */
public interface HighLevelStateHandler extends JSONSerializable
{
	/**
	 * Gets the current value of the given high-level state. <br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @see #setHighLevelState(String, Object)
	 * @see GUIComponent#getHighLevelState(String)
	 * 
	 * @author Daniel Kirschten
	 */
	public Object getHighLevelState(String stateID);

	/**
	 * Sets the given high-level state to the given value. <br>
	 * See {@link HighLevelStateHandler} for an explanation of high-level state IDs.
	 * 
	 * @see #getHighLevelState(String)
	 * @see GUIComponent#setHighLevelState(String, Object)
	 * 
	 * @author Daniel Kirschten
	 */
	public void setHighLevelState(String stateID, Object newState);

}