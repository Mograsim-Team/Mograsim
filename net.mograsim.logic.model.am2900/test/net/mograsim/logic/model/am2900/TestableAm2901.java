package net.mograsim.logic.model.am2900;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Stream;

public interface TestableAm2901
{
	void setup();

	Result run();

	void setDest(Am2901_Dest dest);

	void setFunc(Am2901_Func func);

	void setSrc(Am2901_Src src);

	void setReg_A(String val_4_bit);

	void setReg_B(String val_4_bit);

	void setCarryIn(String val_1_bit);

	void setNotOutEnable(String val_1_bit);

	void setD(String val_4_bit);

	void setQ_0(String val_1_bit);

	void setQ_3(String val_1_bit);

	void setRAM_0(String val_1_bit);

	void setRAM_3(String val_1_bit);

	void clockOn(boolean isClockOn);

	void setDirectly(Register r, String val_4_bit);

	String getQ_0();

	String getQ_3();

	String getRAM_0();

	String getRAM_3();

	String getNotP();

	String getNotG();

	String getCarryOut();

	String getSign();

	String getZero();

	String getOverflow();

	String getY();

	String getDirectly(Register r);

	default void assertRunSuccess()
	{
		assertEquals(Result.SUCCESS, run());
	}

	default void assertFullCycleSuccess()
	{
		assertRunSuccess();
		clockOn(false);
		assertRunSuccess();
		clockOn(true);
		assertRunSuccess();
	}

	public enum Result
	{
		SUCCESS, OUT_OF_TIME, ERROR;
	}

	public enum Am2901_Dest
	{
		QREG, NOP, RAMA, RAMF, RAMQD, RAMD, RAMQU, RAMU;

		public boolean doesShift()
		{
			return ordinal() >= 4;
		}

		public int getShiftDir()
		{
			return doesShift() ? (ordinal() < 6 ? -1 : 1) : 0;
		}

		public int getI7()
		{
			return this.ordinal() >> 1 & 1;
		}
	}

	public enum Am2901_Func
	{
		ADD, SUBR, SUBS, OR, AND, NOTRS, EXOR, EXNOR;
	}

	public enum Am2901_Src
	{
		AQ, AB, ZQ, ZB, ZA, DA, DQ, DZ;
	}

	public enum Register
	{
		r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, rA, rB, rC, rD, rE, rF, Q;

		public String toBitString()
		{
			if (this.ordinal() > 0xF)
				throw new UnsupportedOperationException();
			return TestUtil.to4bitBin(this.ordinal());
		}

		public static Stream<Register> stream()
		{
			return Arrays.stream(values());
		}
	}
}
