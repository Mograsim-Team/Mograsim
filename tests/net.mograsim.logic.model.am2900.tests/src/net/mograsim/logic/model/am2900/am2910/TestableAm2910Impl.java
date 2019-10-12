package net.mograsim.logic.model.am2900.am2910;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper;
import net.mograsim.logic.model.am2900.util.TestEnvironmentHelper.DebugState;
import net.mograsim.logic.model.model.components.ModelComponent;

public class TestableAm2910Impl implements TestableAm2910
{

	private ModelComponent am2901;
	private CoreManualSwitch I;
	private CoreManualSwitch C;
	private CoreManualSwitch CI;
	private CoreManualSwitch D;
	private CoreManualSwitch _CC;
	private CoreManualSwitch _CCEN;
	private CoreManualSwitch _RLD;
	private CoreManualSwitch _OE;
	private CoreBitDisplay _FULL;
	private CoreBitDisplay Y;
	private CoreBitDisplay _PL, _MAP, _VECT;

	private final TestEnvironmentHelper testHelper = new TestEnvironmentHelper(this, "Am2910");

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
	public void setInstruction(Am2910_Inst inst)
	{
		I.setState(BitVector.from(inst.ordinal(), 4));
	}

	@Override
	public void set_CCEN(String val_1_bit)
	{
		_CCEN.setState(BitVector.parseBitstring(val_1_bit));
	}

	@Override
	public void setD(String val_12_bit)
	{
		D.setState(BitVector.parseBitstring(val_12_bit));
	}

	@Override
	public void set_CC(String val_1_bit)
	{
		_CC.setState(BitVector.parseBitstring(val_1_bit));
	}

	@Override
	public void setCI(String val_1_bit)
	{
		CI.setState(BitVector.parseBitstring(val_1_bit));
	}

	@Override
	public void set_RLD(String val_1_bit)
	{
		_RLD.setState(BitVector.parseBitstring(val_1_bit));
	}

	@Override
	public void set_OE(String val_1_bit)
	{
		_OE.setState(BitVector.parseBitstring(val_1_bit));
	}

	@Override
	public void setDirectly(Register r, String val_X_bit)
	{
		am2901.setHighLevelState(regToStateID(r), BitVector.parseBitstring(val_X_bit));
	}

	@Override
	public String getY()
	{
		return Y.getDisplayedValue().toBitstring();
	}

	@Override
	public String get_FULL()
	{
		return _FULL.getDisplayedValue().toBitstring();
	}

	@Override
	public String get_PL()
	{
		return _PL.getDisplayedValue().toBitstring();
	}

	@Override
	public String get_MAP()
	{
		return _MAP.getDisplayedValue().toBitstring();
	}

	@Override
	public String get_VECT()
	{
		return _VECT.getDisplayedValue().toBitstring();
	}

	@Override
	public String getDirectly(Register r)
	{
		return ((BitVector) am2901.getHighLevelState(regToStateID(r))).toBitstring();
	}

	private static String regToStateID(Register r)
	{
		switch (r)
		{
		case S_0:
		case S_1:
		case S_2:
		case S_3:
		case S_4:
			return "stack.c" + BitVector.from(r.ordinal(), 3) + ".q";
		case SP:
			return "sp.q";
		case PC:
			return "mupc.q";
		case REG_COUNT:
			return "r.q";
		default:
			throw new IllegalArgumentException("unknown: " + r);
		}
	}

	@Override
	public TestEnvironmentHelper getTestEnvironmentHelper()
	{
		return testHelper;
	}
}
