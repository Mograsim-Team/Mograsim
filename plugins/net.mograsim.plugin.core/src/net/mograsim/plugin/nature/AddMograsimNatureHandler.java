package net.mograsim.plugin.nature;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddMograsimNatureHandler extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		MultiStatus ms = new MultiStatus("net.mograsim.plugin.core", 42, "MograsimNature Conversion", null);

		if (selection instanceof IStructuredSelection)
		{
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();)
			{
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject)
				{
					project = (IProject) element;
				} else if (element instanceof IAdaptable)
				{
					project = ((IAdaptable) element).getAdapter(IProject.class);
				}
				if (project != null)
				{
					try
					{
						ms.add(toggleNature(project));
					}
					catch (CoreException e)
					{
						// TODO log something
						throw new ExecutionException("Failed to toggle nature", e);
					}
				}
			}
		}

		return ms.getSeverity();
	}

	/**
	 * Adds Mograsim nature on a project
	 *
	 * @param project to have Mograsim nature
	 * @return
	 */
	private IStatus toggleNature(IProject project) throws CoreException
	{
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = MograsimNature.NATURE_ID;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateNatureSet(newNatures);

		// only apply new nature, if the status is ok
		if (status.getCode() == IStatus.OK)
		{
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}

		return status;
	}
}