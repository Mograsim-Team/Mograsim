package mograsim.logic.core.components;

import java.util.List;

import mograsim.logic.core.timeline.Timeline;
import mograsim.logic.core.types.Bit;
import mograsim.logic.core.wires.Wire.ReadEnd;
import mograsim.logic.core.wires.Wire.ReadWriteEnd;

/**
 * This class models a simple on/off (ONE/ZERO) switch for user interaction.
 *
 * @author Christian Femers
 *
 */
public class ManualSwitch extends Component
{
	private ReadWriteEnd output;
	private boolean isOn;

	public ManualSwitch(Timeline timeline, ReadWriteEnd output)
	{
		super(timeline);
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
	public List<ReadEnd> getAllInputs()
	{
		return List.of();
	}

	@Override
	public List<ReadWriteEnd> getAllOutputs()
	{
		return List.of(output);
	}

}
