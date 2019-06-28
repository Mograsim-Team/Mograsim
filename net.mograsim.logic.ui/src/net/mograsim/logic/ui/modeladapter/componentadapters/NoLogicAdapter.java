package net.mograsim.logic.ui.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.Wire;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;

/**
 * For GUIComponents that do not have any simulation logic behaviour
 *
 * @author Christian Femers
 */
public class NoLogicAdapter<T extends GUIComponent> implements ComponentAdapter<T>
{
	private final Class<T> guiComponentClass;

	public NoLogicAdapter(Class<T> guiComponentClass)
	{
		this.guiComponentClass = guiComponentClass;
	}

	@Override
	public Class<T> getSupportedClass()
	{
		return guiComponentClass;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, LogicModelParameters params, T guiComponent, Map<Pin, Wire> logicWiresPerPin)
	{
		// do nothing
	}

	/**
	 * Creates a new {@link NoLogicAdapter} for the given GUIComponent class.
	 * 
	 * @author Christian Femers
	 */
	public static <T extends GUIComponent> NoLogicAdapter<T> forClass(Class<T> guiComponentClass)
	{
		return new NoLogicAdapter<>(guiComponentClass);
	}
}
