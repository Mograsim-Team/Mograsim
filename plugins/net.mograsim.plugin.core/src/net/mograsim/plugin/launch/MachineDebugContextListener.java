package net.mograsim.plugin.launch;

import java.util.Optional;

import org.eclipse.debug.core.ILaunch;
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
				MachineDebugTarget debugTarget;
				if (selectedElement instanceof MachineDebugTarget)
					debugTarget = (MachineDebugTarget) selectedElement;
				else if (selectedElement instanceof ILaunch)
				{
					ILaunch launch = (ILaunch) selectedElement;
					IDebugTarget genericDebugTarget = launch.getDebugTarget();
					if (genericDebugTarget instanceof MachineDebugTarget)
						debugTarget = (MachineDebugTarget) genericDebugTarget;
					else
						continue;
				} else
					continue;
				if (debugTarget.isTerminated())
					continue;
				// we found a selected MachineDebugTarget
				if (this.debugTarget != debugTarget)
				{
					MachineDebugTarget oldTarget = this.debugTarget;
					this.debugTarget = debugTarget;
					machineDebugContextChanged(Optional.ofNullable(oldTarget), Optional.of(debugTarget));
				}
				return;
			}
			MachineDebugTarget oldTarget = debugTarget;
			debugTarget = null;
			machineDebugContextChanged(Optional.ofNullable(oldTarget), Optional.empty());
		}
	}

	public abstract void machineDebugContextChanged(Optional<MachineDebugTarget> oldTarget, Optional<MachineDebugTarget> newTarget);
}
