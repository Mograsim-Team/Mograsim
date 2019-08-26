package net.mograsim.logic.model.am2900.am2910;

import org.junit.Test;

import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.model.am2900.TestEnvironmentHelper;
import net.mograsim.logic.model.am2900.TestEnvironmentHelper.DebugState;
import net.mograsim.logic.model.model.components.GUIComponent;

public class TestableAm2910Impl implements TestableAm2910
{

	private GUIComponent am2901;
	private Timeline timeline;
	private ManualSwitch I;
	private ManualSwitch C;
	private ManualSwitch CI;
	private ManualSwitch D;
	private ManualSwitch _CC;
	private ManualSwitch _CCEN;
	private ManualSwitch _RDL;
	private ManualSwitch _OE;
	private BitDisplay _FULL;
	private BitDisplay Y;
	private BitDisplay _PL, _MAP, _VECT;

	private final TestEnvironmentHelper testHelper = new TestEnvironmentHelper(this, "GUIAm2910");

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
		_CCEN.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setD(String val_12_bit)
	{
		D.setState(BitVector.parse(val_12_bit));
	}

	@Override
	public void set_CC(String val_1_bit)
	{
		_CC.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void setCI(String val_1_bit)
	{
		CI.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_RLD(String val_1_bit)
	{
		_RDL.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public void set_OE(String val_1_bit)
	{
		_OE.setState(BitVector.parse(val_1_bit));
	}

	@Override
	public String getY()
	{
		return Y.getDisplayedValue().toString();
	}

	@Override
	public String get_FULL()
	{
		return _FULL.getDisplayedValue().toString();
	}

	@Override
	public String get_PL()
	{
		return _PL.getDisplayedValue().toString();
	}

	@Override
	public String get_MAP()
	{
		return _MAP.getDisplayedValue().toString();
	}

	@Override
	public String get_VECT()
	{
		return _VECT.getDisplayedValue().toString();
	}

}
