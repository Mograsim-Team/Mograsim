package net.mograsim.plugin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineRegistry;

public class MachineContext
{
	private Machine machine;
	private Set<ContextObserver> observers;
	private static MachineContext instance;

	private MachineContext()
	{
		observers = new HashSet<>();
	}

	public static MachineContext getInstance()
	{
		if (instance == null)
		{
			instance = new MachineContext();
			// TODO don't hardcode the Am2900
			instance.setMachine(MachineRegistry.getinstalledMachines().get("Am2900").createNew());
		}
		return instance;
	}

	public Machine getMachine()
	{
		return machine;
	}

	public void setMachine(Machine machine)
	{
		this.machine = machine;
		notifyObservers(machine);
	}

	public void registerObserver(ContextObserver ob)
	{
		observers.add(ob);
		ob.setMachine(Optional.ofNullable(machine));
	}

	public void deregisterObserver(ContextObserver ob)
	{
		observers.remove(ob);
	}

	private void notifyObservers(Machine machine)
	{
		observers.forEach(ob -> ob.setMachine(Optional.ofNullable(machine)));
	}

	@FunctionalInterface
	public static interface ContextObserver
	{
		void setMachine(Optional<Machine> machine);
	}
}
