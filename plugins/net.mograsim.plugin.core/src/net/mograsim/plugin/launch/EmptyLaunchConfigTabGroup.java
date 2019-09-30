package net.mograsim.plugin.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * Useful for specifying launch config tabs via the extension point <code>org.eclipse.debug.ui.launchConfigurationTabs</code>.
 * 
 * @author Daniel Kirschten
 */
public class EmptyLaunchConfigTabGroup extends AbstractLaunchConfigurationTabGroup
{
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		setTabs(new ILaunchConfigurationTab[0]);
	}
}