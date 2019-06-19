package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
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
		Pin SA = getInputSubmodelPins().get(0);
		Pin SB = getInputSubmodelPins().get(1);
		Pin SC = getInputSubmodelPins().get(2);
		Pin A1 = getInputSubmodelPins().get(3);
		Pin A2 = getInputSubmodelPins().get(4);
		Pin A3 = getInputSubmodelPins().get(5);
		Pin A4 = getInputSubmodelPins().get(6);
		Pin B1 = getInputSubmodelPins().get(7);
		Pin B2 = getInputSubmodelPins().get(8);
		Pin B3 = getInputSubmodelPins().get(9);
		Pin B4 = getInputSubmodelPins().get(10);
		Pin C1 = getInputSubmodelPins().get(11);
		Pin C2 = getInputSubmodelPins().get(12);
		Pin C3 = getInputSubmodelPins().get(13);
		Pin C4 = getInputSubmodelPins().get(14);
		Pin Y1 = getOutputSubmodelPins().get(0);
		Pin Y2 = getOutputSubmodelPins().get(1);
		Pin Y3 = getOutputSubmodelPins().get(2);
		Pin Y4 = getOutputSubmodelPins().get(3);

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

		new GUIWire(submodelModifiable, SA, sel2_4.getInputPins().get(0), new Point(25, 25), new Point(25, 255));
		new GUIWire(submodelModifiable, SB, sel2_4.getInputPins().get(1), new Point(20, 75), new Point(20, 265));
		new GUIWire(submodelModifiable, A1, sel2_4.getInputPins().get(2), new Point(15, 175), new Point(15, 275));
		new GUIWire(submodelModifiable, A2, sel2_4.getInputPins().get(3), new Point(10, 225), new Point(10, 285));
		new GUIWire(submodelModifiable, A3, sel2_4.getInputPins().get(4), new Point(5, 275), new Point(5, 295));
		new GUIWire(submodelModifiable, A4, sel2_4.getInputPins().get(5), new Point(5, 325), new Point(5, 305));
		new GUIWire(submodelModifiable, B1, sel2_4.getInputPins().get(6), new Point(10, 375), new Point(10, 315));
		new GUIWire(submodelModifiable, B2, sel2_4.getInputPins().get(7), new Point(15, 425), new Point(15, 325));
		new GUIWire(submodelModifiable, B3, sel2_4.getInputPins().get(8), new Point(20, 475), new Point(20, 335));
		new GUIWire(submodelModifiable, B4, sel2_4.getInputPins().get(9), new Point(25, 525), new Point(25, 345));
		new GUIWire(submodelModifiable, sel2_4.getOutputPins().get(0), not4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getOutputPins().get(1), not4.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getOutputPins().get(2), not4.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, sel2_4.getOutputPins().get(3), not4.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, SC, cpSC1, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC1, nandC1.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpSC1, cpSC2, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC2, nandC2.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpSC2, cpSC3, new Point(30, 125));
		new GUIWire(submodelModifiable, cpSC3, nandC3.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpSC3, nandC4.getInputPins().get(1), new Point(30, 735));
		new GUIWire(submodelModifiable, C1, nandC1.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, C2, nandC2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, C3, nandC3.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, C4, nandC4.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, not4.getOutputPins().get(0), nandY1.getInputPins().get(0), new Point(115, 255), new Point(115, 20));
		new GUIWire(submodelModifiable, not4.getOutputPins().get(1), nandY2.getInputPins().get(0), new Point(120, 265), new Point(120, 70));
		new GUIWire(submodelModifiable, not4.getOutputPins().get(2), nandY3.getInputPins().get(0), new Point(125, 275),
				new Point(125, 120));
		new GUIWire(submodelModifiable, not4.getOutputPins().get(3), nandY4.getInputPins().get(0), new Point(130, 285),
				new Point(130, 170));
		new GUIWire(submodelModifiable, nandC1.getOutputPin(), nandY1.getInputPins().get(1), new Point(135, 580), new Point(135, 30));
		new GUIWire(submodelModifiable, nandC2.getOutputPin(), nandY2.getInputPins().get(1), new Point(140, 630), new Point(140, 80));
		new GUIWire(submodelModifiable, nandC3.getOutputPin(), nandY3.getInputPins().get(1), new Point(145, 680), new Point(145, 130));
		new GUIWire(submodelModifiable, nandC4.getOutputPin(), nandY4.getInputPins().get(1), new Point(150, 730), new Point(150, 180));
		new GUIWire(submodelModifiable, nandY1.getOutputPin(), Y1, new Point[0]);
		new GUIWire(submodelModifiable, nandY2.getOutputPin(), Y2, new Point[0]);
		new GUIWire(submodelModifiable, nandY3.getOutputPin(), Y3, new Point[0]);
		new GUIWire(submodelModifiable, nandY4.getOutputPin(), Y4, new Point[0]);
	}
}