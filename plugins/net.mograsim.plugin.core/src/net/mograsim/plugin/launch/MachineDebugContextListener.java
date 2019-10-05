package net.mograsim.plugin.launch;

import java.util.Optional;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;

public abstract class MachineDebugContextListener implements IDebugContextListener
{
	private MachineDebugTarget debugTarget;

	@Override
	public void debugContextChanged(DebugContextEvent e)
	{
		debugContextChanged(e.getContext());
	}

	public void debugContextChanged(ISelection selection)
	{
		if (selection != null && selection instanceof TreeSelection)
		{
			TreeSelection treeSelection = (TreeSelection) selection;
			Object[] selectedElements = treeSelection.toArray();
			for (Object selectedElement : selectedElements)
			{
				IDebugTarget debugTarget;
				if (selectedElement instanceof IDebugElement)
					debugTarget = ((IDebugElement) selectedElement).getDebugTarget();
				else if (selectedElement instanceof ILaunch)
					debugTarget = ((ILaunch) selectedElement).getDebugTarget();
				else
					continue;
				if (!(debugTarget instanceof MachineDebugTarget))
					continue;
				if (debugTarget.isTerminated())
					continue;
				// we found a selected MachineDebugTarget
				if (this.debugTarget != debugTarget)
					updateContextAndCallContextChanged((MachineDebugTarget) debugTarget);
				return;
			}
		}
		// we didn't find a selected MachineDebugTarget
		updateContextAndCallContextChanged(null);
	}

	private void updateContextAndCallContextChanged(MachineDebugTarget newTarget)
	{
		MachineDebugTarget oldTarget = this.debugTarget;
		this.debugTarget = newTarget;
		machineDebugContextChanged(Optional.ofNullable(oldTarget), Optional.ofNullable(newTarget));
	}

	public abstract void machineDebugContextChanged(Optional<MachineDebugTarget> oldTarget, Optional<MachineDebugTarget> newTarget);
}
