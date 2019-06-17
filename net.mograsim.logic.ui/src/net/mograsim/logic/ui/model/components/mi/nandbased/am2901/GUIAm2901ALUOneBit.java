package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUINandGate;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUImux1;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIxor;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901ALUOneBit extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUOneBit(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUOneBit");
		setSubmodelScale(.2);
		setInputCount(9);
		setOutputCount(2);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin Cin = getInputSubmodelPins().get(0);
		Pin CoutE = getInputSubmodelPins().get(1);
		Pin CinE = getInputSubmodelPins().get(2);
		Pin R = getInputSubmodelPins().get(3);
		Pin RN = getInputSubmodelPins().get(4);
		Pin S = getInputSubmodelPins().get(5);
		Pin SN = getInputSubmodelPins().get(6);
		Pin FN = getInputSubmodelPins().get(7);
		Pin L = getInputSubmodelPins().get(8);
		Pin Cout = getOutputSubmodelPins().get(0);
		Pin F = getOutputSubmodelPins().get(1);

		GUIand Cinand = new GUIand(submodelModifiable);
		GUIxor Rxor = new GUIxor(submodelModifiable);
		GUIxor Sxor = new GUIxor(submodelModifiable);
		GUIfulladder add = new GUIfulladder(submodelModifiable);
		GUINandGate nand = new GUINandGate(submodelModifiable, 1);
		GUIand Coutand = new GUIand(submodelModifiable);
		GUImux1 Fsel = new GUImux1(submodelModifiable);
		GUIxor Fxor = new GUIxor(submodelModifiable);

		WireCrossPoint cpRXored = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSXored = new WireCrossPoint(submodelModifiable, 1);

		Cinand.moveTo(10, 20);
		Rxor.moveTo(10, 190);
		Sxor.moveTo(10, 290);
		add.moveTo(60, 20);
		nand.moveTo(60, 55);
		Coutand.moveTo(135, 20);
		Fsel.moveTo(90, 70);
		Fxor.moveTo(135, 70);
		cpRXored.moveCenterTo(50, 60);
		cpSXored.moveCenterTo(55, 70);

		new GUIWire(submodelModifiable, Cin, Cinand.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, CoutE, Coutand.getInputPins().get(0), new Point(5, 75), new Point(5, 10), new Point(130, 10),
				new Point(130, 25));
		new GUIWire(submodelModifiable, CinE, Cinand.getInputPins().get(1), new Point(7.5, 125), new Point(7.5, 35));
		new GUIWire(submodelModifiable, R, Rxor.getInputPins().get(0));
		new GUIWire(submodelModifiable, RN, Rxor.getInputPins().get(1));
		new GUIWire(submodelModifiable, S, Sxor.getInputPins().get(0));
		new GUIWire(submodelModifiable, SN, Sxor.getInputPins().get(1));
		new GUIWire(submodelModifiable, FN, Fxor.getInputPins().get(1), new Point(130, 375), new Point(130, 85));
		new GUIWire(submodelModifiable, L, Fsel.getInputPins().get(0), new Point(87.5, 425), new Point(87.5, 75));
		new GUIWire(submodelModifiable, Cinand.getOutputPins().get(0), add.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, Rxor.getOutputPins().get(0), cpRXored, new Point(50, 195));
		new GUIWire(submodelModifiable, cpRXored, add.getInputPins().get(1), new Point(50, 35));
		new GUIWire(submodelModifiable, cpRXored, nand.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, Sxor.getOutputPins().get(0), cpSXored, new Point(55, 295));
		new GUIWire(submodelModifiable, cpSXored, add.getInputPins().get(2), new Point(55, 45));
		new GUIWire(submodelModifiable, cpSXored, nand.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, add.getOutputPins().get(0), Fsel.getInputPins().get(1), new Point(100, 25), new Point(100, 65),
				new Point(85, 65), new Point(85, 85));
		new GUIWire(submodelModifiable, add.getOutputPins().get(1), Coutand.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, nand.getOutputPin(), Fsel.getInputPins().get(2), new Point(82.5, 65), new Point(82.5, 95));
		new GUIWire(submodelModifiable, Fsel.getOutputPins().get(0), Fxor.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, Coutand.getOutputPins().get(0), Cout, new Point[0]);
		new GUIWire(submodelModifiable, Fxor.getOutputPins().get(0), F, new Point[0]);
	}
}