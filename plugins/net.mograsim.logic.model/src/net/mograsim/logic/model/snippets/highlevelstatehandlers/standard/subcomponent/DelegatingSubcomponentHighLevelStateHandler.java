package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class DelegatingSubcomponentHighLevelStateHandler implements SubcomponentHighLevelStateHandler
{
	private final SubmodelComponent parentComponent;
	private ModelComponent delegateTarget;
	private String prefix;

	public DelegatingSubcomponentHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public DelegatingSubcomponentHighLevelStateHandler(SubmodelComponent component,
			DelegatingSubcomponentHighLevelStateHandlerParams params)
	{
		this.parentComponent = component;
		if (params != null)
		{
			// TODO document this
			if (params.delegateTarget == null)
				setDelegateTarget(parentComponent);
			else
			{
				ModelComponent delegateTarget = parentComponent.submodel.getComponentsByName().get(params.delegateTarget);
				if (delegateTarget == null)
					throw new NullPointerException("No subcomponent with name " + params.delegateTarget);
				setDelegateTarget(delegateTarget);
			}
			setPrefix(params.prefix);
		}
		parentComponent.submodel.addComponentRemovedListener(c ->
		{
			if (delegateTarget == c)
				delegateTarget = null;
		});
	}

	public void set(ModelComponent delegateTarget, String prefix)
	{
		setDelegateTarget(delegateTarget);
		setPrefix(prefix);
	}

	public void setDelegateTarget(ModelComponent delegateTarget)
	{
		if (delegateTarget == null)
			this.delegateTarget = parentComponent;
		else if (parentComponent.submodel.getComponentsByName().get(delegateTarget.getName()) != delegateTarget)
			throw new IllegalArgumentException(
					"Can only set components belonging to the submodel of the parent component of this handler as the delegate target");
		this.delegateTarget = delegateTarget;
	}

	public ModelComponent getDelegateTarget()
	{
		return delegateTarget;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getPrefix()
	{
		return prefix;
	}

	@Override
	public Object getHighLevelState(String subStateID)
	{
		if (delegateTarget == null)
			throw new IllegalStateException("Delegating to a component that was destroyed");
		return delegateTarget.getHighLevelState(getDelegateTargetHighLevelStateID(subStateID));
	}

	@Override
	public void setHighLevelState(String subStateID, Object newState)
	{
		if (delegateTarget == null)
			throw new IllegalStateException("Delegating to a component that was destroyed");
		delegateTarget.setHighLevelState(getDelegateTargetHighLevelStateID(subStateID), newState);
	}

	private String getDelegateTargetHighLevelStateID(String subStateID)
	{
		return prefix == null ? subStateID : prefix + '.' + subStateID;
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "delegating";
	}

	@Override
	public DelegatingSubcomponentHighLevelStateHandlerParams getParamsForSerializing(IdentifyParams idParams)
	{
		DelegatingSubcomponentHighLevelStateHandlerParams params = new DelegatingSubcomponentHighLevelStateHandlerParams();
		params.delegateTarget = delegateTarget.getName();
		params.prefix = prefix;
		return params;
	}

	public static class DelegatingSubcomponentHighLevelStateHandlerParams
	{
		public String delegateTarget;
		public String prefix;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.subcomponentHandlerSupplier
				.setSnippetSupplier(DelegatingSubcomponentHighLevelStateHandler.class.getCanonicalName(), SnippetDefinintion
						.create(DelegatingSubcomponentHighLevelStateHandlerParams.class, DelegatingSubcomponentHighLevelStateHandler::new));
	}
}