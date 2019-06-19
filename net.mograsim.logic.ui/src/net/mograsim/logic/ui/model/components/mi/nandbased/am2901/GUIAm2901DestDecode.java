package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901DestDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901DestDecode(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901DestDecode");
		setSubmodelScale(.25);
		setInputPins("I8", "I7", "I6");
		setOutputPins("NSH", "RSH", "RAMWE", "YF", "LSH", "QWE");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I8 = getInputSubmodelPins().get(0);
		Pin I7 = getInputSubmodelPins().get(1);
		Pin I6 = getInputSubmodelPins().get(2);
		Pin NSH = getOutputSubmodelPins().get(0);
		Pin RSH = getOutputSubmodelPins().get(1);
		Pin RAMWE = getOutputSubmodelPins().get(2);
		Pin YF = getOutputSubmodelPins().get(3);
		Pin LSH = getOutputSubmodelPins().get(4);
		Pin QWE = getOutputSubmodelPins().get(5);

		GUINandGate notI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI7 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI8I7 = new GUINandGate(submodelModifiable, 1);
		GUINandGate notI6 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandRSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandRAMWE = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandI7NotI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandLSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate notRSH = new GUINandGate(submodelModifiable, 1);
		GUINandGate andI7NotI8 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandQWE = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandYF = new GUINandGate(submodelModifiable, 1);
		GUINandGate notQWE = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI83 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI71 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI72 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI73 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI6 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI7 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI8I7 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotRSH = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI7NotI81 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandI7NotI82 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNotI6 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNandQWE = new WireCrossPoint(submodelModifiable, 1);

		notI8.moveTo(15, 10);
		notI7.moveTo(15, 50);
		nandI8I7.moveTo(15, 90);
		notI6.moveTo(15, 150);
		nandRSH.moveTo(50, 10);
		nandRAMWE.moveTo(50, 50);
		nandI7NotI8.moveTo(50, 90);
		nandLSH.moveTo(50, 130);
		notRSH.moveTo(80, 10);
		andI7NotI8.moveTo(80, 90);
		nandQWE.moveTo(80, 145);
		nandYF.moveTo(110, 105);
		notQWE.moveTo(115, 210);
		cpI81.moveCenterTo(5, 20);
		cpI82.moveCenterTo(5, 15);
		cpI83.moveCenterTo(5, 25);
		cpI71.moveCenterTo(10, 60);
		cpI72.moveCenterTo(10, 65);
		cpI73.moveCenterTo(10, 105);
		cpI6.moveCenterTo(5, 155);
		cpNotI7.moveCenterTo(40, 60);
		cpNotI81.moveCenterTo(45, 20);
		cpNotI82.moveCenterTo(45, 55);
		cpNandI8I7.moveCenterTo(40, 135);
		cpNotRSH.moveCenterTo(75, 20);
		cpNandI7NotI81.moveCenterTo(75, 100);
		cpNandI7NotI82.moveCenterTo(75, 105);
		cpNotI6.moveCenterTo(75, 160);
		cpNandQWE.moveCenterTo(110, 215);

		new GUIWire(submodelModifiable, I8, cpI81, new Point[0]);
		new GUIWire(submodelModifiable, cpI81, cpI82, new Point[0]);
		new GUIWire(submodelModifiable, cpI81, cpI83, new Point[0]);
		new GUIWire(submodelModifiable, cpI82, notI8.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpI83, notI8.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, I7, cpI71, new Point[0]);
		new GUIWire(submodelModifiable, cpI71, notI7.getInputPins().get(0), new Point(10, 55));
		new GUIWire(submodelModifiable, cpI71, cpI72, new Point[0]);
		new GUIWire(submodelModifiable, cpI72, notI7.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpI72, cpI73, new Point[0]);
		new GUIWire(submodelModifiable, cpI73, nandI8I7.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpI83, nandI8I7.getInputPins().get(0), new Point(5, 95));
		new GUIWire(submodelModifiable, I6, cpI6, new Point(5, 100));
		new GUIWire(submodelModifiable, cpI6, notI6.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpI6, notI6.getInputPins().get(1), new Point(5, 165));
		new GUIWire(submodelModifiable, cpI82, nandRSH.getInputPins().get(0), new Point(5, 5), new Point(40, 5), new Point(40, 15));
		new GUIWire(submodelModifiable, notI7.getOutputPin(), cpNotI7, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI7, nandRSH.getInputPins().get(1), new Point(40, 25));
		new GUIWire(submodelModifiable, cpNotI7, nandRAMWE.getInputPins().get(1), new Point(40, 65));
		new GUIWire(submodelModifiable, notI8.getOutputPin(), cpNotI81, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI81, NSH, new Point(45, 5), new Point(135, 5), new Point(135, 20));
		new GUIWire(submodelModifiable, cpNotI81, cpNotI82, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI82, nandRAMWE.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpNotI82, nandI7NotI8.getInputPins().get(0), new Point(45, 95));
		new GUIWire(submodelModifiable, cpI73, nandI7NotI8.getInputPins().get(1), new Point(10, 115), new Point(45, 115),
				new Point(45, 105));
		new GUIWire(submodelModifiable, nandI8I7.getOutputPin(), cpNandI8I7, new Point(40, 100));
		new GUIWire(submodelModifiable, cpNandI8I7, nandLSH.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpNandI8I7, nandLSH.getInputPins().get(1), new Point(40, 145));
		new GUIWire(submodelModifiable, nandRSH.getOutputPin(), cpNotRSH, new Point[0]);
		new GUIWire(submodelModifiable, cpNotRSH, notRSH.getInputPins().get(0), new Point(75, 15));
		new GUIWire(submodelModifiable, cpNotRSH, notRSH.getInputPins().get(1), new Point(75, 25));
		new GUIWire(submodelModifiable, nandRAMWE.getOutputPin(), RAMWE, new Point(125, 60), new Point(125, 100));
		new GUIWire(submodelModifiable, nandI7NotI8.getOutputPin(), cpNandI7NotI81, new Point[0]);
		new GUIWire(submodelModifiable, cpNandI7NotI81, andI7NotI8.getInputPins().get(0), new Point(75, 95));
		new GUIWire(submodelModifiable, cpNandI7NotI81, cpNandI7NotI82, new Point[0]);
		new GUIWire(submodelModifiable, cpNandI7NotI82, andI7NotI8.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, nandLSH.getOutputPin(), LSH, new Point(125, 140), new Point(125, 180));
		new GUIWire(submodelModifiable, cpNandI7NotI82, nandQWE.getInputPins().get(0), new Point(75, 150));
		new GUIWire(submodelModifiable, notI6.getOutputPin(), cpNotI6, new Point[0]);
		new GUIWire(submodelModifiable, cpNotI6, nandQWE.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, notRSH.getOutputPin(), RSH, new Point(130, 20), new Point(130, 60));
		new GUIWire(submodelModifiable, andI7NotI8.getOutputPin(), nandYF.getInputPins().get(0));
		new GUIWire(submodelModifiable, cpNotI6, nandYF.getInputPins().get(1), new Point(75, 170), new Point(105, 170),
				new Point(105, 120));
		new GUIWire(submodelModifiable, nandQWE.getOutputPin(), cpNandQWE, new Point(110, 155));
		new GUIWire(submodelModifiable, cpNandQWE, notQWE.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpNandQWE, notQWE.getInputPins().get(1), new Point(110, 225));
		new GUIWire(submodelModifiable, nandYF.getOutputPin(), YF);
		new GUIWire(submodelModifiable, notQWE.getOutputPin(), QWE, new Point[0]);
	}
}