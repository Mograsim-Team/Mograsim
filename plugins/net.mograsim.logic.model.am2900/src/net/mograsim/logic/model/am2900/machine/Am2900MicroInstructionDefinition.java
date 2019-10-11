package net.mograsim.logic.model.am2900.machine;

import java.util.Arrays;
import java.util.Optional;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.BooleanClassification;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.MnemonicFamily.MnemonicFamilyBuilder;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public class Am2900MicroInstructionDefinition implements MicroInstructionDefinition
{
	public static final Am2900MicroInstructionDefinition instance = new Am2900MicroInstructionDefinition();

	private static final BooleanClassification interruptEnable = new BooleanClassification(false, "IE", "Dis");
	// not implemented, because not documented.
	private static final MnemonicFamily interruptInstructions = new MnemonicFamilyBuilder(4).addX().setXDefault().build();
	private static final BooleanClassification kmux = new BooleanClassification("D", "K");
	private static final MnemonicFamily am2901SrcInstructions = new MnemonicFamilyBuilder(3).addX().setXDefault()
			.add("AQ", "AB", "ZQ", "ZB", "ZA", "DA", "DQ", "DZ").build();
	private static final MnemonicFamily am2901FuncInstructions = new MnemonicFamilyBuilder(3).addX().setXDefault()
			.add("ADD", "SUBR", "SUBS", "OR", "AND", "NOTRS", "EXOR", "EXNOR").build();
	private static final MnemonicFamily am2901DestInstructions = new MnemonicFamilyBuilder(3).addX()
			.add("QREG", "NOP", "RAMA", "RAMF", "RAMQD", "RAMD", "RAMQU", "RAMU").setDefault("NOP").build();
	private static final IntegerClassification register = new IntegerClassification(0, 4);
	private static final BooleanClassification registerSelect = new BooleanClassification("MR", "IR");
	private static final BooleanClassification abus = new BooleanClassification(true, "H", "AB");
	private static final BooleanClassification dbus = new BooleanClassification(true, "H", "DB");
	private static final MnemonicFamily am2904CarryInstructions = new MnemonicFamilyBuilder(2).addX().setXDefault()
			.add("CI0", "CI1", "CIX", "CIC").build();
	private static final MnemonicFamily am2904ShiftInstructions = new MnemonicFamilyBuilder(4).addX().setXDefault()
			.add("RSL", "RSH", "RSCONI", "RSDH", "RSDC", "RSDN", "RSDL", "RSDCO", "RSRCO", "RSRCIO", "RSR", "RSDIC", "RSDRCI", "RSDRCO",
					"RSDXOR", "RSDR")
			.add("LSLCO", "LSHCO", "LSL", "LSH", "LSDLCO", "LSDHCO", "LSDL", "LSDH", "LSCRO", "LSCRIO", "LSR", "LSLICI", "LSDCIO", "LSDRCO",
					"LSDCI", "LDSR")
			.build();
	// TODO: Maybe "X" and "notX" are swapped.
	private static final MnemonicFamily am2904StatusInstructions = new MnemonicFamilyBuilder(6).addX().setXDefault()
			.add(new String[] { "LOAD", "MI_Zero", "MI_NotZero", "MI_UGTEQ", "MI_ULT", "MI_UGT", "MI_ULTEQ", "MI_SGTEQ", "MI_SLT", "MI_SGT",
					"MI_SLTEQ", "MA_Zero", "MA_NotZero", "MA_UGTEQ", "MA_ULT", "MA_UGT", "MA_ULTEQ", "MA_SGTEQ", "MA_SLT", "MA_SGT",
					"MA_SLTEQ" },
					new long[] { 0b01_0000, 0b01_0101, 0b01_0100, 0b01_1101, 0b01_1100, 0b01_1110, 0b01_1111, 0b01_0010, 0b01_0011,
							0b01_0000, 0b01_0001, 0b10_0101, 0b10_0100, 0b10_1101, 0b10_1100, 0b10_1110, 0b10_1111, 0b10_0010, 0b10_0011,
							0b10_0000, 0b10_0001 })
			.add("LoadM_LoadY_\u00b5_NxorOVRorZ", "Set_Set_\u00b5_NxnorOVRornotZ", "Swap_Swap_\u00b5_NxorOVR",
					"Reset_Reset_\u00b5_NxnorOVR", "Load_LoadForShiftThroughOvr_\u00b5_Z", "Load_Invert_\u00b5_notZ",
					"LoadOvrRetain_Load_\u00b5_OVR", "LoadOvrRetain_Load_\u00b5_notOVR", "ResetZ_LoadCarryInvert_\u00b5_CorZ",
					"SetZ_LoadCarryInvert_\u00b5_notCandnotZ", "ResetC_Load_\u00b5_C", "SetC_Load_\u00b5_notC",
					"ResetN_Load_\u00b5_notCorZ", "SetN_Load_\u00b5_CandnotZ", "ResetOvr_Load_IM_NxorN", "SetOvr_Load_IM_NxnorN",
					"Load_Load_\u00b5_NxorOVRorZ", "Load_Load_\u00b5_NxnorOVRornotZ", "Load_Load_\u00b5_NxorOVR",
					"Load_Load_\u00b5_NxnorOVR", "Load_Load_\u00b5_Z", "Load_Load_\u00b5_notZ", "Load_Load_\u00b5_OVR",
					"Load_Load_\u00b5_notOVR", "LoadCarryInvert_LoadCarryInvert_\u00b5_CorZ",
					"LoadCarryInvert_LoadCarryInvert_\u00b5_notCandnotZ", "Load_Load_\u00b5_C", "Load_Load_\u00b5_notC",
					"Load_Load_\u00b5_notCorZ", "Load_Load_\u00b5_CandnotZ", "Load_Load_\u00b5_N", "Load_Load_\u00b5_notN",
					"Load_Load_M_NxorOVRorZ", "Load_Load_M_NxnorOVRornotZ", "Load_Load_M_NxorOVR", "Load_Load_M_NxnorOVR", "Load_Load_M_Z",
					"Load_Load_M_notZ", "Load_Load_M_OVR", "Load_Load_M_notOVR", "LoadCarryInvert_LoadCarryInvert_M_CorZ",
					"LoadCarryInvert_LoadCarryInvert_M_notCandnotZ", "Load_Load_M_C", "Load_Load_M_notC", "Load_Load_M_notCorZ",
					"Load_Load_M_CandnotZ", "Load_Load_M_N", "Load_Load_M_notN", "Load_Load_I_NxorOVRorZ", "Load_Load_I_NxnorOVRornotZ",
					"Load_Load_I_NxorOVR", "Load_Load_I_NxnorOVR", "Load_Load_I_Z", "Load_Load_I_notZ", "Load_Load_I_OVR",
					"Load_Load_I_notOVR", "LoadCarryInvert_LoadCarryInvert_I_notCorZ", "LoadCarryInvert_LoadCarryInvert_I_CandnotZ",
					"Load_Load_I_C", "Load_Load_I_notC", "Load_Load_I_notCorZ", "Load_Load_I_CandnotZ", "Load_Load_I_N", "Load_Load_I_notN")
			.build();
	// 00b5 = micro symbol
	private static final BooleanClassification ccen = new BooleanClassification(true, "PS", "C");
	private static final MnemonicFamily am2910Instructions = new MnemonicFamilyBuilder(4).addX()
			.add("JZ", "CJS", "JMAP", "CJP", "PUSH", "JSRP", "CJV", "JRP", "RFCT", "RPCT", "CRTN", "CJPP", "LDCT", "LOOP", "CONT", "TWB")
			.setDefault("CONT").build();

	private static final IntegerClassification constant_12bit = new IntegerClassification(0, 12);
	private static final IntegerClassification constant_16bit = new IntegerClassification(0, 16);
	private static final BooleanClassification hE = new BooleanClassification(true, "H", "E");
	private static final BooleanClassification hI = new BooleanClassification(true, "H", "I");
	private static final BooleanClassification hL = new BooleanClassification(true, "H", "L");
	private static final BooleanClassification rW = new BooleanClassification(true, "R", "W");

	private static final ParameterClassification[] classes = { interruptEnable, interruptInstructions, kmux, constant_16bit,
			am2901SrcInstructions, am2901FuncInstructions, am2901DestInstructions, register, registerSelect, register, registerSelect, abus,
			dbus, am2904CarryInstructions, am2904ShiftInstructions, hL, hL, am2904StatusInstructions, ccen, am2910Instructions,
			constant_12bit, hL, hE, hI, hE, hL, rW };

	private static final String[] paramDesc = { "Allow interrupts?", "Interrupt instructions; omitted for simplicity",
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
		return Arrays.copyOf(classes, classes.length);
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

	private Am2900MicroInstructionDefinition()
	{
	}
}
