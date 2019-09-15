package net.mograsim.logic.model.am2900.machine;

import java.util.Arrays;
import java.util.Optional;

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
	private final static MnemonicFamily am2910Instructions = new MnemonicFamily(true, "JZ", "JZ", "CJS", "JMAP", "PUSH", "JSRP", "CJV",
			"JRP", "RFCT", "RPCT", "CRTN", "CJPP", "LDCT", "LOOP", "CONT", "TWB");
	private final static MnemonicFamily am2904StatusInstructions = new MnemonicFamily(true, "MI_Zero",
			new String[] { "MI_Zero", "MI_NotZero", "MI_UGTEQ", "MI_ULT", "MI_UGT", "MI_ULTEQ", "MI_SGTEQ", "MI_SLT", "MI_SGT", "MI_SLTEQ",
					"MA_Zero", "MA_NotZero", "MA_UGTEQ", "MA_ULT", "MA_UGT", "MA_ULTEQ", "MA_SGTEQ", "MA_SLT", "MA_SGT", "MA_SLTEQ" },
			new long[] { 0b01_0101, 0b01_0100, 0b01_1101, 0b01_1100, 0b01_1110, 0b01_1111, 0b01_0010, 0b01_0011, 0b01_0000, 0b01_0001,
					0b10_0101, 0b10_0100, 0b10_1101, 0b10_1100, 0b10_1110, 0b10_1111, 0b10_0010, 0b10_0011, 0b10_0000, 0b10_0001 },
			6);
	private final static MnemonicFamily am2904ShiftInstructions = new MnemonicFamily(true, "SL", "SL", "SH", "SCONI", "SDH", "SDC", "SDN",
			"SDL", "SDCO", "SRCO", "SRCIO", "SR", "SDIC", "SDRCI", "SDRCO", "SDXOR", "SDR");
	private final static MnemonicFamily am2904CarryInstructions = new MnemonicFamily(true, "CI0",
			new String[] { "CI0", "CI1", "CIX", "CIC" }, new long[] { 0b00, 0b01, 0b10, 0b11 }, 2);
	private final static MnemonicFamily am2901DestInstructions = new MnemonicFamily(true, "NOP", "QREG", "NOP", "RAMA", "RAMF", "RAMQD",
			"RAMD", "RAMQU", "RAMU");
	private final static MnemonicFamily am2901FuncInstructions = new MnemonicFamily(true, "ADD", "ADD", "SUBR", "SUBS", "OR", "AND",
			"NOTRS", "EXOR", "EXNOR");
	private final static MnemonicFamily am2901SrcInstructions = new MnemonicFamily(true, "AB", "AQ", "AB", "ZQ", "ZB", "ZA", "DA", "DQ",
			"DZ");
	private final static MnemonicFamily interruptInstructions = new MnemonicFamily("X",
			new MnemonicPair("X", BitVector.of(Bit.ZERO, 4))/* TODO */);
	private final static BooleanClassification hL = new BooleanClassification(true, "H", "L");
	private final static BooleanClassification registerSelect = new BooleanClassification(false, "MR", "IR");
	private final static IntegerClassification register = new IntegerClassification(0, 4);

	private final static ParameterClassification[] classes = { new BooleanClassification(true, "Dis", "IE"),
			new IntegerClassification(0, 16), new BooleanClassification(false, "D", "K"), interruptInstructions,
			new BooleanClassification(true, "H", "AB"), registerSelect, register, registerSelect, register, am2901DestInstructions,
			am2901FuncInstructions, am2901SrcInstructions, new IntegerClassification(0, 12), am2910Instructions,
			new BooleanClassification(true, "PS", "C"), am2904StatusInstructions, hL, hL, am2904ShiftInstructions, am2904CarryInstructions,
			new BooleanClassification(true, "H", "DB"), new BooleanClassification(true, "H", "E"), hL,
			new BooleanClassification(true, "H", "E"), new BooleanClassification(true, "H", "I"), new BooleanClassification(true, "R", "W"),
			hL };

	private final static String[] paramDesc = { "Allow interrupts?", "Interrupt instructions; omitted for simplicity",
			"Get D-input from data bus/constant value", "Constant value", "Operand sources for ALU operation", "ALU operation",
			"Destination of ALU calculation", "Register for A-operand", "Get A-operand from instruction register/micro instruction?",
			"Register for B-operand", "Get B-operand from instruction register/micro instruction?",
			"Put data from Y-output on address bus?", "Put data from Y-output on data bus?", "Source for carry-in", "Shift instructions",
			"Modify micro status register?", "Modify macro status register?", "Operations on the status register",
			"Is conditional jump enabled?", "Instructions for the micro instruction pointer", "Absolute address of a micro instruction",
			"Load instruction pointer from data bus?", "Put instruction pointer on data bus?", "Increment instruction pointer?",
			"Put instruction pointer on address bus?", "Load instruction register from data bus?", "Read from/Write to main memory?" };

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

	@Override
	public Optional<String> getParameterDescription(int index)
	{
		return Optional.of(paramDesc[index]);
	}

	public static void main(String[] args)
	{
		String s = "new BooleanClassification(\"R\", \"W\"), hL, new BooleanClassification(\"H\", \"E\"),\r\n"
				+ "			new BooleanClassification(\"H\", \"I\"), new BooleanClassification(\"H\", \"E\"), hL, new IntegerClassification(12), am2910Instructions,\r\n"
				+ "			new BooleanClassification(\"PS\", \"C\"), am2904StatusInstructions, hL, hL, am2904ShiftInstructions, am2904CarryInstructions,\r\n"
				+ "			new BooleanClassification(\"H\", \"DB\"), new BooleanClassification(\"H\", \"AB\"), registerSelect, register, registerSelect, register,\r\n"
				+ "			am2901DestInstructions, am2901FuncInstructions, am2901SrcInstructions, new IntegerClassification(16),\r\n"
				+ "			new BooleanClassification(\"D\", \"K\"), interruptInstructions, new BooleanClassification(\"Dis\", \"IE\")";
		s = s.replaceAll("[\r\n\t]", "");
		System.out.print(Arrays.stream(s.split(", new")).reduce((a, b) -> b + ", new" + a));
//		System.out.println(Arrays.stream(paramDesc).reduce("", (a, b) -> String.format("\"%s\", %s", b, a)));
	}
}
