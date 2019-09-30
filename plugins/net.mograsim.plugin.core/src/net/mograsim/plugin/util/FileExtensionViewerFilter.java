package net.mograsim.plugin.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FileExtensionViewerFilter extends ViewerFilter
{
	private final String fileext;

	public FileExtensionViewerFilter(String fileext)
	{
		this.fileext = fileext;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		IResource elementResource = (IResource) element;
		switch (elementResource.getType())
		{
		case IResource.FILE:
			return elementResource.getProjectRelativePath().getFileExtension().equals(fileext);
		case IResource.FOLDER:
			return true;
		default:
			return true;
		}
	}
}