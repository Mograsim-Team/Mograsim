package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUInand3;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901ALUFuncDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUFuncDecode(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUFuncDecode");
		setSubmodelScale(.25);
		setInputPins("I5", "I4", "I3");
		setOutputPins("CinE", "L", "SBE");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I5 = getInputSubmodelPins().get(0);
		Pin I4 = getInputSubmodelPins().get(1);
		Pin I3 = getInputSubmodelPins().get(2);
		Pin CinE = getOutputSubmodelPins().get(0);
		Pin L = getOutputSubmodelPins().get(1);
		Pin SBE = getOutputSubmodelPins().get(2);

		GUINandGate notI5 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI4 = new GUINandGate(submodelModifiable, 1);
		GUInand3 nandI4I3NotI5 = new GUInand3(submodelModifiable);
		GUINandGate nandI5NotI4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI3I4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandL = new GUINandGate(submodelModifiable, 1);
		GUIand andSBE = new GUIand(submodelModifiable);

		WireCrossPoint cpI51 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI52 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI41 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI42 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI43 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI51 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI52 = new WireCrossPoint(submodelModifiable, 1);

		notI5.moveTo(15, 10);
		notI4.moveTo(15, 50);
		nandI4I3NotI5.moveTo(55, 10);
		nandI5NotI4.moveTo(55, 45);
		nandI3I4.moveTo(55, 70);
		nandL.moveTo(100, 50);
		andSBE.moveTo(100, 95);
		cpI51.moveCenterTo(5, 20);
		cpI52.moveCenterTo(5, 25);
		cpI41.moveCenterTo(10, 60);
		cpI42.moveCenterTo(10, 55);
		cpI43.moveCenterTo(10, 65);
		cpI3.moveCenterTo(50, 75);
		cpNotI51.moveCenterTo(40, 20);
		cpNotI52.moveCenterTo(40, 35);

		new GUIWire(submodelModifiable, I5, cpI51, new Point[0]);
		new GUIWire(submodelModifiable, cpI51, notI5.getInputPins().get(0), new Point(5, 15));
		new GUIWire(submodelModifiable, cpI51, cpI52, new Point[0]);
		new GUIWire(submodelModifiable, cpI52, notI5.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpI52, nandI5NotI4.getInputPins().get(0), new Point(5, 45), new Point(45, 45), new Point(45, 50));
		new GUIWire(submodelModifiable, I4, cpI41, new Point[0]);
		new GUIWire(submodelModifiable, cpI41, cpI42, new Point[0]);
		new GUIWire(submodelModifiable, cpI42, nandI4I3NotI5.getInputPins().get(0), new Point(10, 40), new Point(45, 40),
				new Point(45, 15));
		new GUIWire(submodelModifiable, cpI42, notI4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpI41, cpI43, new Point[0]);
		new GUIWire(submodelModifiable, cpI43, notI4.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpI43, nandI3I4.getInputPins().get(1), new Point(10, 85));
		new GUIWire(submodelModifiable, I3, cpI3, new Point(50, 100));
		new GUIWire(submodelModifiable, cpI3, nandI4I3NotI5.getInputPins().get(1), new Point(50, 25));
		new GUIWire(submodelModifiable, cpI3, nandI3I4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, notI5.getOutputPin(), cpNotI51, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI51, CinE, new Point(40, 5), new Point(115, 5), new Point(115, 20));
		new GUIWire(submodelModifiable, cpNotI51, cpNotI52, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI52, nandI4I3NotI5.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI52, andSBE.getInputPins().get(1), new Point(40, 110));
		new GUIWire(submodelModifiable, notI4.getOutputPin(), nandI5NotI4.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, nandI4I3NotI5.getOutputPins().get(0), nandL.getInputPins().get(0));
		new GUIWire(submodelModifiable, nandI5NotI4.getOutputPin(), nandL.getInputPins().get(1));
		new GUIWire(submodelModifiable, nandI3I4.getOutputPin(), andSBE.getInputPins().get(0));
		new GUIWire(submodelModifiable, nandL.getOutputPin(), L, new Point[0]);
		new GUIWire(submodelModifiable, andSBE.getOutputPins().get(0), SBE, new Point[0]);
	}
}