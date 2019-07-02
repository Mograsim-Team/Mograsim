package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIsel2_4;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIsel3_4;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901ALUInclSourceDecodeInclFunctionDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUInclSourceDecodeInclFunctionDecode(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUInclSourceDecodeInclFunctionDecode");
		setSubmodelScale(.25);
		setInputPins("I5", "I4", "I3", "I2", "I1", "I0", "Cn", "D1", "D2", "D3", "D4", "A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4", "Q1",
				"Q2", "Q3", "Q4");
		setOutputPins("F1", "F2", "F3", "F4", "Cn+4", "OVR");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I5 = getSubmodelPin("I5");
		Pin I4 = getSubmodelPin("I4");
		Pin I3 = getSubmodelPin("I3");
		Pin I2 = getSubmodelPin("I2");
		Pin I1 = getSubmodelPin("I1");
		Pin I0 = getSubmodelPin("I0");
		Pin Cn = getSubmodelPin("Cn");
		Pin D1 = getSubmodelPin("D1");
		Pin D2 = getSubmodelPin("D2");
		Pin D3 = getSubmodelPin("D3");
		Pin D4 = getSubmodelPin("D4");
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin B1 = getSubmodelPin("B1");
		Pin B2 = getSubmodelPin("B2");
		Pin B3 = getSubmodelPin("B3");
		Pin B4 = getSubmodelPin("B4");
		Pin Q1 = getSubmodelPin("Q1");
		Pin Q2 = getSubmodelPin("Q2");
		Pin Q3 = getSubmodelPin("Q3");
		Pin Q4 = getSubmodelPin("Q4");
		Pin F1 = getSubmodelPin("F1");
		Pin F2 = getSubmodelPin("F2");
		Pin F3 = getSubmodelPin("F3");
		Pin F4 = getSubmodelPin("F4");
		Pin Cnplus4 = getSubmodelPin("Cn+4");
		Pin OVR = getSubmodelPin("OVR");

		GUIAm2901SourceDecode sourceDecode = new GUIAm2901SourceDecode(submodelModifiable);
		GUIsel2_4 selR = new GUIsel2_4(submodelModifiable);
		GUIsel3_4 selS = new GUIsel3_4(submodelModifiable);
		GUIAm2901ALUInclDecode alu = new GUIAm2901ALUInclDecode(submodelModifiable);

		WireCrossPoint cpA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpA4 = new WireCrossPoint(submodelModifiable, 1);

		sourceDecode.moveTo(15, 165);
		selR.moveTo(45, 365);
		selS.moveTo(45, 575);
		alu.moveTo(60, 15);
		cpA1.moveCenterTo(10, 460);
		cpA2.moveCenterTo(15, 500);
		cpA3.moveCenterTo(20, 540);
		cpA4.moveCenterTo(25, 580);

		new GUIWire(submodelModifiable, I5, alu.getPin("I5"), new Point[0]);
		new GUIWire(submodelModifiable, I4, alu.getPin("I4"), new Point(5, 60), new Point(5, 30));
		new GUIWire(submodelModifiable, I3, alu.getPin("I3"), new Point(15, 100), new Point(15, 40));
		new GUIWire(submodelModifiable, I2, sourceDecode.getPin("I2"), new Point(5, 140), new Point(5, 170));
		new GUIWire(submodelModifiable, I1, sourceDecode.getPin("I1"), new Point[0]);
		new GUIWire(submodelModifiable, I0, sourceDecode.getPin("I0"), new Point(5, 220), new Point(5, 190));
		new GUIWire(submodelModifiable, Cn, alu.getPin("Cn"), new Point(10, 260), new Point(10, 50));
		new GUIWire(submodelModifiable, D1, selR.getPin("A1"), new Point(15, 300), new Point(15, 390));
		new GUIWire(submodelModifiable, D2, selR.getPin("A2"), new Point(10, 340), new Point(10, 400));
		new GUIWire(submodelModifiable, D3, selR.getPin("A3"), new Point(5, 380), new Point(5, 410));
		new GUIWire(submodelModifiable, D4, selR.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, A1, cpA1, new Point[0]);
		new GUIWire(submodelModifiable, A2, cpA2, new Point[0]);
		new GUIWire(submodelModifiable, A3, cpA3, new Point[0]);
		new GUIWire(submodelModifiable, A4, cpA4, new Point[0]);
		new GUIWire(submodelModifiable, cpA1, selR.getPin("B1"), new Point(10, 430));
		new GUIWire(submodelModifiable, cpA2, selR.getPin("B2"), new Point(15, 440));
		new GUIWire(submodelModifiable, cpA3, selR.getPin("B3"), new Point(20, 450));
		new GUIWire(submodelModifiable, cpA4, selR.getPin("B4"), new Point(25, 460));
		new GUIWire(submodelModifiable, cpA1, selS.getPin("A1"), new Point(10, 610));
		new GUIWire(submodelModifiable, cpA2, selS.getPin("A2"), new Point(15, 620));
		new GUIWire(submodelModifiable, cpA3, selS.getPin("A3"), new Point(20, 630));
		new GUIWire(submodelModifiable, cpA4, selS.getPin("A4"), new Point(25, 640));
		new GUIWire(submodelModifiable, B1, selS.getPin("B1"), new Point(5, 620), new Point(5, 650));
		new GUIWire(submodelModifiable, B2, selS.getPin("B2"), new Point[0]);
		new GUIWire(submodelModifiable, B3, selS.getPin("B3"), new Point(5, 700), new Point(5, 670));
		new GUIWire(submodelModifiable, B4, selS.getPin("B4"), new Point(10, 740), new Point(10, 680));
		new GUIWire(submodelModifiable, Q1, selS.getPin("C1"), new Point(15, 780), new Point(15, 690));
		new GUIWire(submodelModifiable, Q2, selS.getPin("C2"), new Point(20, 820), new Point(20, 700));
		new GUIWire(submodelModifiable, Q3, selS.getPin("C3"), new Point(25, 860), new Point(25, 710));
		new GUIWire(submodelModifiable, Q4, selS.getPin("C4"), new Point(30, 900), new Point(30, 720));
		new GUIWire(submodelModifiable, sourceDecode.getPin("SQ"), selS.getPin("SC"), new Point(75, 170), new Point(75, 240),
				new Point(30, 240), new Point(30, 600));
		new GUIWire(submodelModifiable, sourceDecode.getPin("RA"), selR.getPin("SB"), new Point(70, 180), new Point(70, 235),
				new Point(20, 235), new Point(20, 380));
		new GUIWire(submodelModifiable, sourceDecode.getPin("SB"), selS.getPin("SB"), new Point(65, 190), new Point(65, 230),
				new Point(35, 230), new Point(35, 590));
		new GUIWire(submodelModifiable, sourceDecode.getPin("SA"), selS.getPin("SA"), new Point(60, 200), new Point(60, 225),
				new Point(40, 225), new Point(40, 580));
		new GUIWire(submodelModifiable, sourceDecode.getPin("RD"), selR.getPin("SA"), new Point(55, 210), new Point(55, 220),
				new Point(25, 220), new Point(25, 370));
		new GUIWire(submodelModifiable, selR.getPin("Y1"), alu.getPin("R1"), new Point(82.5, 370), new Point(82.5, 162.5),
				new Point(20, 162.5), new Point(20, 60));
		new GUIWire(submodelModifiable, selR.getPin("Y2"), alu.getPin("R2"), new Point(85, 380), new Point(85, 160), new Point(22.5, 160),
				new Point(22.5, 70));
		new GUIWire(submodelModifiable, selR.getPin("Y3"), alu.getPin("R3"), new Point(87.5, 390), new Point(87.5, 157.5),
				new Point(25, 157.5), new Point(25, 80));
		new GUIWire(submodelModifiable, selR.getPin("Y4"), alu.getPin("R4"), new Point(90, 400), new Point(90, 155), new Point(27.5, 155),
				new Point(27.5, 90));
		new GUIWire(submodelModifiable, selS.getPin("Y1"), alu.getPin("S1"), new Point(92.5, 580), new Point(92.5, 152.5),
				new Point(30, 152.5), new Point(30, 100));
		new GUIWire(submodelModifiable, selS.getPin("Y2"), alu.getPin("S2"), new Point(95, 590), new Point(95, 150), new Point(32.5, 150),
				new Point(32.5, 110));
		new GUIWire(submodelModifiable, selS.getPin("Y3"), alu.getPin("S3"), new Point(97.5, 600), new Point(97.5, 147.5),
				new Point(35, 147.5), new Point(35, 120));
		new GUIWire(submodelModifiable, selS.getPin("Y4"), alu.getPin("S4"), new Point(100, 610), new Point(100, 145), new Point(37.5, 145),
				new Point(37.5, 130));
		new GUIWire(submodelModifiable, alu.getPin("F1"), F1, new Point[0]);
		new GUIWire(submodelModifiable, alu.getPin("F2"), F2, new Point(135, 30), new Point(135, 60));
		new GUIWire(submodelModifiable, alu.getPin("F3"), F3, new Point(130, 40), new Point(130, 100));
		new GUIWire(submodelModifiable, alu.getPin("F4"), F4, new Point(125, 50), new Point(125, 140));
		new GUIWire(submodelModifiable, alu.getPin("Cn+4"), Cnplus4, new Point(120, 60), new Point(120, 180));
		new GUIWire(submodelModifiable, alu.getPin("OVR"), OVR, new Point(115, 70), new Point(115, 220));
	}
}