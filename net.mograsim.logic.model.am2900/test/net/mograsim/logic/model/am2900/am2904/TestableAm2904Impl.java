package net.mograsim.logic.model.am2900.am2904;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.util.SwitchWithDisplay;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper.DebugState;
import net.mograsim.logic.model.model.components.GUIComponent;

public class TestableAm2904Impl implements TestableAm2904
{

	private GUIComponent am2904;
	private CoreManualSwitch I;
	private CoreManualSwitch C;
	private CoreManualSwitch Cx;
	private CoreManualSwitch IC, IN, IOVR, IZ;
	private CoreManualSwitch _CEM, _CEmu;
	private CoreManualSwitch _EC, _EN, _EOVR, _EZ;
	private CoreManualSwitch _OECT, _OEY;
	private CoreManualSwitch _SE;
	private CoreBitDisplay C0;
	private CoreBitDisplay CT;
	private SwitchWithDisplay SIO0, SIOn, QIO0, QIOn;
	private SwitchWithDisplay YC, YN, YOVR, YZ;

	private final TestEnvironmentHelper testHelper = new TestEnvironmentHelper(this, "GUIAm2904");

	@Override
	public void setup()
	{
		testHelper.setup(DebugState.NO_DEBUG);
	}

	@Override
	public Result run()
	{
		return testHelper.run();
	}

	@Override
	public void clockOn(boolean isClockOn)
	{
		if (isClockOn)
			C.switchFullOn();
		else
			C.switchFullOff();
	}

	@Override
	public void setInstruction(Am2904_Inst inst)
	{
		var old = I.getValues();
		var newPart = BitVector.from(inst.ordinal(), 6);
		I.setState(old.subVector(0, 7).concat(newPart));
	}

	@Override
	public void setShiftCode(String val_4_bit)
	{
		var old = I.getValues();
		var newPart = BitVector.parse(val_4_bit);
		I.setState(old.subVector(0, 3).concat(newPart).concat(old.subVector(7)));
	}

	@Override
	public void setI10(Am2904_ShiftDir dir)
	{
		var old = I.getValues();
		var newPart = BitVector.from(dir.ordinal(), 1);
		I.setState(old.subVector(0, 2).concat(newPart).concat(old.subVector(3)));
	}

	@Override
	public void setCarry(Am2904_Carry carry)
	{
		var old = I.getValues();
		var newPart = BitVector.from(carry.ordinal(), 2);
		I.setState(newPart.concat(old.subVector(2)));
	}

	@Override
	public void setCX(String val_1_bit)
	{
		Cx.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setY(String z_c_n_ovr)
	{
		var bv = BitVector.parse(z_c_n_ovr);
		// correct order apparently unknown, most likely Z-C-N-OVR
		YZ.setState(bv.getLSBit(3).toVector());
		YC.setState(bv.getLSBit(2).toVector());
		YN.setState(bv.getLSBit(1).toVector());
		YOVR.setState(bv.getLSBit(0).toVector());
	}

	@Override
	public void setIZ(String val_1_bit)
	{
		IZ.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setIC(String val_1_bit)
	{
		IC.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setIOVR(String val_1_bit)
	{
		IOVR.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setIN(String val_1_bit)
	{
		IN.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_CEM(String val_1_bit)
	{
		_CEM.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_CEµ(String val_1_bit)
	{
		_CEmu.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_OEY(String val_1_bit)
	{
		_OEY.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_OECT(String val_1_bit)
	{
		_OECT.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_SE(String val_1_bit)
	{
		_SE.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_EZ(String val_1_bit)
	{
		_EZ.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_EC(String val_1_bit)
	{
		_EC.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_EOVR(String val_1_bit)
	{
		_EOVR.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_EN(String val_1_bit)
	{
		_EN.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setSIO0(String val_1_bit)
	{
		SIO0.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setSIO3(String val_1_bit)
	{
		SIOn.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setQIO0(String val_1_bit)
	{
		QIO0.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setQIO3(String val_1_bit)
	{
		QIOn.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setDirectly(Register r, String val_1_bit)
	{
		var bv = (BitVector) am2904.getHighLevelState(regToStateID(r));
		bv = bv.withBitChanged(r.ordinal() % 4, b -> Bit.parse(val_1_bit));
		am2904.setHighLevelState(regToStateID(r), bv);
	}

	@Override
	public void setDirectly(CompleteStatus r, String z_c_n_ovr)
	{
		am2904.setHighLevelState(regToStateID(r), BitVector.parse(z_c_n_ovr));
	}

	@Override
	public String getC0()
	{
		return C0.getDisplayedValue().toString();
	}

	@Override
	public String getCT()
	{
		return CT.getDisplayedValue().toString();
	}

	@Override
	public String getY()
	{
		// correct order apparently unknown, most likely Z-C-N-OVR
		var y3 = YZ.getDisplayedValue();
		var y2 = YC.getDisplayedValue();
		var y1 = YN.getDisplayedValue();
		var y0 = YOVR.getDisplayedValue();
		return y3.concat(y2).concat(y1).concat(y0).toString();
	}

	@Override
	public String getSIO0()
	{
		return SIO0.getDisplayedValue().toString();
	}

	@Override
	public String getSIO3()
	{
		return SIOn.getDisplayedValue().toString();
	}

	@Override
	public String getQIO0()
	{
		return QIO0.getDisplayedValue().toString();
	}

	@Override
	public String getQIO3()
	{
		return QIOn.getDisplayedValue().toString();
	}

	@Override
	public String getDirectly(Register r)
	{
		var bv = (BitVector) am2904.getHighLevelState(regToStateID(r));
		return bv.getMSBit(r.ordinal() % 4).getSymbol();
	}

	@Override
	public String getDirectly(CompleteStatus r)
	{
		var bv = (BitVector) am2904.getHighLevelState(regToStateID(r));
		return bv.toString();
	}

	private static String regToStateID(Register r)
	{
		if (r.ordinal() < 4)
			return "musr.q";
		return "msr.q";
	}

	private static String regToStateID(CompleteStatus r)
	{
		if (r == CompleteStatus.micro)
			return "musr.q";
		return "msr.q";
	}

	@Override
	public TestEnvironmentHelper getTestEnvironmentHelper()
	{
		return testHelper;
	}
}
