package net.mograsim.logic.model.am2900.util;

import net.mograsim.logic.core.components.CoreBitDisplay;
import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelManualSwitch;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.util.ModellingTool;

public class SwitchWithDisplay
{
	private final Pin pin;
	private final ModelBitDisplay modelBitDisplay;
	private final ModelManualSwitch modelManualSwitch;

	public SwitchWithDisplay(ViewModelModifiable model, Pin target)
	{
		pin = target;
		modelBitDisplay = new ModelBitDisplay(model, pin.logicWidth);
		modelManualSwitch = new ModelManualSwitch(model, pin.logicWidth);

		ModellingTool tool = ModellingTool.createFor(model);
		ModelWireCrossPoint crossPoint = new ModelWireCrossPoint(model, pin.logicWidth);
		tool.connect(modelBitDisplay.getInputPin(), crossPoint);
		tool.connect(modelManualSwitch.getOutputPin(), crossPoint);
	}

	public final BitVector getDisplayedValue()
	{
		return modelBitDisplay.getBitDisplay().getDisplayedValue();
	}

	public final void setState(BitVector bits)
	{
		modelManualSwitch.getManualSwitch().setState(bits);
	}

	public final Pin getPin()
	{
		return pin;
	}

	public final CoreBitDisplay getBitDisplay()
	{
		return modelBitDisplay.getBitDisplay();
	}

	public final CoreManualSwitch getManualSwitch()
	{
		return modelManualSwitch.getManualSwitch();
	}

	final ModelBitDisplay getModelBitDisplay()
	{
		return modelBitDisplay;
	}

	final ModelManualSwitch getModelManualSwitch()
	{
		return modelManualSwitch;
	}
}
