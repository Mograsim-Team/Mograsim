package net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.atomic;

import java.util.function.Consumer;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.snippets.SnippetDefinintion;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandlerSnippetSuppliers;

public class DelegatingAtomicHighLevelStateHandler implements AtomicHighLevelStateHandler
{
	private final SubmodelComponent parentComponent;
	private ModelComponent delegateTarget;
	private String subStateID;

	public DelegatingAtomicHighLevelStateHandler(SubmodelComponent component)
	{
		this(component, null);
	}

	public DelegatingAtomicHighLevelStateHandler(SubmodelComponent component, DelegatingAtomicHighLevelStateHandlerParams params)
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
			setSubStateID(params.subStateID);
		}
		parentComponent.submodel.addComponentRemovedListener(c ->
		{
			if (delegateTarget == c)
				delegateTarget = null;
		});
	}

	public void set(ModelComponent delegateTarget, String subStateID)
	{
		setDelegateTarget(delegateTarget);
		setSubStateID(subStateID);
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

	public void setSubStateID(String subStateID)
	{
		this.subStateID = subStateID;
	}

	public String getSubStateID()
	{
		return subStateID;
	}

	@Override
	public Object getHighLevelState()
	{
		checkTarget();
		return delegateTarget.getHighLevelState(subStateID);
	}

	@Override
	public void setHighLevelState(Object newState)
	{
		checkTarget();
		delegateTarget.setHighLevelState(subStateID, newState);
	}

	@Override
	public void addListener(Consumer<Object> stateChanged)
	{
		checkTarget();
		delegateTarget.addHighLevelStateListener(subStateID, stateChanged);
	}

	@Override
	public void removeListener(Consumer<Object> stateChanged)
	{
		checkTarget();
		delegateTarget.removeHighLevelStateListener(subStateID, stateChanged);
	}

	private void checkTarget()
	{
		if (delegateTarget == null)
			throw new IllegalStateException("Delegating to a component that was destroyed");
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "delegating";
	}

	@Override
	public DelegatingAtomicHighLevelStateHandlerParams getParamsForSerializing(IdentifyParams idParams)
	{
		checkTarget();
		DelegatingAtomicHighLevelStateHandlerParams params = new DelegatingAtomicHighLevelStateHandlerParams();
		params.delegateTarget = delegateTarget == parentComponent ? null : delegateTarget.getName();
		params.subStateID = subStateID;
		return params;
	}

	public static class DelegatingAtomicHighLevelStateHandlerParams
	{
		public String delegateTarget;
		public String subStateID;
	}

	static
	{
		StandardHighLevelStateHandlerSnippetSuppliers.atomicHandlerSupplier.setSnippetSupplier(
				DelegatingAtomicHighLevelStateHandler.class.getCanonicalName(),
				SnippetDefinintion.create(DelegatingAtomicHighLevelStateHandlerParams.class, DelegatingAtomicHighLevelStateHandler::new));
	}
}