package net.mograsim.logic.model.am2900.machine;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModel;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.Register;

public class Am2900Machine implements Machine
{
	private Am2900MachineDefinition machineDefinition;
	private ViewModelModifiable viewModel;
	private Timeline timeline;
	private CoreClock clock;

	public Am2900Machine(Am2900MachineDefinition am2900MachineDefinition)
	{
		this.machineDefinition = am2900MachineDefinition;
		viewModel = new ViewModelModifiable();
		IndirectModelComponentCreator.createComponent(viewModel,
				"resloader:Am2900Loader:jsonres:net/mograsim/logic/model/am2900/components/ModelAm2900.json");
		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		timeline = LogicCoreAdapter.convert(viewModel, params);
	}

	@Override
	public MachineDefinition getDefinition()
	{
		return machineDefinition;
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ViewModel getModel()
	{
		return viewModel;
	}

	@Override
	public Timeline getTimeline()
	{
		return timeline;
	}

	@Override
	public CoreClock getClock()
	{
		return clock;
	}

	@Override
	public BitVector getRegister(Register r)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegister(Register r, BitVector value)
	{
		// TODO Auto-generated method stub

	}

}
