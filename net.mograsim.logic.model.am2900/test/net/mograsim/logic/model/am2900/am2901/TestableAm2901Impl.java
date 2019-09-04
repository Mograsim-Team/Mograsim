package net.mograsim.logic.model.am2900.am2901;

import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.util.SwitchWithDisplay;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper.DebugState;
import net.mograsim.logic.model.am2900.util.TestUtil;
import net.mograsim.logic.model.model.components.GUIComponent;

public class TestableAm2901Impl implements TestableAm2901
{
	private GUIComponent am2901;
	private ManualSwitch I8, I7, I6, I5, I4, I3, I2, I1, I0;
	private ManualSwitch C;
	private ManualSwitch Cn;
	private ManualSwitch D1, D2, D3, D4;
	private ManualSwitch A0, A1, A2, A3;
	private ManualSwitch B0, B1, B2, B3;
	private BitDisplay Y1, Y2, Y3, Y4;
	private BitDisplay F_0, Cn_4, OVR, F3;
	private SwitchWithDisplay RAMn, RAMn_3, Qn, Qn_3;

	private final TestEnvironmentHelper testHelper = new TestEnvironmentHelper(this, "GUIAm2901");

	@Override
	public Result run()
	{
		return testHelper.run();
	}

	@Override
	public void setup()
	{
		testHelper.setup(DebugState.NO_DEBUG);
	}

	@Override
	public void setDest(Am2901_Dest dest)
	{
		var bits = TestUtil.of(dest.ordinal(), 3);
		I8.setState(bits.getLSBit(2));
		I7.setState(bits.getLSBit(1));
		I6.setState(bits.getLSBit(0));
	}

	@Override
	public void setFunc(Am2901_Func func)
	{
		var bits = TestUtil.of(func.ordinal(), 3);
		I5.setState(bits.getLSBit(2));
		I4.setState(bits.getLSBit(1));
		I3.setState(bits.getLSBit(0));
	}

	@Override
	public void setSrc(Am2901_Src src)
	{
		var bits = TestUtil.of(src.ordinal(), 3);
		I2.setState(bits.getLSBit(2));
		I1.setState(bits.getLSBit(1));
		I0.setState(bits.getLSBit(0));
	}

	@Override
	public void setReg_A(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		A3.setState(bits.getLSBit(3));
		A2.setState(bits.getLSBit(2));
		A1.setState(bits.getLSBit(1));
		A0.setState(bits.getLSBit(0));
	}

	@Override
	public void setReg_B(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		B3.setState(bits.getLSBit(3));
		B2.setState(bits.getLSBit(2));
		B1.setState(bits.getLSBit(1));
		B0.setState(bits.getLSBit(0));
	}

	@Override
	public void setCarryIn(String val_1_bit)
	{
		Cn.setState(Bit.parse(val_1_bit));
	}

	@Override
	public void setNotOutEnable(String val_1_bit)
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public void setD(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		D4.setState(bits.getLSBit(3));
		D3.setState(bits.getLSBit(2));
		D2.setState(bits.getLSBit(1));
		D1.setState(bits.getLSBit(0));
	}

	@Override
	public void setQ_0(String val_1_bit)
	{
		Qn.setState(Bit.parse(val_1_bit).toVector());
	}

	@Override
	public void setQ_3(String val_1_bit)
	{
		Qn_3.setState(Bit.parse(val_1_bit).toVector());
	}

	@Override
	public void setRAM_0(String val_1_bit)
	{
		RAMn.setState(Bit.parse(val_1_bit).toVector());
	}

	@Override
	public void setRAM_3(String val_1_bit)
	{
		RAMn_3.setState(Bit.parse(val_1_bit).toVector());
	}

	@Override
	public void clockOn(boolean isClockOn)
	{
		C.setState(isClockOn ? Bit.ONE : Bit.ZERO);
	}

	@Override
	public String getQ_0()
	{
		return Qn.getDisplayedValue().toString();
	}

	@Override
	public String getQ_3()
	{
		return Qn_3.getDisplayedValue().toString();
	}

	@Override
	public String getRAM_0()
	{
		return RAMn.getDisplayedValue().toString();
	}

	@Override
	public String getRAM_3()
	{
		return RAMn_3.getDisplayedValue().toString();
	}

	@Override
	public String getNotP()
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public String getNotG()
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public String getCarryOut()
	{
		return Cn_4.getDisplayedValue().toString();
	}

	@Override
	public String getSign()
	{
		return F3.getDisplayedValue().toString();
	}

	@Override
	public String getZero()
	{
		return F_0.getDisplayedValue().toString();
	}

	@Override
	public String getOverflow()
	{
		return OVR.getDisplayedValue().toString();
	}

	@Override
	public String getY()
	{
		var y3 = Y4.getDisplayedValue();
		var y2 = Y3.getDisplayedValue();
		var y1 = Y2.getDisplayedValue();
		var y0 = Y1.getDisplayedValue();
		return y3.concat(y2).concat(y1).concat(y0).toString();
	}

	@Override
	public void setDirectly(Register r, String val_4_bit)
	{
		am2901.setHighLevelState(regToStateID(r), BitVector.parse(val_4_bit));
	}

	@Override
	public String getDirectly(Register r)
	{
		return ((BitVector) am2901.getHighLevelState(regToStateID(r))).toString();
	}

	private static String regToStateID(Register r)
	{
		if (r == Register.Q)
			return "qreg.q";
		return "regs.c" + r.toBitString() + ".q";
	}

	@Override
	public TestEnvironmentHelper getTestEnvironmentHelper()
	{
		return testHelper;
	}
}
