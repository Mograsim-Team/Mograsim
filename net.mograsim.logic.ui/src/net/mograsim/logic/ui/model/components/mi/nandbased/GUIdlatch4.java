package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIdlatch4 extends SimpleRectangularSubmodelComponent
{
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
		Pin D1 = getInputSubmodelPins().get(0);
		Pin D2 = getInputSubmodelPins().get(1);
		Pin D3 = getInputSubmodelPins().get(2);
		Pin D4 = getInputSubmodelPins().get(3);
		Pin C = getInputSubmodelPins().get(4);
		Pin Q1 = getOutputSubmodelPins().get(0);
		Pin Q2 = getOutputSubmodelPins().get(1);
		Pin Q3 = getOutputSubmodelPins().get(2);
		Pin Q4 = getOutputSubmodelPins().get(3);

		GUIdlatch dlatch1 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch2 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch3 = new GUIdlatch(submodelModifiable);
		GUIdlatch dlatch4 = new GUIdlatch(submodelModifiable);

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
		new GUIWire(submodelModifiable, cp4, dlatch4.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cp4, cp3, new Point[0]);
		new GUIWire(submodelModifiable, cp3, dlatch3.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cp3, cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, dlatch2.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cp2, dlatch1.getInputPins().get(1), new Point(15, 22.5));
		new GUIWire(submodelModifiable, D1, dlatch1.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, D2, dlatch2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, D3, dlatch3.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, D4, dlatch4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, dlatch1.getOutputPins().get(0), Q1, new Point[0]);
		new GUIWire(submodelModifiable, dlatch2.getOutputPins().get(0), Q2, new Point[0]);
		new GUIWire(submodelModifiable, dlatch3.getOutputPins().get(0), Q3, new Point[0]);
		new GUIWire(submodelModifiable, dlatch4.getOutputPins().get(0), Q4, new Point[0]);
	}
}