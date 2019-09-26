package net.mograsim.plugin.nature;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;

public class ProjectContextEvent
{
	private final MachineContext machineContext;
	private final ProjectContextEventType eventType;

	public ProjectContextEvent(MachineContext machineContext, ProjectContextEventType eventType)
	{
		this.machineContext = machineContext;
		this.eventType = eventType;
	}

	public final MachineContext getMachineContext()
	{
		return machineContext;
	}

	public final ProjectContextEventType getEventType()
	{
		return eventType;
	}

	public final IProject getProject()
	{
		return machineContext.getProject();
	}

	public enum ProjectContextEventType
	{
		NEW, MACHINE_DEFINITION_CHANGE, OTHER_CHANGE, REFRESH, CLOSE, DELETE;

		static ProjectContextEventType ofResourceChangeEvent(int id)
		{
			switch (id)
			{
			case IResourceChangeEvent.POST_CHANGE:
				return OTHER_CHANGE;
			case IResourceChangeEvent.PRE_CLOSE:
				return CLOSE;
			case IResourceChangeEvent.PRE_DELETE:
				return DELETE;
			case IResourceChangeEvent.PRE_REFRESH:
				return REFRESH;
			default:
				return null;
			}
		}

		Optional<MachineContextStatus> getForcedStatus()
		{
			switch (this)
			{
			case CLOSE:
				return Optional.of(MachineContextStatus.CLOSED);
			case DELETE:
				return Optional.of(MachineContextStatus.DEAD);
			default:
				return Optional.empty();
			}
		}
	}
}