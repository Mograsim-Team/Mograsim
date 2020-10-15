package net.mograsim.logic.model.am2900.machine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.logic.model.am2900.machine.registers.muInstrRegister;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.CoreModelParameters.CoreModelParametersBuilder;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.machine.AssignableMainMemory;
import net.mograsim.machine.Machine;
import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.StandardMainMemory;
import net.mograsim.machine.mi.AssignableMPROM;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.StandardMPROM;
import net.mograsim.machine.mi.StandardMicroInstructionMemory;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;
import net.mograsim.machine.registers.Register;
import net.mograsim.machine.registers.RegisterGroup;

public class Am2900Machine implements Machine
{
	private final AbstractAm2900MachineDefinition machineDefinition;
	private final LogicModelModifiable logicModel;
	private final ModelComponent am2900;
	private final Timeline timeline;
	private final CoreModelParameters params;
	private final AssignableMainMemory mainMemory;
	private final AssignableMicroInstructionMemory instMemory;
	private final AssignableMPROM mprom;
	private final CoreClock clock;
	private long activeInstructionAddress;

	private final Set<ActiveMicroInstructionChangedListener> amicListeners;

	public Am2900Machine(LogicModelModifiable model, AbstractAm2900MachineDefinition am2900MachineDefinition)
	{
		this.machineDefinition = am2900MachineDefinition;
		this.logicModel = model;
		this.am2900 = IndirectModelComponentCreator.createComponent(logicModel,
				"resloader:Am2900Loader:jsonres:net/mograsim/logic/model/am2900/components/Am2900.json", "Am2900");
		this.amicListeners = new HashSet<>();

		CoreModelParametersBuilder paramsBuilder = CoreModelParameters.builder();
		paramsBuilder.gateProcessTime = 50;
		paramsBuilder.hardcodedComponentProcessTime = paramsBuilder.gateProcessTime * 5;
		paramsBuilder.wireTravelTime = 10;
		params = paramsBuilder.build();

		mainMemory = new AssignableMainMemory(new StandardMainMemory(am2900MachineDefinition.getMainMemoryDefinition()));
		instMemory = new AssignableMicroInstructionMemory(
				new StandardMicroInstructionMemory(am2900MachineDefinition.getMicroInstructionMemoryDefinition()));
		mprom = new AssignableMPROM(new StandardMPROM(am2900MachineDefinition.getMPROMDefinition()));
		timeline = LogicCoreAdapter.convert(logicModel, params);
		am2900.setHighLevelState("ram.memory_binding", mainMemory);
		am2900.setHighLevelState("mpm.memory_binding", instMemory);
		am2900.setHighLevelState("mprom.memory_binding", mprom);
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
		am2900.setHighLevelState("muir_2.q", jzMI.toBitVector());
		Bit regsValue = machineDefinition.expert ? Bit.U : Bit.ZERO;
		setRegistersTo(machineDefinition.getUnsortedRegisters(), regsValue);
		setRegisterGroupTo(machineDefinition.getRegisterGroups(), regsValue);
		// TODO reset latches?
	}

	private void setRegistersTo(List<Register> registers, Bit value)
	{
		for (Register r : registers)
			if (r != muInstrRegister.instance)// don't reset; sometimes causes a glitch
				setRegister(r, BitVector.of(value, r.getWidth()));
	}

	private void setRegisterGroupTo(List<RegisterGroup> registerGroups, Bit value)
	{
		for (RegisterGroup rg : registerGroups)
		{
			setRegistersTo(rg.getRegisters(), value);
			setRegisterGroupTo(rg.getSubGroups(), value);
		}
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
	public CoreModelParameters getCoreModelParameters()
	{
		return params;
	}

	@Override
	public CoreClock getClock()
	{
		return clock;
	}

	@Override
	public BitVector getRegister(Register r)
	{
		return castAm2900Register(r).read(am2900);
	}

	@Override
	public void setRegister(Register r, BitVector value)
	{
		castAm2900Register(r).write(am2900, value);
	}

	@Override
	public void addRegisterListener(Register r, Consumer<BitVector> listener)
	{
		castAm2900Register(r).addListener(am2900, listener);
	}

	@Override
	public void removeRegisterListener(Register r, Consumer<BitVector> listener)
	{
		castAm2900Register(r).removeListener(am2900, listener);
	}

	private static Am2900Register castAm2900Register(Register r)
	{
		if (r instanceof Am2900Register)
			return (Am2900Register) r;
		throw new IllegalArgumentException("Not a register of an Am2900Machine: " + r);
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
	public AssignableMPROM getMPROM()
	{
		return mprom;
	}

	@Override
	public long getActiveMicroInstructionAddress()
	{
		return activeInstructionAddress;
	}

	private long getCurrentMicroInstructionAddress()
	{
		BitVector vector = (BitVector) am2900.getHighLevelState("mpm_address");
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