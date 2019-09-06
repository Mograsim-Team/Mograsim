package net.mograsim.logic.model.am2900.machine;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.BooleanClassification;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.MnemonicFamily.MnemonicPair;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public class Am2900MicroInstructionDefinition implements MicroInstructionDefinition
{
	private final static MnemonicFamily am2910Instructions = new MnemonicFamily(new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2904StatusInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2904ShiftInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2904CarryInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2901DestInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2901FuncInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily am2901SrcInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static MnemonicFamily interruptInstructions = new MnemonicFamily(
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static BooleanClassification hL = new BooleanClassification("H", "L");
	private final static BooleanClassification registerSelect = new BooleanClassification("MR", "IR");
	private final static IntegerClassification register = new IntegerClassification(4);

	private final static ParameterClassification[] classes = { new BooleanClassification("R", "W"), hL, new BooleanClassification("H", "E"),
			new BooleanClassification("H", "I"), new BooleanClassification("H", "E"), hL, new IntegerClassification(11), am2910Instructions,
			new BooleanClassification("PS", "C"), am2904StatusInstructions, hL, hL, am2904ShiftInstructions, am2904CarryInstructions,
			new BooleanClassification("H", "DB"), new BooleanClassification("H", "AB"), registerSelect, register, registerSelect, register,
			am2901DestInstructions, am2901FuncInstructions, am2901SrcInstructions, new IntegerClassification(16),
			new BooleanClassification("D", "K"), interruptInstructions, new BooleanClassification("Dis", "IE") };

	@Override
	public ParameterClassification[] getParameterClassifications()
	{
		return classes;
	}

	@Override
	public ParameterClassification getParameterClassification(int index)
	{
		return classes[index];
	}

}
