package net.mograsim.plugin.nature;

import static net.mograsim.plugin.nature.MachineContextStatus.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.nature.ProjectContextEvent.ProjectContextEventType;

/**
 * A MachineContext is a project specific context for the Mograsim machine associated to them.
 * <p>
 * It stores the {@link MachineDefinition#getId() machine id}, the {@link MachineDefinition} if applicable and an active machine if present.
 * {@link ActiveMachineListener}s and {@link MachineContextStatusListener}s can be used to track the state of the MachineContext.
 *
 * @author Christian Femers
 *
 */
public class MachineContext
{
	final IProject owner;
	final ScopedPreferenceStore prefs;
	Optional<String> machineId = Optional.empty();
	Optional<MachineDefinition> machineDefinition = Optional.empty();
	Optional<Machine> activeMachine = Optional.empty();

	private MachineContextStatus status = UNKOWN;

	private final Set<ActiveMachineListener> machineListeners = new HashSet<>();
	private final Set<MachineContextStatusListener> stateListeners = new HashSet<>();

	public MachineContext(IProject owner)
	{
		this.owner = Objects.requireNonNull(owner);
		prefs = ProjectMachineContext.getProjectPrefs(owner);
		prefs.addPropertyChangeListener(this::preferenceListener);
		updateDefinition(ProjectMachineContext.getMachineIdFrom(prefs));
	}

	public final IProject getProject()
	{
		return owner;
	}

	public final ScopedPreferenceStore getPreferences()
	{
		return prefs;
	}

	/**
	 * Returns true if the project configuration is valid in the current environment
	 */
	public final boolean isCurrentyValid()
	{
		return status == READY || status == ACTIVE;
	}

	/**
	 * Returns true if the persisted project configuration itself is intact
	 * 
	 * @see MachineContextStatus#INTACT
	 */
	public final boolean isIntact()
	{
		return isCurrentyValid() || status == INTACT;
	}

	/**
	 * Returns true if a machine is instantiated and (possibly) running
	 */
	public final boolean isActive()
	{
		return status == ACTIVE || status == ACTIVE_CHANGED;
	}

	/**
	 * Returns the current status of this machine context
	 */
	public final MachineContextStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the projects machineId. Will likely break things, if the {@link MachineContext} {@link #isActive()}.
	 */
	public final boolean setMachineId(String machineId)
	{
		prefs.setValue(ProjectMachineContext.MACHINE_PROPERTY, machineId);
		try
		{
			prefs.save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Sets the active machine in the {@link MachineContext}'s project scope.
	 */
	public final void setActiveMachine(Machine machine)
	{
		activeMachine = Optional.ofNullable(machine);
		updateStatus();
		notifyActiveMachineListeners();
	}

	public final Optional<String> getMachineId()
	{
		return machineId;
	}

	public final Optional<MachineDefinition> getMachineDefinition()
	{
		return machineDefinition;
	}

	public final Optional<Machine> getActiveMachine()
	{
//		activateMachine(); // TODO is this the best way to deal with this?
		return activeMachine;
	}

	/**
	 * Tries to activate the associated machine. This will not succeed if the project is not {@link MachineContextStatus#READY}. If the
	 * status is {@link MachineContextStatus#ACTIVE}, this method has no effect.
	 * 
	 * @return true if the activation was successful
	 */
	public final boolean activateMachine()
	{
		if (status == ACTIVE)
			return true;
		machineDefinition.ifPresent(md -> setActiveMachine(md.createNew()));
		if (activeMachine.isPresent())
			System.out.format("Created new machine %s for project %s%n", activeMachine.get().getDefinition().getId(), owner.getName());
		updateStatus();
		return isActive();
	}

	/**
	 * This changes the internal status to a newly evaluated one and calls the {@link MachineContextStatusListener}s if this caused the
	 * status to change.
	 * 
	 * @see #reevaluateStatus()
	 * @see #getStatus()
	 */
	public final void updateStatus()
	{
		MachineContextStatus newStatus = reevaluateStatus();
		forceUpdateStatus(newStatus);
	}

	final void forceUpdateStatus(MachineContextStatus newStatus)
	{
		MachineContextStatus oldStatus = status;
		if (oldStatus == newStatus)
			return;
		status = newStatus;
		System.out.format("Project %s context status: %s -> %s%n", owner.getName(), oldStatus, newStatus);
		doPostStatusChangedAction();
		notifyMachineContextStatusListeners(oldStatus);
	}

	/**
	 * This method reevaluates the status <b>but does not change/update it</b>.<br>
	 * To update the status of the {@link MachineContext}, use {@link #updateStatus()}.
	 * 
	 * @return the raw status of the project at the time of the call.
	 */
	public final MachineContextStatus reevaluateStatus()
	{
		if (!owner.exists())
			return DEAD;
		if (!owner.isOpen())
			return CLOSED;
		if (hasInvaildMograsimProject())
			return BROKEN;
		if (machineDefinition.isEmpty())
			return INTACT;
		if (activeMachine.isEmpty())
			return READY;
		if (!activeMachine.get().getDefinition().getId().equals(machineDefinition.get().getId()))
			return ACTIVE_CHANGED;
		return ACTIVE;
	}

	private void doPostStatusChangedAction()
	{
		if ((status == DEAD || status == CLOSED) && activeMachine.isPresent())
		{
			System.out.format("Removed machine %s for project %s%n", activeMachine.get().getDefinition().getId(), owner.getName());
			activeMachine = Optional.empty();
			notifyActiveMachineListeners();
		}
	}

	private boolean hasInvaildMograsimProject()
	{
		try
		{
			if (!owner.isNatureEnabled(MograsimNature.NATURE_ID))
				return true;
			return machineId.isEmpty();
		}
		catch (CoreException e)
		{
			// cannot happen, because this method is called after the exceptional states were checked.
			e.printStackTrace();
			return false;
		}
	}

	final void updateDefinition(Optional<String> newMachineDefinitionId)
	{
		if (newMachineDefinitionId.equals(machineId))
			return;
		machineId = newMachineDefinitionId;
		machineDefinition = machineId.map(MachineRegistry::getMachine);
		updateStatus();
		ProjectMachineContext.notifyListeners(new ProjectContextEvent(this, ProjectContextEventType.MACHINE_DEFINITION_CHANGE));
	}

	private void preferenceListener(PropertyChangeEvent changeEvent)
	{
		if (changeEvent.getProperty().equals(ProjectMachineContext.MACHINE_PROPERTY))
		{
			updateDefinition(Optional.ofNullable((String) changeEvent.getNewValue()));
		}
	}

	private void notifyActiveMachineListeners()
	{
		machineListeners.forEach(ob -> ob.setMachine(activeMachine));
	}

	public void addActiveMachineListener(ActiveMachineListener ob)
	{
		machineListeners.add(ob);
		ob.setMachine(activeMachine);
	}

	public void removeActiveMachineListener(ActiveMachineListener ob)
	{
		machineListeners.remove(ob);
	}

	private void notifyMachineContextStatusListeners(MachineContextStatus oldStatus)
	{
		MachineContextStatus newStatus = status;
		stateListeners.forEach(ob -> ob.updateStatus(oldStatus, newStatus));
	}

	public void addMachineContextStatusListener(MachineContextStatusListener ob)
	{
		stateListeners.add(ob);
	}

	public void removeMachineContextStatusListener(MachineContextStatusListener ob)
	{
		stateListeners.remove(ob);
	}

	@FunctionalInterface
	public static interface ActiveMachineListener
	{
		void setMachine(Optional<Machine> machine);
	}

	@FunctionalInterface
	public static interface MachineContextStatusListener
	{
		void updateStatus(MachineContextStatus oldStatus, MachineContextStatus newStatus);
	}
}