package net.mograsim.logic.model.am2900.am2901;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.util.SwitchWithDisplay;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper.DebugState;
import net.mograsim.logic.model.am2900.util.TestUtil;
import net.mograsim.logic.model.model.components.ModelComponent;

public class TestableAm2901Impl implements TestableAm2901
{
	private ModelComponent am2901;
	private CoreManualSwitch I;
	private CoreManualSwitch C;
	private CoreManualSwitch Cn;
	private CoreManualSwitch D;
	private CoreManualSwitch A;
	private CoreManualSwitch B;
	private CoreBitDisplay Y;
	private CoreBitDisplay F_0, Cn_4, OVR, F3;
	private SwitchWithDisplay RAMn, RAMn_3, Qn, Qn_3;

	private final TestEnvironmentHelper testHelper = new TestEnvironmentHelper(this, "Am2901");

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
		BitVector oldI = I.getValues();
		I.setState(TestUtil.of(dest.ordinal(), 3).concat(oldI.subVector(3)));
	}

	@Override
	public void setFunc(Am2901_Func func)
	{
		BitVector oldI = I.getValues();
		I.setState(oldI.subVector(0, 3).concat(TestUtil.of(func.ordinal(), 3)).concat(oldI.subVector(6)));
	}

	@Override
	public void setSrc(Am2901_Src src)
	{
		BitVector oldI = I.getValues();
		I.setState(oldI.subVector(0, 6).concat(TestUtil.of(src.ordinal(), 3)));
	}

	@Override
	public void setReg_A(String val_4_bit)
	{
		A.setState(BitVector.parse(val_4_bit));
	}

	@Override
	public void setReg_B(String val_4_bit)
	{
		B.setState(BitVector.parse(val_4_bit));
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
		D.setState(BitVector.parse(val_4_bit));
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
		return Y.getDisplayedValue().toString();
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
