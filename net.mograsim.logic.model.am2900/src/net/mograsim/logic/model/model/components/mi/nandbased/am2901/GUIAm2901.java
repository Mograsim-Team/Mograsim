package net.mograsim.logic.model.model.components.mi.nandbased.am2901;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.atomic.GUINandGate;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIand;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIdlatch4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUImux1_4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIor4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIram4;
import net.mograsim.logic.model.model.components.mi.nandbased.GUIsel3_4;
import net.mograsim.logic.model.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.model.model.wires.GUIWire;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.WireCrossPoint;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.StandardHighLevelStateHandler;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.standard.subcomponent.DelegatingSubcomponentHighLevelStateHandler;

public class GUIAm2901 extends SimpleRectangularSubmodelComponent
{
	private StandardHighLevelStateHandler highLevelStateHandler;

	public GUIAm2901(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIAm2901(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIAm2901", name);
		setSubmodelScale(.1);
		setInputPins("I8", "I7", "I6", "I5", "I4", "I3", "I2", "I1", "I0", "C", "Cn", "D1", "D2", "D3", "D4", "A0", "A1", "A2", "A3", "B0",
				"B1", "B2", "B3", "IRAMn", "IRAMn+3", "IQn", "IQn+3");
		setOutputPins("Y1", "Y2", "Y3", "Y4", "F=0", "Cn+4", "OVR", "F3", "ORAMn", "ORAMn+3", "OQn", "OQn+3");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin I8 = getSubmodelPin("I8");
		Pin I7 = getSubmodelPin("I7");
		Pin I6 = getSubmodelPin("I6");
		Pin I5 = getSubmodelPin("I5");
		Pin I4 = getSubmodelPin("I4");
		Pin I3 = getSubmodelPin("I3");
		Pin I2 = getSubmodelPin("I2");
		Pin I1 = getSubmodelPin("I1");
		Pin I0 = getSubmodelPin("I0");
		Pin C = getSubmodelPin("C");
		Pin Cn = getSubmodelPin("Cn");
		Pin D1 = getSubmodelPin("D1");
		Pin D2 = getSubmodelPin("D2");
		Pin D3 = getSubmodelPin("D3");
		Pin D4 = getSubmodelPin("D4");
		Pin A0 = getSubmodelPin("A0");
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin B0 = getSubmodelPin("B0");
		Pin B1 = getSubmodelPin("B1");
		Pin B2 = getSubmodelPin("B2");
		Pin B3 = getSubmodelPin("B3");
		Pin IRAMn = getSubmodelPin("IRAMn");
		Pin IRAMnplus3 = getSubmodelPin("IRAMn+3");
		Pin IQn = getSubmodelPin("IQn");
		Pin IQnplus3 = getSubmodelPin("IQn+3");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");
		Pin Feq0 = getSubmodelPin("F=0");
		Pin Cnplus4 = getSubmodelPin("Cn+4");
		Pin OVR = getSubmodelPin("OVR");
		Pin F3 = getSubmodelPin("F3");
		Pin ORAMn = getSubmodelPin("ORAMn");
		Pin ORAMnplus3 = getSubmodelPin("ORAMn+3");
		Pin OQn = getSubmodelPin("OQn");
		Pin OQnplus3 = getSubmodelPin("OQn+3");

		GUIAm2901DestDecode destDecode = new GUIAm2901DestDecode(submodelModifiable);
		GUImux1_4 Ymux = new GUImux1_4(submodelModifiable);
		GUIand ramweAnd = new GUIand(submodelModifiable);
		GUINandGate notC = new GUINandGate(submodelModifiable, 1);
		GUIAm2901ALUInclSourceDecodeInclFunctionDecode alu = new GUIAm2901ALUInclSourceDecodeInclFunctionDecode(submodelModifiable);
		GUIor4 Fneq0 = new GUIor4(submodelModifiable);
		GUINandGate notFneq0 = new GUINandGate(submodelModifiable, 1);
		GUIram4 ram = new GUIram4(submodelModifiable);
		GUIdlatch4 QAlatch = new GUIdlatch4(submodelModifiable);
		GUIdlatch4 QBlatch = new GUIdlatch4(submodelModifiable);
		GUIsel3_4 ramDsel = new GUIsel3_4(submodelModifiable);
		GUIsel3_4 qregDsel = new GUIsel3_4(submodelModifiable);
		GUIAm2901QReg qreg = new GUIAm2901QReg(submodelModifiable);

		WireCrossPoint cpC1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpC4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpLSH = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpNSH = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpRSH = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQA1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQA2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQA3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQA4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ4 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpOQn = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpOQnplus3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ2Rsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ3Rsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpORAMn = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpORAMnplus3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF3 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF11 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF21 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF31 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF41 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF12 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF22 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF32 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF42 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF1Lsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF2Lsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF3Lsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF2Rsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF3Rsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF4Rsh = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF13 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF23 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF33 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpF43 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpFneq0 = new WireCrossPoint(submodelModifiable, 1);

		destDecode.moveTo(15, 45);
		Ymux.moveTo(275, 135);
		ramweAnd.moveTo(190, 65);
		notC.moveTo(160, 75);
		alu.moveTo(240, 2110);
		Fneq0.moveTo(275, 445);
		notFneq0.moveTo(320, 440);
		ram.moveTo(95, 2220);
		QAlatch.moveTo(160, 2220);
		QBlatch.moveTo(160, 2275);
		ramDsel.moveTo(45, 2310);
		qregDsel.moveTo(45, 2510);
		qreg.moveTo(90, 2490);
		cpC1.moveCenterTo(155, 950);
		cpC2.moveCenterTo(155, 90);
		cpC3.moveCenterTo(155, 2265);
		cpC4.moveCenterTo(155, 2320);
		cpLSH.moveCenterTo(40, 2315);
		cpNSH.moveCenterTo(35, 2325);
		cpRSH.moveCenterTo(30, 2335);
		cpQA1.moveCenterTo(220, 2225);
		cpQA2.moveCenterTo(225, 2235);
		cpQA3.moveCenterTo(230, 2245);
		cpQA4.moveCenterTo(235, 2255);
		cpQ1.moveCenterTo(130, 2495);
		cpQ2.moveCenterTo(135, 2505);
		cpQ3.moveCenterTo(140, 2515);
		cpQ4.moveCenterTo(145, 2525);
		cpOQn.moveCenterTo(220, 2495);
		cpOQnplus3.moveCenterTo(235, 2525);
		cpQ2Rsh.moveCenterTo(35, 2625);
		cpQ3Rsh.moveCenterTo(40, 2635);
		cpORAMn.moveCenterTo(280, 2115);
		cpORAMnplus3.moveCenterTo(295, 2145);
		cpF3.moveCenterTo(330, 950);
		cpF11.moveCenterTo(255, 2090);
		cpF21.moveCenterTo(260, 2095);
		cpF31.moveCenterTo(265, 2100);
		cpF41.moveCenterTo(270, 2105);
		cpF12.moveCenterTo(255, 450);
		cpF22.moveCenterTo(260, 460);
		cpF32.moveCenterTo(265, 470);
		cpF42.moveCenterTo(270, 480);
		cpF1Lsh.moveCenterTo(10, 2355);
		cpF2Lsh.moveCenterTo(15, 2365);
		cpF3Lsh.moveCenterTo(20, 2375);
		cpF2Rsh.moveCenterTo(15, 2395);
		cpF3Rsh.moveCenterTo(20, 2405);
		cpF4Rsh.moveCenterTo(25, 2415);
		cpF13.moveCenterTo(10, 2385);
		cpF23.moveCenterTo(15, 2425);
		cpF33.moveCenterTo(20, 2435);
		cpF43.moveCenterTo(25, 2445);
		cpFneq0.moveCenterTo(315, 450);

		new GUIWire(submodelModifiable, I8, destDecode.getPin("I8"), new Point[0]);
		new GUIWire(submodelModifiable, I7, destDecode.getPin("I7"), new Point(5, 150), new Point(5, 60));
		new GUIWire(submodelModifiable, I6, destDecode.getPin("I6"), new Point(10, 250), new Point(10, 70));
		new GUIWire(submodelModifiable, I5, alu.getPin("I5"), new Point(130, 350), new Point(130, 2115));
		new GUIWire(submodelModifiable, I4, alu.getPin("I4"), new Point(125, 450), new Point(125, 2125));
		new GUIWire(submodelModifiable, I3, alu.getPin("I3"), new Point(120, 550), new Point(120, 2135));
		new GUIWire(submodelModifiable, I2, alu.getPin("I2"), new Point(115, 650), new Point(115, 2145));
		new GUIWire(submodelModifiable, I1, alu.getPin("I1"), new Point(110, 750), new Point(110, 2155));
		new GUIWire(submodelModifiable, I0, alu.getPin("I0"), new Point(105, 850), new Point(105, 2165));
		new GUIWire(submodelModifiable, C, cpC1, new Point[0]);
		new GUIWire(submodelModifiable, cpC1, cpC2, new Point[0]);
		new GUIWire(submodelModifiable, cpC2, notC.getPin("A"), new Point(155, 80));
		new GUIWire(submodelModifiable, cpC2, notC.getPin("B"), new Point[0]);
		new GUIWire(submodelModifiable, cpC1, cpC3, new Point[0]);
		new GUIWire(submodelModifiable, cpC3, QAlatch.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, cpC3, cpC4, new Point[0]);
		new GUIWire(submodelModifiable, cpC4, QBlatch.getPin("C"), new Point[0]);
		new GUIWire(submodelModifiable, cpC4, qreg.getPin("C"), new Point(155, 2485), new Point(80, 2485), new Point(80, 2495));
		new GUIWire(submodelModifiable, destDecode.getPin("LSH"), cpLSH, new Point(55, 90), new Point(55, 125), new Point(40, 125));
		new GUIWire(submodelModifiable, destDecode.getPin("NSH"), cpNSH, new Point(60, 50), new Point(60, 120), new Point(35, 120));
		new GUIWire(submodelModifiable, destDecode.getPin("RSH"), cpRSH, new Point(65, 60), new Point(65, 115), new Point(30, 115));
		new GUIWire(submodelModifiable, cpLSH, ramDsel.getPin("SA"), new Point[0]);
		new GUIWire(submodelModifiable, cpNSH, ramDsel.getPin("SB"), new Point[0]);
		new GUIWire(submodelModifiable, cpRSH, ramDsel.getPin("SC"), new Point[0]);
		new GUIWire(submodelModifiable, cpLSH, qregDsel.getPin("SA"), new Point(40, 2515));
		new GUIWire(submodelModifiable, cpNSH, qregDsel.getPin("SB"), new Point(35, 2525));
		new GUIWire(submodelModifiable, cpRSH, qregDsel.getPin("SC"), new Point(30, 2535));
		new GUIWire(submodelModifiable, A0, ram.getPin("A0"), new Point(80, 1550), new Point(80, 2225));
		new GUIWire(submodelModifiable, A1, ram.getPin("A1"), new Point(75, 1650), new Point(75, 2235));
		new GUIWire(submodelModifiable, A2, ram.getPin("A2"), new Point(70, 1750), new Point(70, 2245));
		new GUIWire(submodelModifiable, A3, ram.getPin("A3"), new Point(65, 1850), new Point(65, 2255));
		new GUIWire(submodelModifiable, B0, ram.getPin("B0"), new Point(60, 1950), new Point(60, 2265));
		new GUIWire(submodelModifiable, B1, ram.getPin("B1"), new Point(55, 2050), new Point(55, 2275));
		new GUIWire(submodelModifiable, B2, ram.getPin("B2"), new Point(50, 2150), new Point(50, 2285));
		new GUIWire(submodelModifiable, B3, ram.getPin("B3"), new Point(45, 2250), new Point(45, 2295));
		new GUIWire(submodelModifiable, ram.getPin("QA1"), QAlatch.getPin("D1"), new Point[0]);
		new GUIWire(submodelModifiable, ram.getPin("QA2"), QAlatch.getPin("D2"), new Point[0]);
		new GUIWire(submodelModifiable, ram.getPin("QA3"), QAlatch.getPin("D3"), new Point[0]);
		new GUIWire(submodelModifiable, ram.getPin("QA4"), QAlatch.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, ram.getPin("QB1"), QBlatch.getPin("D1"), new Point(150, 2265), new Point(150, 2280));
		new GUIWire(submodelModifiable, ram.getPin("QB2"), QBlatch.getPin("D2"), new Point(145, 2275), new Point(145, 2290));
		new GUIWire(submodelModifiable, ram.getPin("QB3"), QBlatch.getPin("D3"), new Point(140, 2285), new Point(140, 2300));
		new GUIWire(submodelModifiable, ram.getPin("QB4"), QBlatch.getPin("D4"), new Point(135, 2295), new Point(135, 2310));
		new GUIWire(submodelModifiable, Cn, alu.getPin("Cn"), new Point(100, 1050), new Point(100, 2175));
		new GUIWire(submodelModifiable, D1, alu.getPin("D1"), new Point(180, 1150), new Point(180, 2185));
		new GUIWire(submodelModifiable, D2, alu.getPin("D2"), new Point(175, 1250), new Point(175, 2195));
		new GUIWire(submodelModifiable, D3, alu.getPin("D3"), new Point(170, 1350), new Point(170, 2205));
		new GUIWire(submodelModifiable, D4, alu.getPin("D4"), new Point(165, 1450), new Point(165, 2215));
		new GUIWire(submodelModifiable, QAlatch.getPin("Q1"), cpQA1, new Point[0]);
		new GUIWire(submodelModifiable, QAlatch.getPin("Q2"), cpQA2, new Point[0]);
		new GUIWire(submodelModifiable, QAlatch.getPin("Q3"), cpQA3, new Point[0]);
		new GUIWire(submodelModifiable, QAlatch.getPin("Q4"), cpQA4, new Point[0]);
		new GUIWire(submodelModifiable, cpQA1, Ymux.getPin("I0_1"), new Point(220, 150));
		new GUIWire(submodelModifiable, cpQA2, Ymux.getPin("I0_2"), new Point(225, 160));
		new GUIWire(submodelModifiable, cpQA3, Ymux.getPin("I0_3"), new Point(230, 170));
		new GUIWire(submodelModifiable, cpQA4, Ymux.getPin("I0_4"), new Point(235, 180));
		new GUIWire(submodelModifiable, cpQA1, alu.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQA2, alu.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQA3, alu.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpQA4, alu.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, QBlatch.getPin("Q1"), alu.getPin("B1"), new Point(200, 2280), new Point(200, 2265));
		new GUIWire(submodelModifiable, QBlatch.getPin("Q2"), alu.getPin("B2"), new Point(205, 2290), new Point(205, 2275));
		new GUIWire(submodelModifiable, QBlatch.getPin("Q3"), alu.getPin("B3"), new Point(210, 2300), new Point(210, 2285));
		new GUIWire(submodelModifiable, QBlatch.getPin("Q4"), alu.getPin("B4"), new Point(215, 2310), new Point(215, 2295));
		new GUIWire(submodelModifiable, qreg.getPin("Q1"), cpQ1, new Point[0]);
		new GUIWire(submodelModifiable, qreg.getPin("Q2"), cpQ2, new Point[0]);
		new GUIWire(submodelModifiable, qreg.getPin("Q3"), cpQ3, new Point[0]);
		new GUIWire(submodelModifiable, qreg.getPin("Q4"), cpQ4, new Point[0]);
		new GUIWire(submodelModifiable, cpQ1, cpOQn, new Point[0]);
		new GUIWire(submodelModifiable, cpOQn, OQn, new Point(335, 2495), new Point(335, 1050));
		new GUIWire(submodelModifiable, cpQ4, cpOQnplus3, new Point[0]);
		new GUIWire(submodelModifiable, cpOQnplus3, OQnplus3, new Point(340, 2525), new Point(340, 1150));
		new GUIWire(submodelModifiable, cpQ2, cpQ2Rsh, new Point(135, 2670), new Point(30, 2670), new Point(30, 2635), new Point(35, 2635));
		new GUIWire(submodelModifiable, cpQ3, cpQ3Rsh, new Point(140, 2675), new Point(35, 2675), new Point(35, 2640), new Point(40, 2640));
		new GUIWire(submodelModifiable, cpQ2Rsh, qregDsel.getPin("C1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ3Rsh, qregDsel.getPin("C2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ4, qregDsel.getPin("C3"), new Point(145, 2680), new Point(40, 2680), new Point(40, 2645));
		new GUIWire(submodelModifiable, IQnplus3, qregDsel.getPin("C4"), new Point(5, 2650), new Point(5, 2655));
		new GUIWire(submodelModifiable, IQn, qregDsel.getPin("A1"), new Point(5, 2550), new Point(5, 2545));
		new GUIWire(submodelModifiable, cpQ1, qregDsel.getPin("A2"), new Point(130, 2665), new Point(25, 2665), new Point(25, 2630),
				new Point(30, 2630), new Point(30, 2555));
		new GUIWire(submodelModifiable, cpOQn, alu.getPin("Q1"), new Point(220, 2305));
		new GUIWire(submodelModifiable, cpQ2, alu.getPin("Q2"), new Point(225, 2505), new Point(225, 2315));
		new GUIWire(submodelModifiable, cpQ3, alu.getPin("Q3"), new Point(230, 2515), new Point(230, 2325));
		new GUIWire(submodelModifiable, cpOQnplus3, alu.getPin("Q4"), new Point(235, 2335));
		new GUIWire(submodelModifiable, cpQ2Rsh, qregDsel.getPin("A3"), new Point(35, 2565));
		new GUIWire(submodelModifiable, cpQ3Rsh, qregDsel.getPin("A4"), new Point(40, 2575));
		new GUIWire(submodelModifiable, qregDsel.getPin("Y1"), qreg.getPin("D1"), new Point[0]);
		new GUIWire(submodelModifiable, qregDsel.getPin("Y2"), qreg.getPin("D2"), new Point[0]);
		new GUIWire(submodelModifiable, qregDsel.getPin("Y3"), qreg.getPin("D3"), new Point[0]);
		new GUIWire(submodelModifiable, qregDsel.getPin("Y4"), qreg.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, alu.getPin("Cn+4"), Cnplus4, new Point(315, 2155), new Point(315, 550));
		new GUIWire(submodelModifiable, alu.getPin("OVR"), OVR, new Point(320, 2165), new Point(320, 650));
		new GUIWire(submodelModifiable, alu.getPin("F1"), cpORAMn, new Point[0]);
		new GUIWire(submodelModifiable, alu.getPin("F4"), cpORAMnplus3, new Point[0]);
		new GUIWire(submodelModifiable, cpORAMn, ORAMn, new Point(325, 2115), new Point(325, 850));
		new GUIWire(submodelModifiable, cpORAMnplus3, cpF3, new Point(330, 2145));
		new GUIWire(submodelModifiable, cpF3, ORAMnplus3, new Point[0]);
		new GUIWire(submodelModifiable, cpF3, F3, new Point(330, 750));
		new GUIWire(submodelModifiable, cpORAMn, cpF11, new Point(280, 2090));
		new GUIWire(submodelModifiable, alu.getPin("F2"), cpF21, new Point(285, 2125), new Point(285, 2095));
		new GUIWire(submodelModifiable, alu.getPin("F3"), cpF31, new Point(290, 2135), new Point(290, 2100));
		new GUIWire(submodelModifiable, cpORAMnplus3, cpF41, new Point(295, 2105));
		new GUIWire(submodelModifiable, cpF11, cpF12, new Point[0]);
		new GUIWire(submodelModifiable, cpF21, cpF22, new Point[0]);
		new GUIWire(submodelModifiable, cpF31, cpF32, new Point[0]);
		new GUIWire(submodelModifiable, cpF41, cpF42, new Point[0]);
		new GUIWire(submodelModifiable, cpF12, Fneq0.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpF22, Fneq0.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpF32, Fneq0.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpF42, Fneq0.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, cpF12, Ymux.getPin("I1_1"), new Point(255, 190));
		new GUIWire(submodelModifiable, cpF22, Ymux.getPin("I1_2"), new Point(260, 200));
		new GUIWire(submodelModifiable, cpF32, Ymux.getPin("I1_3"), new Point(265, 210));
		new GUIWire(submodelModifiable, cpF42, Ymux.getPin("I1_4"), new Point(270, 220));
		new GUIWire(submodelModifiable, cpF11, cpF1Lsh, new Point(10, 2090));
		new GUIWire(submodelModifiable, cpF21, cpF2Lsh, new Point(15, 2095));
		new GUIWire(submodelModifiable, cpF31, cpF3Lsh, new Point(20, 2100));
		new GUIWire(submodelModifiable, IRAMn, ramDsel.getPin("A1"), new Point(5, 2350), new Point(5, 2345));
		new GUIWire(submodelModifiable, cpF1Lsh, ramDsel.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpF2Lsh, ramDsel.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpF3Lsh, ramDsel.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, cpF2Lsh, cpF2Rsh, new Point[0]);
		new GUIWire(submodelModifiable, cpF3Lsh, cpF3Rsh, new Point[0]);
		new GUIWire(submodelModifiable, cpF41, cpF4Rsh, new Point(25, 2105));
		new GUIWire(submodelModifiable, cpF1Lsh, cpF13, new Point[0]);
		new GUIWire(submodelModifiable, cpF13, ramDsel.getPin("B1"), new Point[0]);
		new GUIWire(submodelModifiable, cpF2Rsh, ramDsel.getPin("B2"), new Point[0]);
		new GUIWire(submodelModifiable, cpF3Rsh, ramDsel.getPin("B3"), new Point[0]);
		new GUIWire(submodelModifiable, cpF4Rsh, ramDsel.getPin("B4"), new Point[0]);
		new GUIWire(submodelModifiable, cpF2Rsh, cpF23, new Point[0]);
		new GUIWire(submodelModifiable, cpF3Rsh, cpF33, new Point[0]);
		new GUIWire(submodelModifiable, cpF4Rsh, cpF43, new Point[0]);
		new GUIWire(submodelModifiable, cpF23, ramDsel.getPin("C1"), new Point[0]);
		new GUIWire(submodelModifiable, cpF33, ramDsel.getPin("C2"), new Point[0]);
		new GUIWire(submodelModifiable, cpF43, ramDsel.getPin("C3"), new Point[0]);
		new GUIWire(submodelModifiable, IRAMnplus3, ramDsel.getPin("C4"), new Point(5, 2450), new Point(5, 2455));
		new GUIWire(submodelModifiable, cpF13, qregDsel.getPin("B1"), new Point(10, 2585));
		new GUIWire(submodelModifiable, cpF23, qregDsel.getPin("B2"), new Point(15, 2595));
		new GUIWire(submodelModifiable, cpF33, qregDsel.getPin("B3"), new Point(20, 2605));
		new GUIWire(submodelModifiable, cpF43, qregDsel.getPin("B4"), new Point(25, 2615));
		new GUIWire(submodelModifiable, ramDsel.getPin("Y1"), ram.getPin("D1"), new Point[0]);
		new GUIWire(submodelModifiable, ramDsel.getPin("Y2"), ram.getPin("D2"), new Point[0]);
		new GUIWire(submodelModifiable, ramDsel.getPin("Y3"), ram.getPin("D3"), new Point[0]);
		new GUIWire(submodelModifiable, ramDsel.getPin("Y4"), ram.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, destDecode.getPin("RAMWE"), ramweAnd.getPin("A"), new Point[0]);
		new GUIWire(submodelModifiable, notC.getPin("Y"), ramweAnd.getPin("B"));
		new GUIWire(submodelModifiable, ramweAnd.getPin("Y"), ram.getPin("WE"), new Point(230, 70), new Point(230, 105), new Point(90, 105),
				new Point(90, 2305));
		new GUIWire(submodelModifiable, destDecode.getPin("QWE"), qreg.getPin("WE"), new Point(85, 100), new Point(85, 2505));
		new GUIWire(submodelModifiable, destDecode.getPin("YF"), Ymux.getPin("S0"), new Point(70, 80), new Point(70, 140));
		new GUIWire(submodelModifiable, Ymux.getPin("Y1"), Y1, new Point(335, 140), new Point(335, 50));
		new GUIWire(submodelModifiable, Ymux.getPin("Y2"), Y2, new Point[0]);
		new GUIWire(submodelModifiable, Ymux.getPin("Y3"), Y3, new Point(335, 160), new Point(335, 250));
		new GUIWire(submodelModifiable, Ymux.getPin("Y4"), Y4, new Point(325, 170), new Point(325, 350));
		new GUIWire(submodelModifiable, Fneq0.getPin("Y"), cpFneq0, new Point[0]);
		new GUIWire(submodelModifiable, cpFneq0, notFneq0.getPin("A"), new Point(315, 445));
		new GUIWire(submodelModifiable, cpFneq0, notFneq0.getPin("B"), new Point(315, 455));
		new GUIWire(submodelModifiable, notFneq0.getPin("Y"), Feq0, new Point[0]);

		highLevelStateHandler = new StandardHighLevelStateHandler(this);
		highLevelStateHandler.addSubcomponentHighLevelState("regs", DelegatingSubcomponentHighLevelStateHandler::new).set(ram, null);
		highLevelStateHandler.addSubcomponentHighLevelState("qreg", DelegatingSubcomponentHighLevelStateHandler::new).set(qreg, null);
	}

	@Override
	public Object getHighLevelState(String stateID)
	{
		return highLevelStateHandler.getHighLevelState(stateID);
	}

	@Override
	public void setHighLevelState(String stateID, Object newState)
	{
		highLevelStateHandler.setHighLevelState(stateID, newState);
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIAm2901.class.getCanonicalName(), (m, p, n) -> new GUIAm2901(m, n));
	}
}