package net.mograsim.logic.ui.am2900;

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

	void toogleClock();

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
}
