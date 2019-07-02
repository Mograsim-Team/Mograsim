package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIsel3_4 extends SimpleRectangularSubmodelComponent
{
	public GUIsel3_4(ViewModelModifiable model)
	{
		super(model, 1, "GUIsel3_4");
		setSubmodelScale(.2);
		setInputPins("SA", "SB", "SC", "A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4", "C1", "C2", "C3", "C4");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin SA = getSubmodelPin("SA");
		Pin SB = getSubmodelPin("SB");
		Pin SC = getSubmodelPin("SC");
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin B1 = getSubmodelPin("B1");
		Pin B2 = getSubmodelPin("B2");
		Pin B3 = getSubmodelPin("B3");
		Pin B4 = getSubmodelPin("B4");
		Pin C1 = getSubmodelPin("C1");
		Pin C2 = getSubmodelPin("C2");
		Pin C3 = getSubmodelPin("C3");
		Pin C4 = getSubmodelPin("C4");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

		GUIsel2_4 sel2_4 = new GUIsel2_4(submodelModifiable);
		GUInot4 not4 = new GUInot4(submodelModifiable);
		GUINandGate nandC1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandC2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandC3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandC4 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY1 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY2 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY3 = new GUINandGate(submodelModifiable, 1);
		GUINandGate nandY4 = new GUINandGate(submodelModifiable, 1);

		WireCrossPoint cpSC1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSC2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSC3 = new WireCrossPoint(submodelModifiable, 1);

		sel2_4.moveTo(35, 250);
		not4.moveTo(75, 250);
		nandC1.moveTo(50, 570);
		nandC2.moveTo(50, 620);
		nandC3.moveTo(50, 670);
		nandC4.moveTo(50, 720);
		nandY1.moveTo(152.5, 15);
		nandY2.moveTo(152.5, 65);
		nandY3.moveTo(152.5, 115);
		nandY4.moveTo(152.5, 165);
		cpSC1.moveCenterTo(30, 585);
		cpSC2.moveCenterTo(30, 635);
		cpSC3.moveCenterTo(30, 685);

		new GUIWire(submodelModifiable, SA, sel2_4.getPin("SA"), new Point(25, 25), new Point(25, 255));
		new GUIWire(submodelModifiable, SB, sel2_4.getPin("SB"), new Point(20, 75), new Point(20, 265));
		new GUIWire(submodelModifiable, A1, sel2_4.getPin("A1"), new Point(15, 175), new Point(15, 275));
		new GUIWire(submodelModifiable, A2, sel2_4.getPin("A2"), new Point(10, 225), new Point(10, 285));
		new GUIWire(submodelModifiable, A3, sel2_4.getPin("A3"), new Point(5, 275), new Point(5, 295));
		new GUIWire(submodelModifiable, A4, sel2_4.getPin("A4"), new Point(5, 325), new Point(5, 305));
		new GUIWire(submodelModifiable, B1, sel2_4.getPin("B1"), new Point(10, 375), new Point(10, 315));
		new GUIWire(submodelModifiable, B2, sel2_4.getPin("B2"), new Point(15, 425), new Point(15, 325));
		new GUIWire(submodelModifiable, B3, sel2_4.getPin("B3"), new Point(20, 475), new Point(20, 335));
		new GUIWire(submodelModifiable, B4, sel2_4.getPin("B4"), new Point(25, 525), new Point(25, 345));
		new GUIWire(submodelModifiable, sel2_4.getPin("Y1"), not4.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getPin("Y2"), not4.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getPin("Y3"), not4.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getPin("Y4"), not4.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, SC, cpSC1, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC1, nandC1.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpSC1, cpSC2, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC2, nandC2.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpSC2, cpSC3, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC3, nandC3.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpSC3, nandC4.getPin("B"), new Point(30, 735));
		new GUIWire(submodelModifiable, C1, nandC1.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, C2, nandC2.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, C3, nandC3.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, C4, nandC4.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, not4.getPin("Y1"), nandY1.getPin("A"), new Point(115, 255), new Point(115, 20));
		new GUIWire(submodelModifiable, not4.getPin("Y2"), nandY2.getPin("A"), new Point(120, 265), new Point(120, 70));
		new GUIWire(submodelModifiable, not4.getPin("Y3"), nandY3.getPin("A"), new Point(125, 275), new Point(125, 120));
		new GUIWire(submodelModifiable, not4.getPin("Y4"), nandY4.getPin("A"), new Point(130, 285), new Point(130, 170));
		new GUIWire(submodelModifiable, nandC1.getPin("Y"), nandY1.getPin("B"), new Point(135, 580), new Point(135, 30));
		new GUIWire(submodelModifiable, nandC2.getPin("Y"), nandY2.getPin("B"), new Point(140, 630), new Point(140, 80));
		new GUIWire(submodelModifiable, nandC3.getPin("Y"), nandY3.getPin("B"), new Point(145, 680), new Point(145, 130));
		new GUIWire(submodelModifiable, nandC4.getPin("Y"), nandY4.getPin("B"), new Point(150, 730), new Point(150, 180));
		new GUIWire(submodelModifiable, nandY1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nandY2.getPin("Y"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nandY3.getPin("Y"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nandY4.getPin("Y"), Y4, new Point[0]);
	}
}