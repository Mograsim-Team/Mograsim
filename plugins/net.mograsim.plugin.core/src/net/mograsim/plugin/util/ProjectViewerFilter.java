package net.mograsim.plugin.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ProjectViewerFilter extends ViewerFilter
{
	private final IProject project;

	public ProjectViewerFilter(IProject project)
	{
		this.project = project;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (((IResource) element).getType() == IResource.PROJECT)
			return element == project;
		return true;
	}
}