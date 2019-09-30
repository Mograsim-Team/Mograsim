package net.mograsim.plugin.util;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

public class ImageDescriptorWithMargins extends CompositeImageDescriptor
{
	private final ImageDescriptor input;
	private final Point size;
	private final int ox, oy;

	public ImageDescriptorWithMargins(ImageDescriptor input, Point size)
	{
		this(input, 0, 0, size);
	}

	public ImageDescriptorWithMargins(ImageDescriptor input, int offX, int offY, Point size)
	{
		this.input = input;
		this.size = size;
		this.ox = offX;
		this.oy = offY;
	}

	@Override
	protected Point getSize()
	{
		return size;
	}

	@Override
	protected void drawCompositeImage(int width, int height)
	{
		drawImage(input::getImageData, ox, oy);
	}
}