package era.mi.logic.components;

import java.util.List;

import era.mi.logic.types.Bit;
import era.mi.logic.wires.Wire.WireEnd;

/**
 * This class models a simple on/off (ONE/ZERO) switch for user interaction.
 *
 * @author Christian Femers
 *
 */
public class ManualSwitch implements Component
{
	private WireEnd output;
	private boolean isOn;

	public ManualSwitch(WireEnd output)
	{
		if (output.length() != 1)
			throw new IllegalArgumentException("Switch output can be only a single wire");
		this.output = output;
	}

	public void switchOn()
	{
		setState(true);
	}

	public void switchOff()
	{
		setState(false);
	}

	public void toggle()
	{
		setState(!isOn);
	}

	public void setState(boolean isOn)
	{
		if (this.isOn == isOn)
			return;
		this.isOn = isOn;
		output.feedSignals(getValue());
	}

	public boolean isOn()
	{
		return isOn;
	}

	public Bit getValue()
	{
		return isOn ? Bit.ONE : Bit.ZERO;
	}

	@Override
	public List<WireEnd> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<WireEnd> getAllOutputs()
	{
		return List.of(output);
	}

}
