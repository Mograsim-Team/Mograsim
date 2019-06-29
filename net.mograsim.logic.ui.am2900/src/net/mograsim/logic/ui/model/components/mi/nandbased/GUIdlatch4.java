package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdlatch4 extends SimpleRectangularSubmodelComponent
{
	private GUIdlatch dlatch1;
	private GUIdlatch dlatch2;
	private GUIdlatch dlatch3;
	private GUIdlatch dlatch4;

	public GUIdlatch4(ViewModelModifiable model)
	{
		super(model, 1, "GUIdlatch4");
		setSubmodelScale(.4);
		setInputPins("D1", "D2", "D3", "D4", "C");
		setOutputPins("Q1", "Q2", "Q3", "Q4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin D1 = getSubmodelPin("D1");
		Pin D2 = getSubmodelPin("D2");
		Pin D3 = getSubmodelPin("D3");
		Pin D4 = getSubmodelPin("D4");
		Pin C = getSubmodelPin("C");
		Pin Q1 = getSubmodelPin("Q1");
		Pin Q2 = getSubmodelPin("Q2");
		Pin Q3 = getSubmodelPin("Q3");
		Pin Q4 = getSubmodelPin("Q4");

		dlatch1 = new GUIdlatch(submodelModifiable);
		dlatch2 = new GUIdlatch(submodelModifiable);
		dlatch3 = new GUIdlatch(submodelModifiable);
		dlatch4 = new GUIdlatch(submodelModifiable);

		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp4 = new WireCrossPoint(submodelModifiable, 1);

		dlatch1.moveTo(30, 7.5);
		dlatch2.moveTo(30, 32.5);
		dlatch3.moveTo(30, 57.5);
		dlatch4.moveTo(30, 82.5);
		cp2.moveCenterTo(15, 47.5);
		cp3.moveCenterTo(15, 72.5);
		cp4.moveCenterTo(15, 97.5);

		new GUIWire(submodelModifiable, C, cp4, new Point(15, 112.5));
		new GUIWire(submodelModifiable, cp4, dlatch4.getPin("E"), new Point[0]);
		new GUIWire(submodelModifiable, cp4, cp3, new Point[0]);
		new GUIWire(submodelModifiable, cp3, dlatch3.getPin("E"), new Point[0]);
		new GUIWire(submodelModifiable, cp3, cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, dlatch2.getPin("E"), new Point[0]);
		new GUIWire(submodelModifiable, cp2, dlatch1.getPin("E"), new Point(15, 22.5));
		new GUIWire(submodelModifiable, D1, dlatch1.getPin("D"), new Point[0]);
		new GUIWire(submodelModifiable, D2, dlatch2.getPin("D"), new Point[0]);
		new GUIWire(submodelModifiable, D3, dlatch3.getPin("D"), new Point[0]);
		new GUIWire(submodelModifiable, D4, dlatch4.getPin("D"), new Point[0]);
		new GUIWire(submodelModifiable, dlatch1.getPin("Q"), Q1, new Point[0]);
		new GUIWire(submodelModifiable, dlatch2.getPin("Q"), Q2, new Point[0]);
		new GUIWire(submodelModifiable, dlatch3.getPin("Q"), Q3, new Point[0]);
		new GUIWire(submodelModifiable, dlatch4.getPin("Q"), Q4, new Point[0]);
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		switch (stateID)
		{
		case "q1":
			dlatch1.setHighLevelState("q", newState);
			break;
		case "q2":
			dlatch2.setHighLevelState("q", newState);
			break;
		case "q3":
			dlatch3.setHighLevelState("q", newState);
			break;
		case "q4":
			dlatch4.setHighLevelState("q", newState);
			break;
		case "q":
			BitVector newStateCasted = (BitVector) newState;
			setHighLevelState("q1", newStateCasted.getBit(3));
			setHighLevelState("q2", newStateCasted.getBit(2));
			setHighLevelState("q3", newStateCasted.getBit(1));
			setHighLevelState("q4", newStateCasted.getBit(0));
			break;
		default:
			super.setHighLevelState(stateID, newState);
			break;
		}
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		switch (stateID)
		{
		case "q1":
			return dlatch1.getHighLevelState("q");
		case "q2":
			return dlatch2.getHighLevelState("q");
		case "q3":
			return dlatch3.getHighLevelState("q");
		case "q4":
			return dlatch4.getHighLevelState("q");
		case "q":
			Bit q1 = (Bit) getHighLevelState("q1");
			Bit q2 = (Bit) getHighLevelState("q2");
			Bit q3 = (Bit) getHighLevelState("q3");
			Bit q4 = (Bit) getHighLevelState("q4");
			return BitVector.of(q4, q3, q2, q1);
		default:
			return super.getHighLevelState(stateID);
		}
	}
}