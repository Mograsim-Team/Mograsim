package era.mi.logic.tests;

import era.mi.logic.Bit;
import era.mi.logic.Simulation;
import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;
import era.mi.logic.wires.WireArrayObserver;

public class Connector implements WireArrayObserver {
	private final WireArray a;
//	private final WireArray b;
	private final WireArrayInput aI;
	private final WireArrayInput bI;

	public Connector(WireArray a, WireArray b) {
		if (a.length != b.length)
			throw new IllegalArgumentException(String.format("WireArray width does not match: %d, %d", a.length, b.length));
		this.a = a;
//		this.b = b;
		a.addObserver(this);
		b.addObserver(this);
		aI = a.createInput();
		bI = b.createInput();
	}

	@Override
	public void update(WireArray initiator, Bit[] oldValues) {
		Simulation.TIMELINE.addEvent((e) -> {
			if (initiator == a)
				bI.feedSignals(aI.wireValuesExcludingMe());
			else
				aI.feedSignals(bI.wireValuesExcludingMe());
		}, 1);
	}
}
