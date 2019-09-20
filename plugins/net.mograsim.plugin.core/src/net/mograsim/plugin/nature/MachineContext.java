package net.mograsim.plugin.nature;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;

public class MachineContext
{
	IProject owner;
	ScopedPreferenceStore prefs;
	Optional<String> machineId;
	Optional<MachineDefinition> machineDefinition;
	Optional<Machine> activeMachine;

	private final Set<ActiveMachineListener> observers = new HashSet<>();

	public MachineContext(IProject owner)
	{
		this.owner = Objects.requireNonNull(owner);
		prefs = ProjectMachineContext.getProjectPrefs(owner);
		prefs.addPropertyChangeListener(this::preferenceListener);
		machineId = ProjectMachineContext.getMachineIdFrom(prefs);
		updateDefinition();
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
		return machineDefinition.isPresent();
	}

	/**
	 * Returns true if the persisted project configuration itself is intact
	 */
	public final boolean isIntact()
	{
		return machineId.isPresent();
	}

	/**
	 * Returns true if a machine is instantiated and (possibly) running
	 */
	public final boolean isActive()
	{
		return activeMachine.isPresent();
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
		notifyObservers();
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
		return activeMachine;
	}

	final void updateDefinition()
	{
		machineDefinition = machineId.map(MachineRegistry::getMachine);
	}

	private void preferenceListener(PropertyChangeEvent changeEvent)
	{
		if (changeEvent.getProperty().equals(ProjectMachineContext.MACHINE_PROPERTY))
		{
			machineId = Optional.ofNullable((String) changeEvent.getNewValue());
			updateDefinition();
		}
	}

	public void registerObserver(ActiveMachineListener ob)
	{
		observers.add(ob);
		ob.setMachine(activeMachine);
	}

	public void deregisterObserver(ActiveMachineListener ob)
	{
		observers.remove(ob);
	}

	private void notifyObservers()
	{
		observers.forEach(ob -> ob.setMachine(activeMachine));
	}

	@FunctionalInterface
	public static interface ActiveMachineListener
	{
		void setMachine(Optional<Machine> machine);
	}
}