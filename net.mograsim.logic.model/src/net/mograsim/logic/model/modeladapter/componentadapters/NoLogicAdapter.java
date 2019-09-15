package net.mograsim.logic.model.modeladapter.componentadapters;

import java.util.Map;

import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.wires.CoreWire;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;

/**
 * For ModelComponents that do not have any simulation logic behaviour
 *
 * @author Christian Femers
 */
public class NoLogicAdapter<T extends ModelComponent> implements ComponentAdapter<T>
{
	private final Class<T> modelComponentClass;

	public NoLogicAdapter(Class<T> modelComponentClass)
	{
		this.modelComponentClass = modelComponentClass;
	}

	@Override
	public Class<T> getSupportedClass()
	{
		return modelComponentClass;
	}

	@Override
	public void createAndLinkComponent(Timeline timeline, CoreModelParameters params, T modelComponent, Map<Pin, CoreWire> logicWiresPerPin)
	{
		// do nothing
	}
}
