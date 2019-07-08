package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIdlatch extends SimpleRectangularSubmodelComponent
{
	private GUI_rsLatch _rsLatch;

	public GUIdlatch(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIdlatch(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIdlatch", name);
		setSubmodelScale(.4);
		setInputPins("D", "E");
		setOutputPins("Q", "_Q");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin D = getSubmodelPin("D");
		Pin E = getSubmodelPin("E");
		Pin Q = getSubmodelPin("Q");
		Pin _Q = getSubmodelPin("_Q");

		GUINandGate nand1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nand2 = new GUINandGate(submodelModifiable, 1);
		_rsLatch = new GUI_rsLatch(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);

		nand1.moveTo(10, 2.5);
		nand2.moveTo(15, 27.5);
		_rsLatch.moveTo(45, 7.5);
		cp1.moveCenterTo(5, 37.5);
		cp2.moveCenterTo(35, 12.5);

		new GUIWire(submodelModifiable, D, nand1.getPin("A"));
		new GUIWire(submodelModifiable, E, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, nand1.getPin("B"), new Point(5, 17.5));
		new GUIWire(submodelModifiable, cp1, nand2.getPin("B"), new Point(5, 42.5));
		new GUIWire(submodelModifiable, nand1.getPin("Y"), cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, nand2.getPin("A"), new Point(35, 25), new Point(10, 25), new Point(10, 32.5));
		new GUIWire(submodelModifiable, cp2, _rsLatch.getPin("_S"), new Point[0]);
		new GUIWire(submodelModifiable, nand2.getPin("Y"), _rsLatch.getPin("_R"), new Point(40, 37.5), new Point(40, 22.5));
		new GUIWire(submodelModifiable, _rsLatch.getPin("Q"), Q, new Point[0]);
		new GUIWire(submodelModifiable, _rsLatch.getPin("_Q"), _Q);

		addAtomicHighLevelStateID("q");
	}

	@Override
	public void setAtomicHighLevelState(String stateID, Object newState)
	{
		switch (stateID)
		{
		case "q":
			_rsLatch.setHighLevelState("q", newState);
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
			return _rsLatch.getHighLevelState("q");
		default:
			// should not happen because we tell SubmodelComponent to only allow these state IDs.
			throw new IllegalStateException("Illegal atomic state ID: " + stateID);
		}
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIdlatch.class.getCanonicalName(), (m, p, n) -> new GUIdlatch(m, n));
	}
}