package net.mograsim.logic.model.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;

public class GUI_rsLatch extends SimpleRectangularSubmodelComponent
{
	private GUIWire wireQ, wire_Q;

	public GUI_rsLatch(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUI_rsLatch(ViewModelModifiable model, String name)
	{
		super(model, 1, "_rsLatch", name);
		setSubmodelScale(.4);
		setInputPins("_S", "_R");
		setOutputPins("Q", "_Q");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin _S = getSubmodelPin("_S");
		Pin _R = getSubmodelPin("_R");
		Pin Q = getSubmodelPin("Q");
		Pin _Q = getSubmodelPin("_Q");

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 7.5);
		nand2.moveTo(40, 12.5);
		cp1.moveCenterTo(35, 17.5);
		cp2.moveCenterTo(65, 37.5);

		new GUIWire(submodelModifiable, _S, nand1.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, _R, nand2.getPin("B"), new Point(35, 37.5), new Point(35, 27.5));
		new GUIWire(submodelModifiable, nand1.getPin("Y"), cp1, new Point[0]);
		new GUIWire(submodelModifiable, nand2.getPin("Y"), cp2, new Point(65, 22.5));
		new GUIWire(submodelModifiable, cp1, nand2.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, cp2, nand1.getPin("B"), new Point(65, 42.5), new Point(5, 42.5), new Point(5, 22.5));
		wireQ = new GUIWire(submodelModifiable, cp1, Q, new Point(35, 17.5), new Point(35, 7.5), new Point(65, 7.5), new Point(65, 12.5));
		wire_Q = new GUIWire(submodelModifiable, cp2, _Q, new Point[0]);

		addAtomicHighLevelStateID("q");
	}

	@Override
	public void setAtomicHighLevelState(String stateID, Object newState)
	{
		switch (stateID)
		{
		case "q":
			if (wireQ != null)
			{
				// TODO force this to happen without any Timeline updates in the meantime.
				// Maybe make it a requirement of setHighLevelState that the Timeline is "halted" during a call?
				Bit newStateCasted = (Bit) newState;
				BitVector newStateVector = BitVector.of(newStateCasted);
				if (wireQ.hasLogicModelBinding())
					wireQ.forceWireValues(newStateVector);
				// We set both wires because then both outputs go to their correct state at the same time, and to avoid problems when not
				// both
				// inputs are 1
				if (wire_Q.hasLogicModelBinding())
					wire_Q.forceWireValues(newStateVector.not());
			}
			break;
		default:
			// should not happen because we tell SubmodelComponent to only allow these state IDs.
			throw new IllegalStateException("Illegal atomic state ID: " + stateID);
		}
	}

	@Override
	public Object getAtomicHighLevelState(String stateID)
	{
		switch (stateID)
		{
		case "q":
			if (wireQ.hasLogicModelBinding())
				return wireQ.getWireValues().getBit(0);
			return null;
		default:
			// should not happen because we tell SubmodelComponent to only allow these state IDs.
			throw new IllegalStateException("Illegal atomic state ID: " + stateID);
		}
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUI_rsLatch.class.getCanonicalName(), (m, p, n) -> new GUI_rsLatch(m, n));
	}
}