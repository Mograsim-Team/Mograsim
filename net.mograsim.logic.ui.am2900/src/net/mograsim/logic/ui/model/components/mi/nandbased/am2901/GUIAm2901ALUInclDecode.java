package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIxor;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIAm2901ALUInclDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUInclDecode(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUInclDecode");
		setSubmodelScale(.25);
		setInputPins("I5", "I4", "I3", "Cn", "R1", "R2", "R3", "R4", "S1", "S2", "S3", "S4");
		setOutputPins("F1", "F2", "F3", "F4", "Cn+4", "OVR");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I5 = getSubmodelPin("I5");
		Pin I4 = getSubmodelPin("I4");
		Pin I3 = getSubmodelPin("I3");
		Pin Cn = getSubmodelPin("Cn");
		Pin R1 = getSubmodelPin("R1");
		Pin R2 = getSubmodelPin("R2");
		Pin R3 = getSubmodelPin("R3");
		Pin R4 = getSubmodelPin("R4");
		Pin S1 = getSubmodelPin("S1");
		Pin S2 = getSubmodelPin("S2");
		Pin S3 = getSubmodelPin("S3");
		Pin S4 = getSubmodelPin("S4");
		Pin F1 = getSubmodelPin("F1");
		Pin F2 = getSubmodelPin("F2");
		Pin F3 = getSubmodelPin("F3");
		Pin F4 = getSubmodelPin("F4");
		Pin Cnplus4 = getSubmodelPin("Cn+4");
		Pin OVR = getSubmodelPin("OVR");

		GUIAm2901ALUFuncDecode funcDecode = new GUIAm2901ALUFuncDecode(submodelModifiable);
		GUIAm2901ALUOneBit alu1 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu2 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu3 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu4 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIxor xorOVR = new GUIxor(submodelModifiable);

		WireCrossPoint cpCoutE1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCoutE2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCoutE3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCinE1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCinE2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCinE3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpRN1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpRN2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpRN3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSN1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSN2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpSN3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpFN1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpFN2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpFN3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpL1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpL2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpL3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCnplus3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpCnplus4 = new WireCrossPoint(submodelModifiable, 1);

		funcDecode.moveTo(20, 2.5);
		alu1.moveTo(45, 80);
		alu2.moveTo(45, 180);
		alu3.moveTo(45, 280);
		alu4.moveTo(45, 380);
		xorOVR.moveTo(95, 400);
		cpCoutE1.moveCenterTo(25, 95);
		cpCoutE2.moveCenterTo(25, 195);
		cpCoutE3.moveCenterTo(25, 295);
		cpCinE1.moveCenterTo(27.5, 105);
		cpCinE2.moveCenterTo(27.5, 205);
		cpCinE3.moveCenterTo(27.5, 305);
		cpRN1.moveCenterTo(30, 125);
		cpRN2.moveCenterTo(30, 225);
		cpRN3.moveCenterTo(30, 325);
		cpSN1.moveCenterTo(32.5, 145);
		cpSN2.moveCenterTo(32.5, 245);
		cpSN3.moveCenterTo(32.5, 345);
		cpFN1.moveCenterTo(35, 155);
		cpFN2.moveCenterTo(35, 255);
		cpFN3.moveCenterTo(35, 355);
		cpL1.moveCenterTo(37.5, 165);
		cpL2.moveCenterTo(37.5, 265);
		cpL3.moveCenterTo(37.5, 365);
		cpCnplus3.moveCenterTo(85, 375);
		cpCnplus4.moveCenterTo(85, 385);

		new GUIWire(submodelModifiable, I5, funcDecode.getPin("I5"), new Point(5, 20), new Point(5, 7.5));
		new GUIWire(submodelModifiable, I4, funcDecode.getPin("I4"), new Point(10, 60), new Point(10, 17.5));
		new GUIWire(submodelModifiable, I3, funcDecode.getPin("I3"), new Point(15, 100), new Point(15, 27.5));
		new GUIWire(submodelModifiable, funcDecode.getPin("SBE"), cpCoutE1, new Point(62.5, 37.5), new Point(62.5, 70), new Point(25, 70));
		new GUIWire(submodelModifiable, cpCoutE1, cpCoutE2, new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE2, cpCoutE3, new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE1, alu1.getPin("CoutE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE2, alu2.getPin("CoutE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE3, alu3.getPin("CoutE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE3, alu4.getPin("CoutE"), new Point(25, 395));
		new GUIWire(submodelModifiable, funcDecode.getPin("CinE"), cpCinE1, new Point(70, 7.5), new Point(70, 77.5), new Point(27.5, 77.5));
		new GUIWire(submodelModifiable, cpCinE1, cpCinE2, new Point[0]);
		new GUIWire(submodelModifiable, cpCinE2, cpCinE3, new Point[0]);
		new GUIWire(submodelModifiable, cpCinE1, alu1.getPin("CinE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE2, alu2.getPin("CinE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE3, alu3.getPin("CinE"), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE3, alu4.getPin("CinE"), new Point(27.5, 405));
		new GUIWire(submodelModifiable, funcDecode.getPin("RN"), cpRN1, new Point(57.5, 57.5), new Point(57.5, 65), new Point(30, 65));
		new GUIWire(submodelModifiable, cpRN1, cpRN2, new Point[0]);
		new GUIWire(submodelModifiable, cpRN2, cpRN3, new Point[0]);
		new GUIWire(submodelModifiable, cpRN1, alu1.getPin("RN"), new Point[0]);
		new GUIWire(submodelModifiable, cpRN2, alu2.getPin("RN"), new Point[0]);
		new GUIWire(submodelModifiable, cpRN3, alu3.getPin("RN"), new Point[0]);
		new GUIWire(submodelModifiable, cpRN3, alu4.getPin("RN"), new Point(30, 425));
		new GUIWire(submodelModifiable, funcDecode.getPin("SN"), cpSN1, new Point(65, 27.5), new Point(65, 72.5), new Point(32.5, 72.5));
		new GUIWire(submodelModifiable, cpSN1, cpSN2, new Point[0]);
		new GUIWire(submodelModifiable, cpSN2, cpSN3, new Point[0]);
		new GUIWire(submodelModifiable, cpSN1, alu1.getPin("SN"), new Point[0]);
		new GUIWire(submodelModifiable, cpSN2, alu2.getPin("SN"), new Point[0]);
		new GUIWire(submodelModifiable, cpSN3, alu3.getPin("SN"), new Point[0]);
		new GUIWire(submodelModifiable, cpSN3, alu4.getPin("SN"), new Point(32.5, 445));
		new GUIWire(submodelModifiable, funcDecode.getPin("FN"), cpFN1, new Point(60, 47.5), new Point(60, 67.5), new Point(35, 67.5));
		new GUIWire(submodelModifiable, cpFN1, cpFN2, new Point[0]);
		new GUIWire(submodelModifiable, cpFN2, cpFN3, new Point[0]);
		new GUIWire(submodelModifiable, cpFN1, alu1.getPin("FN"), new Point[0]);
		new GUIWire(submodelModifiable, cpFN2, alu2.getPin("FN"), new Point[0]);
		new GUIWire(submodelModifiable, cpFN3, alu3.getPin("FN"), new Point[0]);
		new GUIWire(submodelModifiable, cpFN3, alu4.getPin("FN"), new Point(35, 455));
		new GUIWire(submodelModifiable, funcDecode.getPin("L"), cpL1, new Point(67.5, 17.5), new Point(67.5, 75), new Point(37.5, 75));
		new GUIWire(submodelModifiable, cpL1, cpL2, new Point[0]);
		new GUIWire(submodelModifiable, cpL2, cpL3, new Point[0]);
		new GUIWire(submodelModifiable, cpL1, alu1.getPin("L"), new Point[0]);
		new GUIWire(submodelModifiable, cpL2, alu2.getPin("L"), new Point[0]);
		new GUIWire(submodelModifiable, cpL3, alu3.getPin("L"), new Point[0]);
		new GUIWire(submodelModifiable, cpL3, alu4.getPin("L"), new Point(37.5, 465));
		new GUIWire(submodelModifiable, R1, alu1.getPin("R"), new Point(10, 180), new Point(10, 115));
		new GUIWire(submodelModifiable, R2, alu2.getPin("R"), new Point(10, 220), new Point(10, 215));
		new GUIWire(submodelModifiable, R3, alu3.getPin("R"), new Point(10, 260), new Point(10, 315));
		new GUIWire(submodelModifiable, R4, alu4.getPin("R"), new Point(20, 300), new Point(20, 415));
		new GUIWire(submodelModifiable, S1, alu1.getPin("S"), new Point(15, 340), new Point(15, 135));
		new GUIWire(submodelModifiable, S2, alu2.getPin("S"), new Point(5, 380), new Point(5, 235));
		new GUIWire(submodelModifiable, S3, alu3.getPin("S"), new Point(10, 420), new Point(10, 335));
		new GUIWire(submodelModifiable, S4, alu4.getPin("S"), new Point(10, 460), new Point(10, 435));
		new GUIWire(submodelModifiable, Cn, alu1.getPin("Cin"), new Point(5, 140), new Point(5, 85));
		new GUIWire(submodelModifiable, alu1.getPin("Cout"), alu2.getPin("Cin"), new Point(85, 85), new Point(85, 175), new Point(40, 175),
				new Point(40, 185));
		new GUIWire(submodelModifiable, alu2.getPin("Cout"), alu3.getPin("Cin"), new Point(85, 185), new Point(85, 275), new Point(40, 275),
				new Point(40, 285));
		new GUIWire(submodelModifiable, alu3.getPin("Cout"), cpCnplus3, new Point(85, 285));
		new GUIWire(submodelModifiable, cpCnplus3, alu4.getPin("Cin"), new Point(40, 375), new Point(40, 385));
		new GUIWire(submodelModifiable, alu4.getPin("Cout"), cpCnplus4, new Point[0]);
		new GUIWire(submodelModifiable, alu1.getPin("F"), F1, new Point(90, 95), new Point(90, 20));
		new GUIWire(submodelModifiable, alu2.getPin("F"), F2, new Point(95, 195), new Point(95, 60));
		new GUIWire(submodelModifiable, alu3.getPin("F"), F3, new Point(100, 295), new Point(100, 100));
		new GUIWire(submodelModifiable, alu4.getPin("F"), F4, new Point(105, 395), new Point(105, 140));
		new GUIWire(submodelModifiable, cpCnplus3, xorOVR.getPin("A"), new Point(90, 375), new Point(90, 405));
		new GUIWire(submodelModifiable, cpCnplus4, xorOVR.getPin("B"), new Point(85, 415));
		new GUIWire(submodelModifiable, cpCnplus4, Cnplus4, new Point(130, 385), new Point(130, 180));
		new GUIWire(submodelModifiable, xorOVR.getPin("Y"), OVR);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901ALUInclDecode.class.getCanonicalName(),
				(m, p) -> new GUIAm2901ALUInclDecode(m));
	}
}