package net.mograsim.plugin.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MograsimPerspective implements IPerspectiveFactory
{
	@Override
	public void createInitialLayout(IPageLayout factory)
	{
		// everything is done in the plugin.xml
	}
}
