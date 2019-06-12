package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIdff;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901QReg extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901QReg(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901QReg");
		setSubmodelScale(.4);
		setInputCount(6);
		setOutputCount(4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin C = getInputSubmodelPins().get(0);
		Pin WE = getInputSubmodelPins().get(1);
		Pin D1 = getInputSubmodelPins().get(2);
		Pin D2 = getInputSubmodelPins().get(3);
		Pin D3 = getInputSubmodelPins().get(4);
		Pin D4 = getInputSubmodelPins().get(5);
		Pin Q1 = getOutputSubmodelPins().get(0);
		Pin Q2 = getOutputSubmodelPins().get(1);
		Pin Q3 = getOutputSubmodelPins().get(2);
		Pin Q4 = getOutputSubmodelPins().get(3);

		GUIand and = new GUIand(submodelModifiable);
		GUIdff dff1 = new GUIdff(submodelModifiable);
		GUIdff dff2 = new GUIdff(submodelModifiable);
		GUIdff dff3 = new GUIdff(submodelModifiable);
		GUIdff dff4 = new GUIdff(submodelModifiable);

		WireCrossPoint cpC1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC3 = new WireCrossPoint(submodelModifiable, 1);

		and.moveTo(5, 15);
		dff1.moveTo(50, 7.5);
		dff2.moveTo(50, 32.5);
		dff3.moveTo(50, 57.5);
		dff4.moveTo(50, 82.5);
		cpC1.moveCenterTo(42.5, 20);
		cpC2.moveCenterTo(42.5, 37.5);
		cpC3.moveCenterTo(42.5, 62.5);

		new GUIWire(submodelModifiable, C, and.getInputPins().get(0));
		new GUIWire(submodelModifiable, WE, and.getInputPins().get(1));
		new GUIWire(submodelModifiable, and.getOutputPins().get(0), cpC1, new Point[0]);
		new GUIWire(submodelModifiable, cpC1, dff1.getInputPins().get(0), new Point(42.5, 12.5));
		new GUIWire(submodelModifiable, cpC1, cpC2, new Point[0]);
		new GUIWire(submodelModifiable, cpC2, dff2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpC2, cpC3, new Point[0]);
		new GUIWire(submodelModifiable, cpC3, dff3.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpC3, dff4.getInputPins().get(0), new Point(42.5, 87.5));
		new GUIWire(submodelModifiable, D1, dff1.getInputPins().get(1), new Point(17.5, 62.5), new Point(17.5, 42.5), new Point(45, 42.5),
				new Point(45, 22.5));
		new GUIWire(submodelModifiable, D2, dff2.getInputPins().get(1), new Point(22.5, 87.5), new Point(22.5, 47.5));
		new GUIWire(submodelModifiable, D3, dff3.getInputPins().get(1), new Point(27.5, 112.5), new Point(27.5, 72.5));
		new GUIWire(submodelModifiable, D4, dff4.getInputPins().get(1), new Point(32.5, 137.5), new Point(32.5, 97.5));
		new GUIWire(submodelModifiable, dff1.getOutputPins().get(0), Q1, new Point[0]);
		new GUIWire(submodelModifiable, dff2.getOutputPins().get(0), Q2, new Point[0]);
		new GUIWire(submodelModifiable, dff3.getOutputPins().get(0), Q3, new Point[0]);
		new GUIWire(submodelModifiable, dff4.getOutputPins().get(0), Q4, new Point[0]);
	}
}