package net.mograsim.plugin.launch;

import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import net.mograsim.plugin.MograsimActivator;

public class MachineLaunchConfigType extends LaunchConfigurationDelegate
{
	public static final String PROJECT_ATTR = MograsimActivator.PLUGIN_ID + "project";

	private final IResourceChangeListener resChangedListener;

	public MachineLaunchConfigType()
	{
		this.resChangedListener = this::resourceChanged;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resChangedListener,
				IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_BUILD
						| IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.PRE_REFRESH);
		System.out.println("launch");
		// TODO start a machine
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "\"echo Press Enter... &&pause>NUL && echo finished\"");
		try
		{
			launch.addProcess(DebugPlugin.newProcess(launch, pb.start(), ""));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void resourceChanged(IResourceChangeEvent event)
	{
		// TODO react to MPM changes
		int type = event.getType();
		String typeStr;
		switch (type)
		{
		case IResourceChangeEvent.POST_BUILD:
			typeStr = "POST_BUILD";
			break;
		case IResourceChangeEvent.POST_CHANGE:
			typeStr = "POST_CHANGE";
			break;
		case IResourceChangeEvent.PRE_BUILD:
			typeStr = "PRE_BUILD";
			break;
		case IResourceChangeEvent.PRE_CLOSE:
			typeStr = "PRE_CLOSE";
			break;
		case IResourceChangeEvent.PRE_DELETE:
			typeStr = "PRE_DELETE";
			break;
		case IResourceChangeEvent.PRE_REFRESH:
			typeStr = "PRE_REFRESH";
			break;
		default:
			typeStr = "<unknown: " + type + ">";
		}
		System.out.println(typeStr + ": " + event);
	}
}