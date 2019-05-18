package era.mi.logic.components;

import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

/**
 * This class models a simple on/off (ONE/ZERO) switch for user interaction.
 *
 * @author Christian Femers
 *
 */
public class ManualSwitch implements Component {
	private WireArray output;
	private WireArrayInput outputI;
	private boolean isOn;

	public ManualSwitch(WireArray output) {
		if (output.length != 1)
			throw new IllegalArgumentException("Switch output can be only a single wire");
		this.output = output;
		this.outputI = output.createInput();
	}

	public void switchOn() {
		setState(true);
	}

	public void switchOff() {
		setState(false);
	}

	public void toggle() {
		setState(!isOn);
	}

	public void setState(boolean isOn) {
		if (this.isOn == isOn)
			return;
		this.isOn = isOn;
		outputI.feedSignals(getValue());
	}

	public boolean isOn() {
		return isOn;
	}

	public Bit getValue() {
		return isOn ? Bit.ONE : Bit.ZERO;
	}

	@Override
	public List<WireArray> getAllInputs() {
		return List.of();
	}

	@Override
	public List<WireArray> getAllOutputs() {
		return List.of(output);
	}

}
