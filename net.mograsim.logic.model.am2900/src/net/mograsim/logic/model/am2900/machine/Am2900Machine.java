package net.mograsim.logic.model.am2900.machine;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.Register;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.standard.memory.WordAddressableMemory;

public class Am2900Machine implements Machine
{
	private Am2900MachineDefinition machineDefinition;
	private LogicModelModifiable logicModel;
	private Timeline timeline;
	private MainMemory mainMemory;
	private MicroInstructionMemory instMemory;
	private CoreClock clock;

	public Am2900Machine(Am2900MachineDefinition am2900MachineDefinition)
	{
		this.machineDefinition = am2900MachineDefinition;
		logicModel = new LogicModelModifiable();
		IndirectModelComponentCreator.createComponent(logicModel,
				"resloader:Am2900Loader:jsonres:net/mograsim/logic/model/am2900/components/Am2900.json", "Am2900");
		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		mainMemory = new WordAddressableMemory(am2900MachineDefinition.getMainMemoryDefinition());
		instMemory = new StandardMicroInstructionMemory(am2900MachineDefinition.getMicroInstructionMemoryDefinition());
		timeline = LogicCoreAdapter.convert(logicModel, params);
		logicModel.getComponentByPath("Am2900.@c", ModelManualSwitch.class).getManualSwitch();
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
	public LogicModel getModel()
	{
		return logicModel;
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

	@Override
	public MainMemory getMainMemory()
	{
		return mainMemory;
	}

	@Override
	public MicroInstructionMemory getMicroInstructionMemory()
	{
		return instMemory;
	}

}
