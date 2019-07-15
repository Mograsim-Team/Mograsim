package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.HighLevelStateHandlerContext;

public class DelegatingSubcomponentHighLevelStateHandler implements SubcomponentHighLevelStateHandler
{
	private final SubmodelComponent parentComponent;
	private GUIComponent delegateTarget;
	private String prefix;

	public DelegatingSubcomponentHighLevelStateHandler(HighLevelStateHandlerContext context)
	{
		this(context, null);
	}

	public DelegatingSubcomponentHighLevelStateHandler(HighLevelStateHandlerContext context,
			DelegatingSubcomponentHighLevelStateHandlerParams params)
	{
		this.parentComponent = context.component;
		if (params != null)
		{
			// TODO document this
			if (params.delegateTarget == null)
				setDelegateTarget(parentComponent);
			else
				this.delegateTarget = parentComponent.submodel.getComponentsByName().get(params.delegateTarget);
			setPrefix(prefix);
		}
	}

	public void set(GUIComponent delegateTarget, String prefix)
	{
		setDelegateTarget(delegateTarget);
		setPrefix(prefix);
	}

	public void setDelegateTarget(GUIComponent delegateTarget)
	{
		if (delegateTarget == null)
			this.delegateTarget = parentComponent;
		else if (!parentComponent.submodel.getComponentsByName().containsValue(delegateTarget))
			throw new IllegalArgumentException(
					"Can only set components belonging to the submodel of the parent component of this handler as the delegate target");
		this.delegateTarget = delegateTarget;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	@Override
	public Object getHighLevelState(String subStateID)
	{
		return delegateTarget.getHighLevelState(getDelegateTargetHighLevelStateID(subStateID));
	}

	@Override
	public void setHighLevelState(String subStateID, Object newState)
	{
		delegateTarget.setHighLevelState(getDelegateTargetHighLevelStateID(subStateID), newState);
	}

	private String getDelegateTargetHighLevelStateID(String subStateID)
	{
		return prefix == null ? subStateID : prefix + '.' + subStateID;
	}

	public static class DelegatingSubcomponentHighLevelStateHandlerParams
	{
		public String delegateTarget;
		public String prefix;
	}
}