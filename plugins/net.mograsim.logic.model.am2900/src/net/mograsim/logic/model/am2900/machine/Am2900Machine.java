package net.mograsim.logic.model.am2900.machine;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.components.ModelAm2900MainMemory;
import net.mograsim.logic.model.am2900.components.ModelAm2900MicroInstructionMemory;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.Register;
import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.machine.standard.memory.WordAddressableMemory;

public class Am2900Machine implements Machine
{
	private Am2900MachineDefinition machineDefinition;
	private LogicModelModifiable logicModel;
	private ModelComponent am2900;
	private Timeline timeline;
	private MainMemory mainMemory;
	private MicroInstructionMemory instMemory;
	private CoreClock clock;

	public Am2900Machine(LogicModelModifiable model, Am2900MachineDefinition am2900MachineDefinition)
	{
		this.machineDefinition = am2900MachineDefinition;
		this.logicModel = model;
		this.am2900 = IndirectModelComponentCreator.createComponent(logicModel,
				"resloader:Am2900Loader:jsonres:net/mograsim/logic/model/am2900/components/Am2900.json", "Am2900");
		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		mainMemory = new WordAddressableMemory(am2900MachineDefinition.getMainMemoryDefinition());
		instMemory = new StandardMicroInstructionMemory(am2900MachineDefinition.getMicroInstructionMemoryDefinition());
		logicModel.getComponentBySubmodelPath("Am2900.Am2900MainMemory#0", ModelAm2900MainMemory.class).setMachine(this);
		logicModel.getComponentBySubmodelPath("Am2900.Am2900MicroInstructionMemory#0", ModelAm2900MicroInstructionMemory.class)
				.setMachine(this);
		timeline = LogicCoreAdapter.convert(logicModel, params);
	}

	@Override
	public MachineDefinition getDefinition()
	{
		return machineDefinition;
	}

	@Override
	public void reset()
	{
		logicModel.getComponentByName("Am2900").setHighLevelState("c.out", BitVector.of(Bit.ZERO));
		MicroInstructionDefinition muiDef = getDefinition().getMicroInstructionMemoryDefinition().getMicroInstructionDefinition();
		ParameterClassification[] paramClassifications = muiDef.getParameterClassifications();
		MicroInstructionParameter[] defaultParams = muiDef.createDefaultInstruction().getParameters();
		defaultParams[19] = paramClassifications[19].parse("JZ");
		MicroInstruction jzMI = MicroInstruction.create(defaultParams);
		logicModel.getComponentByName("Am2900").setHighLevelState("muir_2.q", jzMI.toBitVector());
	}

	@Override
	public LogicModel getModel()
	{
		return logicModel;
	}

	public ModelComponent getAm2900()
	{
		return am2900;
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