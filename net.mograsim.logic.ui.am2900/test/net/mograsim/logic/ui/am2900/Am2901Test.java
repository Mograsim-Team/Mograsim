package net.mograsim.logic.ui.am2900;

import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Dest.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Func.*;
import static net.mograsim.logic.ui.am2900.TestableAm2901.Am2901_Src.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.mograsim.logic.ui.am2900.TestableAm2901.Result;

public class Am2901Test
{
	private TestableAm2901 am2901;

	@BeforeEach
	void initialize()
	{
		createAndSetup();
		setRegistersToZero();
	}

	@Test
	void testInit()
	{
		assertEquals("0", am2901.getCarryOut());
		assertEquals("0", am2901.getOverflow());
		assertEquals("0", am2901.getSign());
		assertEquals("1", am2901.getZero());
		assertEquals("0000", am2901.getY());
		assertEquals("0", am2901.getQ_0());
		assertEquals("0", am2901.getQ_3());
		assertEquals("0", am2901.getRAM_0());
		assertEquals("0", am2901.getRAM_3());
	}

	void createAndSetup()
	{
		am2901 = new TestableAm2901Impl();
		am2901.setup();
	}

	void setRegistersToZero()
	{
		setInputsToZero();
		for (Regsiter r : Regsiter.values())
		{
			setRegisterToZero(r);
		}
	}

	void setRegisterToZero(Regsiter r)
	{
		System.out.println("Setting reg " + r + " to zero");
		am2901.setD("0000");
		am2901.setSrc(DZ);
		am2901.setFunc(AND);
		if (r == Regsiter.Q)
		{
			am2901.setDest(QREG);
		} else
		{
			am2901.setReg_B(r.toBitString());
			am2901.setDest(RAMF);
		}
		assertRunSuccess();
		am2901.toogleClock();
		assertRunSuccess();
		am2901.toogleClock();
		assertRunSuccess();
	}

	void setInputsToZero()
	{
		am2901.setCarryIn("0");
		am2901.setQ_0("0");
		am2901.setQ_3("0");
		am2901.setRAM_0("0");
		am2901.setRAM_3("0");
		am2901.setReg_A("0000");
		am2901.setReg_B("0000");
		am2901.setD("0000");
		am2901.setSrc(AB);
		am2901.setFunc(ADD);
		am2901.setDest(QREG);
//		am2901.setNotOutEnable("0"); TODO
		assertRunSuccess();
	}

	void assertRunSuccess()
	{
		assertEquals(Result.SUCCESS, am2901.run());
	}

	public enum Regsiter
	{
		r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, rA, rB, rC, rD, rE, rF, Q;

		public String toBitString()
		{
			if (this.ordinal() > 0xF)
				throw new UnsupportedOperationException();
			return TestUtil.to4bitBin(this.ordinal());
		}
	}
}
