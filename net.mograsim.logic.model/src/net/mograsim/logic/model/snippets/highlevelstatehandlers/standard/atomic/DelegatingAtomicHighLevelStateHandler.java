package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.HighLevelStateHandlerContext;

public class DelegatingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private final SubmodelComponent parentComponent;
	private GUIComponent delegateTarget;
	private String subStateID;

	public DelegatingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context)
	{
		this(context, null);
	}

	public DelegatingAtomicHighLevelStateHandler(HighLevelStateHandlerContext context, DelegatingAtomicHighLevelStateHandlerParams params)
	{
		this.parentComponent = context.component;
		if (params != null)
		{
			// TODO document this
			if (params.delegateTarget == null)
				setDelegateTarget(parentComponent);
			else
				setDelegateTarget(parentComponent.submodel.getComponentsByName().get(params.delegateTarget));
			setSubStateID(params.subStateID);
		}
	}

	public void set(GUIComponent delegateTarget, String subStateID)
	{
		setDelegateTarget(delegateTarget);
		setSubStateID(subStateID);
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

	public void setSubStateID(String subStateID)
	{
		this.subStateID = subStateID;
	}

	@Override
	public Object getHighLevelState()
	{
		return delegateTarget.getHighLevelState(subStateID);
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		delegateTarget.setHighLevelState(subStateID, newState);
	}

	public static class DelegatingAtomicHighLevelStateHandlerParams
	{
		public String delegateTarget;
		public String subStateID;
	}
}