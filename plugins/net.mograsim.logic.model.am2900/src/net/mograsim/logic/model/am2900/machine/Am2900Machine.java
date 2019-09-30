package net.mograsim.logic.model.am2900.machine;

import java.util.HashSet;
import java.util.Set;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.Register;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.machine.standard.memory.AssignableMainMemory;
import net.mograsim.machine.standard.memory.WordAddressableMemory;

public class Am2900Machine implements Machine
{
	private Am2900MachineDefinition machineDefinition;
	private LogicModelModifiable logicModel;
	private ModelComponent am2900;
	private Timeline timeline;
	private AssignableMainMemory mainMemory;
	private AssignableMicroInstructionMemory instMemory;
	private CoreClock clock;
	private long activeInstructionAddress;

	private final Set<ActiveMicroInstructionChangedListener> amicListeners;

	public Am2900Machine(LogicModelModifiable model, Am2900MachineDefinition am2900MachineDefinition)
	{
		this.machineDefinition = am2900MachineDefinition;
		this.logicModel = model;
		this.am2900 = IndirectModelComponentCreator.createComponent(logicModel,
				"resloader:Am2900Loader:jsonres:net/mograsim/logic/model/am2900/components/Am2900.json", "Am2900");
		this.amicListeners = new HashSet<>();

		CoreModelParameters params = new CoreModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		mainMemory = new AssignableMainMemory(new WordAddressableMemory(am2900MachineDefinition.getMainMemoryDefinition()));
		instMemory = new AssignableMicroInstructionMemory(
				new StandardMicroInstructionMemory(am2900MachineDefinition.getMicroInstructionMemoryDefinition()));
		timeline = LogicCoreAdapter.convert(logicModel, params);
		am2900.setHighLevelState("ram.memory_binding", mainMemory);
		am2900.setHighLevelState("mpm.memory_binding", instMemory);
		clock = logicModel.getComponentBySubmodelPath("Am2900.Clock#0", ModelClock.class).getClock();
		clock.registerObserver(c ->
		{
			if (clock.isOn())
			{
				long oldAddress = activeInstructionAddress;
				activeInstructionAddress = getCurrentMicroInstructionAddress();
				notifyActiveMicroInstructionChangedListeners(oldAddress, activeInstructionAddress);
			}
		});
	}

	@Override
	public MachineDefinition getDefinition()
	{
		return machineDefinition;
	}

	@Override
	public void reset()
	{
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
	public AssignableMainMemory getMainMemory()
	{
		return mainMemory;
	}

	@Override
	public AssignableMicroInstructionMemory getMicroInstructionMemory()
	{
		return instMemory;
	}

	@Override
	public long getActiveMicroInstructionAddress()
	{
		return activeInstructionAddress;
	}

	private long getCurrentMicroInstructionAddress()
	{
		// TODO: replace with highlevelstate
		BitVector vector = logicModel.getWireBySubmodelPath("Am2900.wire_mpm_address").getWireValues();
		return vector.isBinary() ? vector.getUnsignedValueLong() : -1;
	}

	@Override
	public void addActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener listener)
	{
		amicListeners.add(listener);
	}

	@Override
	public void removeActiveMicroInstructionChangedListener(ActiveMicroInstructionChangedListener listener)
	{
		amicListeners.remove(listener);
	}

	private void notifyActiveMicroInstructionChangedListeners(long oldAddress, long newAddress)
	{
		amicListeners.forEach(l -> l.instructionChanged(oldAddress, newAddress));
	}
}