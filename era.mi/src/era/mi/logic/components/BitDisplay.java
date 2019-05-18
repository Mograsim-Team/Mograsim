package era.mi.logic.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.Bit;
import era.mi.logic.wires.WireArray;

public class BitDisplay extends BasicComponent {
	private final WireArray in;
	private Bit[] displayedValue;

	public BitDisplay(WireArray in) {
		super(1);
		this.in = in;
		in.addObserver(this);
		compute();
	}

	@Override
	protected void compute() {
		displayedValue = in.getValues();
	}

	public Bit[] getDisplayedValue() {
		return displayedValue;
	}

	public boolean isDisplaying(Bit... values) {
		return Arrays.equals(displayedValue, values);
	}

	@Override
	public List<WireArray> getAllInputs() {
		return Collections.unmodifiableList(Arrays.asList(in));
	}

	@Override
	public List<WireArray> getAllOutputs() {
		return Collections.unmodifiableList(new ArrayList<WireArray>());
	}
}
