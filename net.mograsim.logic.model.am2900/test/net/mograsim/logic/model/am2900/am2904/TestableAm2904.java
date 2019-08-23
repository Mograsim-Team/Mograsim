package net.mograsim.logic.model.am2900.am2904;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.model.am2900.TestableCircuit;

public interface TestableAm2904 extends TestableCircuit
{

	void setInstruction(Am2904_Inst inst);

	void setCarry(Am2904_Carry carry);

	void setShiftCode(String val_4_bit);

	void setI10(Am2904_ShiftDir dir);

	void setCX(String val_1_bit);

	void setY3(String val_1_bit);

	void setIZ(String val_1_bit);

	void setIC(String val_1_bit);

	void setIOVR(String val_1_bit);

	void setIN(String val_1_bit);

	default void setI(String z_c_ovr_n)
	{
		setIZ(z_c_ovr_n.substring(0, 1));
		setIC(z_c_ovr_n.substring(1, 2));
		setIOVR(z_c_ovr_n.substring(2, 3));
		setIN(z_c_ovr_n.substring(3, 4));
	}

	void set_CEM(String val_1_bit);

	void set_CEµ(String val_1_bit);

	void set_OEY(String val_1_bit);

	void set_OECT(String val_1_bit);

	void set_SE(String val_1_bit);

	void set_EZ(String val_1_bit);

	void set_EC(String val_1_bit);

	void set_EOVR(String val_1_bit);

	void set_EN(String val_1_bit);

	default void set_E(String z_c_ovr_n)
	{
		set_EZ(z_c_ovr_n.substring(0, 1));
		set_EC(z_c_ovr_n.substring(1, 2));
		set_EOVR(z_c_ovr_n.substring(2, 3));
		set_EN(z_c_ovr_n.substring(3, 4));
	}

	void setSIO0(String val_1_bit);

	void setSIO3(String val_1_bit);

	void setQIO0(String val_1_bit);

	void setQIO3(String val_1_bit);

	String getC0();

	String getCT();

	String getY3();

	String getSIO0();

	String getSIO3();

	String getQIO0();

	String getQIO3();

	public enum Am2904_ShiftDir
	{
		RIGHT, LEFT;
	}

	public enum Am2904_Carry
	{
		CI0, CI1, CX, USE_SR;
	}

	/**
	 * Pattern:<br>
	 * [microSR]_[machineSR]_[condCodeSource]_[condCode]<br>
	 * <ul>
	 * <li>Load = load from I inputs (Am2904s)</li>
	 * <li>condCodeSource = µ, M, I (and a special case: I and M)</li>
	 * <li>condCode = the boolean term defining the CT output</li>
	 * </ul>
	 */
	public enum Am2904_Inst
	{
		// @formatter:off
		LoadM_LoadY_µ_NxorOVRorZ(0_00),
		Set_Set_µ_NxnorOVRornotZ(0_01),
		Swap_Swap_µ_NxorOVR(0_02),
		Reset_Reset_µ_NxnorOVR(0_03),
		Load_LoadForShiftThroughOvr_µ_Z(0_04),
		Load_Invert_µ_notZ(0_05),
		LoadOvrRetain_Load_µ_OVR(0_06),
		LoadOvrRetain_Load_µ_notOVR(0_07),
		ResetZ_LoadCarryInvert_µ_CorZ(0_10),
		SetZ_LoadCarryInvert_µ_notCandnotZ(0_11),
		ResetC_Load_µ_C(0_12),
		SetC_Load_µ_notC(0_13),
		ResetN_Load_µ_notCorZ(0_14),
		SetN_Load_µ_CandnotZ(0_15),
		ResetOvr_Load_IM_NxorN(0_16),
		SetOvr_Load_IM_NxnorN(0_17),
		Load_Load_µ_NxorOVRorZ(0_20),
		Load_Load_µ_NxnorOVRornotZ(0_21),
		Load_Load_µ_NxorOVR(0_22),
		Load_Load_µ_NxnorOVR(0_23),
		Load_Load_µ_Z(0_24),
		Load_Load_µ_notZ(0_25),
		Load_Load_µ_OVR(0_26),
		Load_Load_µ_notOVR(0_27),
		LoadCarryInvert_LoadCarryInvert_µ_CorZ(0_30),
		LoadCarryInvert_LoadCarryInvert_µ_notCandnotZ(0_31),
		Load_Load_µ_C(0_32),
		Load_Load_µ_notC(0_33),
		Load_Load_µ_notCorZ(0_34),
		Load_Load_µ_CandnotZ(0_35),
		Load_Load_µ_N(0_36),
		Load_Load_µ_notN(0_37),
		Load_Load_M_NxorOVRorZ(0_40),
		Load_Load_M_NxnorOVRornotZ(0_41),
		Load_Load_M_NxorOVR(0_42),
		Load_Load_M_NxnorOVR(0_43),
		Load_Load_M_Z(0_44),
		Load_Load_M_notZ(0_45),
		Load_Load_M_OVR(0_46),
		Load_Load_M_notOVR(0_47),
		LoadCarryInvert_LoadCarryInvert_M_CorZ(0_50),
		LoadCarryInvert_LoadCarryInvert_M_notCandnotZ(0_51),
		Load_Load_M_C(0_52),
		Load_Load_M_notC(0_53),
		Load_Load_M_notCorZ(0_54),
		Load_Load_M_CandnotZ(0_55),
		Load_Load_M_N(0_56),
		Load_Load_M_notN(0_57),
		Load_Load_I_NxorOVRorZ(0_60),
		Load_Load_I_NxnorOVRornotZ(0_61),
		Load_Load_I_NxorOVR(0_62),
		Load_Load_I_NxnorOVR(0_63),
		Load_Load_I_Z(0_64),
		Load_Load_I_notZ(0_65),
		Load_Load_I_OVR(0_66),
		Load_Load_I_notOVR(0_67),
		LoadCarryInvert_LoadCarryInvert_I_notCorZ(0_70),
		LoadCarryInvert_LoadCarryInvert_I_CandnotZ(0_71),
		Load_Load_I_C(0_72),
		Load_Load_I_notC(0_73),
		Load_Load_I_notCorZ(0_74),
		Load_Load_I_CandnotZ(0_75),
		Load_Load_I_N(0_76),
		Load_Load_I_notN(0_77);
		// @formatter:on

		public final int code;

		private Am2904_Inst(int code)
		{
			this.code = code;
		}

		private static final Map<Integer, Am2904_Inst> instCodes;

		static
		{
			Map<Integer, Am2904_Inst> instCodesMod = new HashMap<>();
			Am2904_Inst[] vals = values();
			for (Am2904_Inst inst : vals)
			{
				assert inst.code == inst.ordinal();
				instCodesMod.put(inst.code, inst);
			}
			if (instCodesMod.size() != 64)
				throw new IllegalStateException("There must be exactly 64 Am2904 instruction codes, but was " + instCodesMod.size());
			instCodes = Collections.unmodifiableMap(instCodesMod);
		}

		public static Am2904_Inst getInstFor(int I_543210)
		{
			Am2904_Inst inst = instCodes.get(I_543210);
			if (inst != null)
				return inst;
			throw new IllegalArgumentException("Invaild Am2904 instruction code: " + I_543210);
		}

		public int getBlock()
		{
			return code >> 4;
		}

		public boolean isLoadCarryInvert()
		{
			return (code & 0b001_110) == 0b001_000;
		}
	}
}
