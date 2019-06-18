package net.mograsim.logic.ui.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.GUIxor;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIAm2901ALUInclDecode extends SimpleRectangularSubmodelComponent
{
	public GUIAm2901ALUInclDecode(ViewModelModifiable model)
	{
		super(model, 1, "GUIAm2901ALUInclDecode");
		setSubmodelScale(.25);
		setInputCount(12);
		setOutputCount(6);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I5 = getInputSubmodelPins().get(0);
		Pin I4 = getInputSubmodelPins().get(1);
		Pin I3 = getInputSubmodelPins().get(2);
		Pin Cn = getInputSubmodelPins().get(3);
		Pin R1 = getInputSubmodelPins().get(4);
		Pin R2 = getInputSubmodelPins().get(5);
		Pin R3 = getInputSubmodelPins().get(6);
		Pin R4 = getInputSubmodelPins().get(7);
		Pin S1 = getInputSubmodelPins().get(8);
		Pin S2 = getInputSubmodelPins().get(9);
		Pin S3 = getInputSubmodelPins().get(10);
		Pin S4 = getInputSubmodelPins().get(11);
		Pin F1 = getOutputSubmodelPins().get(0);
		Pin F2 = getOutputSubmodelPins().get(1);
		Pin F3 = getOutputSubmodelPins().get(2);
		Pin F4 = getOutputSubmodelPins().get(3);
		Pin Cnplus4 = getOutputSubmodelPins().get(4);
		Pin OVR = getOutputSubmodelPins().get(5);

		GUIAm2901ALUFuncDecode funcDecode = new GUIAm2901ALUFuncDecode(submodelModifiable);
		GUIAm2901ALUOneBit alu1 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu2 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu3 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIAm2901ALUOneBit alu4 = new GUIAm2901ALUOneBit(submodelModifiable);
		GUIxor xorOVR = new GUIxor(submodelModifiable);

		WireCrossPoint cpI5 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpI3 = new WireCrossPoint(submodelModifiable, 1);
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

		funcDecode.moveTo(15, 25);
		alu1.moveTo(45, 80);
		alu2.moveTo(45, 180);
		alu3.moveTo(45, 280);
		alu4.moveTo(45, 380);
		xorOVR.moveTo(95, 400);
		cpI5.moveCenterTo(5, 20);
		cpI4.moveCenterTo(5, 60);
		cpI3.moveCenterTo(10, 65);
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

		new GUIWire(submodelModifiable, I5, cpI5, new Point[0]);
		new GUIWire(submodelModifiable, cpI5, funcDecode.getInputPins().get(0), new Point(5, 30));
		new GUIWire(submodelModifiable, I4, cpI4, new Point[0]);
		new GUIWire(submodelModifiable, cpI4, funcDecode.getInputPins().get(1), new Point(5, 40));
		new GUIWire(submodelModifiable, I3, cpI3, new Point(10, 100));
		new GUIWire(submodelModifiable, cpI3, funcDecode.getInputPins().get(2), new Point(10, 50));
		new GUIWire(submodelModifiable, funcDecode.getOutputPins().get(2), cpCoutE1, new Point(52.5, 50), new Point(52.5, 57.5),
				new Point(25, 57.5));
		new GUIWire(submodelModifiable, cpCoutE1, cpCoutE2, new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE2, cpCoutE3, new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE1, alu1.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE2, alu2.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE3, alu3.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpCoutE3, alu4.getInputPins().get(1), new Point(25, 395));
		new GUIWire(submodelModifiable, funcDecode.getOutputPins().get(0), cpCinE1, new Point(57.5, 30), new Point(57.5, 62.5),
				new Point(27.5, 62.5));
		new GUIWire(submodelModifiable, cpCinE1, cpCinE2, new Point[0]);
		new GUIWire(submodelModifiable, cpCinE2, cpCinE3, new Point[0]);
		new GUIWire(submodelModifiable, cpCinE1, alu1.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE2, alu2.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE3, alu3.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpCinE3, alu4.getInputPins().get(2), new Point(27.5, 405));
		new GUIWire(submodelModifiable, cpI3, cpRN1, new Point(30, 65));
		new GUIWire(submodelModifiable, cpRN1, cpRN2, new Point[0]);
		new GUIWire(submodelModifiable, cpRN2, cpRN3, new Point[0]);
		new GUIWire(submodelModifiable, cpRN1, alu1.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpRN2, alu2.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpRN3, alu3.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpRN3, alu4.getInputPins().get(4), new Point(30, 425));
		new GUIWire(submodelModifiable, cpI4, cpSN1, new Point(32.5, 60));
		new GUIWire(submodelModifiable, cpSN1, cpSN2, new Point[0]);
		new GUIWire(submodelModifiable, cpSN2, cpSN3, new Point[0]);
		new GUIWire(submodelModifiable, cpSN1, alu1.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpSN2, alu2.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpSN3, alu3.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpSN3, alu4.getInputPins().get(6), new Point(32.5, 445));
		new GUIWire(submodelModifiable, cpI5, cpFN1, new Point(60, 20), new Point(60, 65), new Point(35, 65));
		new GUIWire(submodelModifiable, cpFN1, cpFN2, new Point[0]);
		new GUIWire(submodelModifiable, cpFN2, cpFN3, new Point[0]);
		new GUIWire(submodelModifiable, cpFN1, alu1.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpFN2, alu2.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpFN3, alu3.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpFN3, alu4.getInputPins().get(7), new Point(35, 455));
		new GUIWire(submodelModifiable, funcDecode.getOutputPins().get(1), cpL1, new Point(55, 40), new Point(55, 60), new Point(37.5, 60));
		new GUIWire(submodelModifiable, cpL1, cpL2, new Point[0]);
		new GUIWire(submodelModifiable, cpL2, cpL3, new Point[0]);
		new GUIWire(submodelModifiable, cpL1, alu1.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpL2, alu2.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpL3, alu3.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpL3, alu4.getInputPins().get(8), new Point(37.5, 465));
		new GUIWire(submodelModifiable, R1, alu1.getInputPins().get(3), new Point(10, 180), new Point(10, 115));
		new GUIWire(submodelModifiable, R2, alu2.getInputPins().get(3), new Point(10, 220), new Point(10, 215));
		new GUIWire(submodelModifiable, R3, alu3.getInputPins().get(3), new Point(10, 260), new Point(10, 315));
		new GUIWire(submodelModifiable, R4, alu4.getInputPins().get(3), new Point(20, 300), new Point(20, 415));
		new GUIWire(submodelModifiable, S1, alu1.getInputPins().get(5), new Point(15, 340), new Point(15, 135));
		new GUIWire(submodelModifiable, S2, alu2.getInputPins().get(5), new Point(5, 380), new Point(5, 235));
		new GUIWire(submodelModifiable, S3, alu3.getInputPins().get(5), new Point(10, 420), new Point(10, 335));
		new GUIWire(submodelModifiable, S4, alu4.getInputPins().get(5), new Point(10, 460), new Point(10, 435));
		new GUIWire(submodelModifiable, Cn, alu1.getInputPins().get(0), new Point(5, 140), new Point(5, 85));
		new GUIWire(submodelModifiable, alu1.getOutputPins().get(0), alu2.getInputPins().get(0), new Point(85, 85), new Point(85, 175),
				new Point(40, 175), new Point(40, 185));
		new GUIWire(submodelModifiable, alu2.getOutputPins().get(0), alu3.getInputPins().get(0), new Point(85, 185), new Point(85, 275),
				new Point(40, 275), new Point(40, 285));
		new GUIWire(submodelModifiable, alu3.getOutputPins().get(0), cpCnplus3, new Point(85, 285));
		new GUIWire(submodelModifiable, cpCnplus3, alu4.getInputPins().get(0), new Point(40, 375), new Point(40, 385));
		new GUIWire(submodelModifiable, alu4.getOutputPins().get(0), cpCnplus4, new Point[0]);
		new GUIWire(submodelModifiable, alu1.getOutputPins().get(1), F1, new Point(90, 95), new Point(90, 20));
		new GUIWire(submodelModifiable, alu2.getOutputPins().get(1), F2, new Point(95, 195), new Point(95, 60));
		new GUIWire(submodelModifiable, alu3.getOutputPins().get(1), F3, new Point(100, 295), new Point(100, 100));
		new GUIWire(submodelModifiable, alu4.getOutputPins().get(1), F4, new Point(105, 395), new Point(105, 140));
		new GUIWire(submodelModifiable, cpCnplus3, xorOVR.getInputPins().get(0), new Point(90, 375), new Point(90, 405));
		new GUIWire(submodelModifiable, cpCnplus4, xorOVR.getInputPins().get(1), new Point(85, 415));
		new GUIWire(submodelModifiable, cpCnplus4, Cnplus4, new Point(130, 385), new Point(130, 180));
		new GUIWire(submodelModifiable, xorOVR.getOutputPins().get(0), OVR);
	}
}