package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.atomic.GUINandGate;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIfulladder;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUImux1;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIxor;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901ALUOneBit extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUOneBit(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUOneBit");
		setSubmodelScale(.2);
		setInputPins("Cin", "CoutE", "CinE", "R", "RN", "S", "SN", "FN", "L");
		setOutputPins("Cout", "F");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin Cin = getSubmodelPin("Cin");
		Pin CoutE = getSubmodelPin("CoutE");
		Pin CinE = getSubmodelPin("CinE");
		Pin R = getSubmodelPin("R");
		Pin RN = getSubmodelPin("RN");
		Pin S = getSubmodelPin("S");
		Pin SN = getSubmodelPin("SN");
		Pin FN = getSubmodelPin("FN");
		Pin L = getSubmodelPin("L");
		Pin Cout = getSubmodelPin("Cout");
		Pin F = getSubmodelPin("F");

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

		new GUIWire(submodelModifiable, Cin, Cinand.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, CoutE, Coutand.getPin("A"), new Point(5, 75), new Point(5, 10), new Point(130, 10),
				new Point(130, 25));
		new GUIWire(submodelModifiable, CinE, Cinand.getPin("B"), new Point(7.5, 125), new Point(7.5, 35));
		new GUIWire(submodelModifiable, R, Rxor.getPin("A"));
		new GUIWire(submodelModifiable, RN, Rxor.getPin("B"));
		new GUIWire(submodelModifiable, S, Sxor.getPin("A"));
		new GUIWire(submodelModifiable, SN, Sxor.getPin("B"));
		new GUIWire(submodelModifiable, FN, Fxor.getPin("B"), new Point(130, 375), new Point(130, 85));
		new GUIWire(submodelModifiable, L, Fsel.getPin("S0"), new Point(87.5, 425), new Point(87.5, 75));
		new GUIWire(submodelModifiable, Cinand.getPin("Y"), add.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, Rxor.getPin("Y"), cpRXored, new Point(50, 195));
		new GUIWire(submodelModifiable, cpRXored, add.getPin("B"), new Point(50, 35));
		new GUIWire(submodelModifiable, cpRXored, nand.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, Sxor.getPin("Y"), cpSXored, new Point(55, 295));
		new GUIWire(submodelModifiable, cpSXored, add.getPin("C"), new Point(55, 45));
		new GUIWire(submodelModifiable, cpSXored, nand.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, add.getPin("Y"), Fsel.getPin("I0"), new Point(100, 25), new Point(100, 65), new Point(85, 65),
				new Point(85, 85));
		new GUIWire(submodelModifiable, add.getPin("Z"), Coutand.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, nand.getPin("Y"), Fsel.getPin("I1"), new Point(82.5, 65), new Point(82.5, 95));
		new GUIWire(submodelModifiable, Fsel.getPin("Y"), Fxor.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, Coutand.getPin("Y"), Cout, new Point[0]);
		new GUIWire(submodelModifiable, Fxor.getPin("Y"), F, new Point[0]);
	}
}