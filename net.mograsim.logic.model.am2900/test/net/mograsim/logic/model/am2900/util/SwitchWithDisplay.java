package net.mograsim.logic.model.am2900.util;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUIBitDisplay;
import net.mograsim.logic.model.model.components.atomic.GUIManualSwitch;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.util.ModellingTool;

public class SwitchWithDisplay
{
	private final Pin pin;
	private final GUIBitDisplay guiBitDisplay;
	private final GUIManualSwitch guiManualSwitch;

	public SwitchWithDisplay(ViewModelModifiable model, Pin target)
	{
		pin = target;
		guiBitDisplay = new GUIBitDisplay(model, pin.logicWidth);
		guiManualSwitch = new GUIManualSwitch(model, pin.logicWidth);

		ModellingTool tool = ModellingTool.createFor(model);
		WireCrossPoint crossPoint = new WireCrossPoint(model, pin.logicWidth);
		tool.connect(guiBitDisplay.getInputPin(), crossPoint);
		tool.connect(guiManualSwitch.getOutputPin(), crossPoint);
	}

	public final BitVector getDisplayedValue()
	{
		return guiBitDisplay.getBitDisplay().getDisplayedValue();
	}

	public final void setState(BitVector bits)
	{
		guiManualSwitch.getManualSwitch().setState(bits);
	}

	public final Pin getPin()
	{
		return pin;
	}

	public final CoreBitDisplay getBitDisplay()
	{
		return guiBitDisplay.getBitDisplay();
	}

	public final CoreManualSwitch getManualSwitch()
	{
		return guiManualSwitch.getManualSwitch();
	}

	final GUIBitDisplay getGuiBitDisplay()
	{
		return guiBitDisplay;
	}

	final GUIManualSwitch getGuiManualSwitch()
	{
		return guiManualSwitch;
	}
}
