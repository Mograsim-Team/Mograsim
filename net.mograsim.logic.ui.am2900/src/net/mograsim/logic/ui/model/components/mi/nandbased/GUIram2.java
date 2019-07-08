package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public class GUIram2 extends SimpleRectangularSubmodelComponent
{
	private GUIdlatch4 cell00;
	private GUIdlatch4 cell01;
	private GUIdlatch4 cell10;
	private GUIdlatch4 cell11;

	public GUIram2(ViewModelModifiable model)
	{
		this(model, null);
	}

	public GUIram2(ViewModelModifiable model, String name)
	{
		super(model, 1, "GUIram2", name);
		setSubmodelScale(.1);
		setInputPins("A0", "A1", "B0", "B1", "WE", "D1", "D2", "D3", "D4");
		setOutputPins("QA1", "QA2", "QA3", "QA4", "QB1", "QB2", "QB3", "QB4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		//@formatter:off
		Pin  A0 = getSubmodelPin("A0");
		Pin  A1 = getSubmodelPin("A1");
		Pin  B0 = getSubmodelPin("B0");
		Pin  B1 = getSubmodelPin("B1");
		Pin  WE = getSubmodelPin("WE");
		Pin  D1 = getSubmodelPin("D1");
		Pin  D2 = getSubmodelPin("D2");
		Pin  D3 = getSubmodelPin("D3");
		Pin  D4 = getSubmodelPin("D4");
		Pin QA1 = getSubmodelPin("QA1");
		Pin QA2 = getSubmodelPin("QA2");
		Pin QA3 = getSubmodelPin("QA3");
		Pin QA4 = getSubmodelPin("QA4");
		Pin QB1 = getSubmodelPin("QB1");
		Pin QB2 = getSubmodelPin("QB2");
		Pin QB3 = getSubmodelPin("QB3");
		Pin QB4 = getSubmodelPin("QB4");

		GUIdemux2   demuxA   = new GUIdemux2  (submodelModifiable);
		GUIdemux2   demuxB   = new GUIdemux2  (submodelModifiable);
		GUIand41    weAndB   = new GUIand41   (submodelModifiable);
		cell00   = new GUIdlatch4 (submodelModifiable);
		cell01   = new GUIdlatch4 (submodelModifiable);
		cell10   = new GUIdlatch4 (submodelModifiable);
		cell11   = new GUIdlatch4 (submodelModifiable);
		GUIand41    andA00   = new GUIand41   (submodelModifiable);
		GUIandor414 andorA01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA11 = new GUIandor414(submodelModifiable);
		GUIand41    andB00   = new GUIand41   (submodelModifiable);
		GUIandor414 andorB01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB11 = new GUIandor414(submodelModifiable);

		WireCrossPoint cpB00  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB01  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB10  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB11  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD1in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD2in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD3in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD4in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD101 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD201 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD301 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD401 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD110 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD210 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD310 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD410 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ100 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ200 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ300 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ400 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ101 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ201 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ301 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ401 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ110 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ210 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ310 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ410 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ111 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ211 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ311 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpQ411 = new WireCrossPoint(submodelModifiable, 1);

		demuxA  .moveTo( 55,  45);
		demuxB  .moveTo( 55, 150);
		weAndB  .moveTo(130, 150);
		cell00  .moveTo( 55, 325);
		cell01  .moveTo( 55, 475);
		cell10  .moveTo( 55, 625);
		cell11  .moveTo( 55, 775);
		andA00  .moveTo(235, 375);
		andorA01.moveTo(235, 485);
		andorA10.moveTo(235, 635);
		andorA11.moveTo(235, 785);
		andB00  .moveTo(135, 325);
		andorB01.moveTo(135, 435);
		andorB10.moveTo(135, 585);
		andorB11.moveTo(135, 735);
		cpB00 .moveCenterTo(110, 155);
		cpB01 .moveCenterTo(105, 165);
		cpB10 .moveCenterTo(100, 175);
		cpB11 .moveCenterTo( 95, 185);
		cpD1in.moveCenterTo( 35, 550);
		cpD2in.moveCenterTo( 40, 650);
		cpD3in.moveCenterTo( 45, 750);
		cpD4in.moveCenterTo( 50, 810);
		cpD101.moveCenterTo( 35, 480);
		cpD201.moveCenterTo( 40, 490);
		cpD301.moveCenterTo( 45, 500);
		cpD401.moveCenterTo( 50, 510);
		cpD110.moveCenterTo( 35, 630);
		cpD210.moveCenterTo( 40, 640);
		cpD310.moveCenterTo( 45, 650);
		cpD410.moveCenterTo( 50, 660);
		cpQ100.moveCenterTo(130, 330);
		cpQ200.moveCenterTo(125, 340);
		cpQ300.moveCenterTo(120, 350);
		cpQ400.moveCenterTo(115, 360);
		cpQ101.moveCenterTo(130, 480);
		cpQ201.moveCenterTo(125, 490);
		cpQ301.moveCenterTo(120, 500);
		cpQ401.moveCenterTo(115, 510);
		cpQ110.moveCenterTo(130, 630);
		cpQ210.moveCenterTo(125, 640);
		cpQ310.moveCenterTo(120, 650);
		cpQ410.moveCenterTo(115, 660);
		cpQ111.moveCenterTo(130, 780);
		cpQ211.moveCenterTo(125, 790);
		cpQ311.moveCenterTo(120, 800);
		cpQ411.moveCenterTo(115, 810);

		new GUIWire(submodelModifiable, A0, demuxA.getPin("S0"), new Point[0]);
		new GUIWire(submodelModifiable, A1, demuxA.getPin("S1"), new Point(10, 150), new Point(10,  60));
		new GUIWire(submodelModifiable, B0, demuxB.getPin("S0"), new Point( 5, 250), new Point( 5, 155));
		new GUIWire(submodelModifiable, B1, demuxB.getPin("S1"), new Point(10, 350), new Point(10, 165));
		new GUIWire(submodelModifiable, demuxB.getPin("Y00"), cpB00, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getPin("Y01"), cpB01, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getPin("Y10"), cpB10, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getPin("Y11"), cpB11, new Point[0]);
		new GUIWire(submodelModifiable, cpB00, weAndB.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpB01, weAndB.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpB10, weAndB.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpB11, weAndB.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, WE, weAndB.getPin("B"), new Point(5, 450), new Point(5, 300), new Point(125, 300), new Point(125, 195));
		new GUIWire(submodelModifiable, weAndB.getPin("Y1"), cell00.getPin("C"), new Point(185, 155), new Point(185, 250), new Point(30, 250), new Point(30, 370));
		new GUIWire(submodelModifiable, weAndB.getPin("Y2"), cell01.getPin("C"), new Point(180, 165), new Point(180, 245), new Point(25, 245), new Point(25, 520));
		new GUIWire(submodelModifiable, weAndB.getPin("Y3"), cell10.getPin("C"), new Point(175, 175), new Point(175, 240), new Point(20, 240), new Point(20, 670));
		new GUIWire(submodelModifiable, weAndB.getPin("Y4"), cell11.getPin("C"), new Point(170, 185), new Point(170, 235), new Point(15, 235), new Point(15, 820));
		new GUIWire(submodelModifiable, D1, cpD1in                 , new Point[0]);
		new GUIWire(submodelModifiable, D2, cpD2in                 , new Point[0]);
		new GUIWire(submodelModifiable, D3, cpD3in                 , new Point[0]);
		new GUIWire(submodelModifiable, D4, cpD4in                 , new Point(50, 850));
		new GUIWire(submodelModifiable, cpD101, cell00.getPin("D1"), new Point(35, 330));
		new GUIWire(submodelModifiable, cpD201, cell00.getPin("D2"), new Point(40, 340));
		new GUIWire(submodelModifiable, cpD301, cell00.getPin("D3"), new Point(45, 350));
		new GUIWire(submodelModifiable, cpD401, cell00.getPin("D4"), new Point(50, 360));
		new GUIWire(submodelModifiable, cpD101, cell01.getPin("D1"), new Point[0]);
		new GUIWire(submodelModifiable, cpD201, cell01.getPin("D2"), new Point[0]);
		new GUIWire(submodelModifiable, cpD301, cell01.getPin("D3"), new Point[0]);
		new GUIWire(submodelModifiable, cpD401, cell01.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, cpD101, cpD1in             , new Point[0]);
		new GUIWire(submodelModifiable, cpD1in, cpD110             , new Point[0]);
		new GUIWire(submodelModifiable, cpD201, cpD210             , new Point[0]);
		new GUIWire(submodelModifiable, cpD301, cpD310             , new Point[0]);
		new GUIWire(submodelModifiable, cpD401, cpD410             , new Point[0]);
		new GUIWire(submodelModifiable, cpD110, cell10.getPin("D1"), new Point[0]);
		new GUIWire(submodelModifiable, cpD210, cell10.getPin("D2"), new Point[0]);
		new GUIWire(submodelModifiable, cpD310, cell10.getPin("D3"), new Point[0]);
		new GUIWire(submodelModifiable, cpD410, cell10.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, cpD210, cpD2in             , new Point[0]);
		new GUIWire(submodelModifiable, cpD310, cpD3in             , new Point[0]);
		new GUIWire(submodelModifiable, cpD410, cpD4in             , new Point[0]);
		new GUIWire(submodelModifiable, cpD110, cell11.getPin("D1"), new Point(35, 780));
		new GUIWire(submodelModifiable, cpD2in, cell11.getPin("D2"), new Point(40, 790));
		new GUIWire(submodelModifiable, cpD3in, cell11.getPin("D3"), new Point(45, 800));
		new GUIWire(submodelModifiable, cpD4in, cell11.getPin("D4"), new Point[0]);
		new GUIWire(submodelModifiable, cell00.getPin("Q1"), cpQ100, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getPin("Q2"), cpQ200, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getPin("Q3"), cpQ300, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getPin("Q4"), cpQ400, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getPin("Q1"), cpQ101, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getPin("Q2"), cpQ201, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getPin("Q3"), cpQ301, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getPin("Q4"), cpQ401, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getPin("Q1"), cpQ110, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getPin("Q2"), cpQ210, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getPin("Q3"), cpQ310, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getPin("Q4"), cpQ410, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getPin("Q1"), cpQ111, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getPin("Q2"), cpQ211, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getPin("Q3"), cpQ311, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getPin("Q4"), cpQ411, new Point[0]);
		new GUIWire(submodelModifiable, demuxA.getPin("Y00"), andA00  .getPin("B"), new Point(210, 50), new Point(210, 420));
		new GUIWire(submodelModifiable, demuxA.getPin("Y01"), andorA01.getPin("B"), new Point(205, 60), new Point(205, 570));
		new GUIWire(submodelModifiable, demuxA.getPin("Y10"), andorA10.getPin("B"), new Point(200, 70), new Point(200, 720));
		new GUIWire(submodelModifiable, demuxA.getPin("Y11"), andorA11.getPin("B"), new Point(195, 80), new Point(195, 870));
		new GUIWire(submodelModifiable, cpB00 , andB00  .getPin("B"), new Point(110, 370));
		new GUIWire(submodelModifiable, cpB01 , andorB01.getPin("B"), new Point(105, 520));
		new GUIWire(submodelModifiable, cpB10 , andorB10.getPin("B"), new Point(100, 670));
		new GUIWire(submodelModifiable, cpB11 , andorB11.getPin("B"), new Point(95, 820));
		new GUIWire(submodelModifiable, cpQ100, andA00  .getPin("A1"), new Point(130, 380));
		new GUIWire(submodelModifiable, cpQ200, andA00  .getPin("A2"), new Point(125, 390));
		new GUIWire(submodelModifiable, cpQ300, andA00  .getPin("A3"), new Point(120, 400));
		new GUIWire(submodelModifiable, cpQ400, andA00  .getPin("A4"), new Point(115, 410));
		new GUIWire(submodelModifiable, cpQ101, andorA01.getPin("A1"), new Point(130, 530));
		new GUIWire(submodelModifiable, cpQ201, andorA01.getPin("A2"), new Point(125, 540));
		new GUIWire(submodelModifiable, cpQ301, andorA01.getPin("A3"), new Point(120, 550));
		new GUIWire(submodelModifiable, cpQ401, andorA01.getPin("A4"), new Point(115, 560));
		new GUIWire(submodelModifiable, cpQ110, andorA10.getPin("A1"), new Point(130, 680));
		new GUIWire(submodelModifiable, cpQ210, andorA10.getPin("A2"), new Point(125, 690));
		new GUIWire(submodelModifiable, cpQ310, andorA10.getPin("A3"), new Point(120, 700));
		new GUIWire(submodelModifiable, cpQ410, andorA10.getPin("A4"), new Point(115, 710));
		new GUIWire(submodelModifiable, cpQ111, andorA11.getPin("A1"), new Point(130, 830));
		new GUIWire(submodelModifiable, cpQ211, andorA11.getPin("A2"), new Point(125, 840));
		new GUIWire(submodelModifiable, cpQ311, andorA11.getPin("A3"), new Point(120, 850));
		new GUIWire(submodelModifiable, cpQ411, andorA11.getPin("A4"), new Point(115, 860));
		new GUIWire(submodelModifiable, cpQ100, andB00  .getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ200, andB00  .getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ300, andB00  .getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ400, andB00  .getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ101, andorB01.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ201, andorB01.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ301, andorB01.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ401, andorB01.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ110, andorB10.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ210, andorB10.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ310, andorB10.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ410, andorB10.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ111, andorB11.getPin("A1"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ211, andorB11.getPin("A2"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ311, andorB11.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, cpQ411, andorB11.getPin("A4"), new Point[0]);
		new GUIWire(submodelModifiable, andA00  .getPin("Y1"), andorA01.getPin("C1"), new Point(290, 380), new Point(290, 445), new Point(230, 445), new Point(230, 490));
		new GUIWire(submodelModifiable, andA00  .getPin("Y2"), andorA01.getPin("C2"), new Point(285, 390), new Point(285, 440), new Point(225, 440), new Point(225, 500));
		new GUIWire(submodelModifiable, andA00  .getPin("Y3"), andorA01.getPin("C3"), new Point(280, 400), new Point(280, 435), new Point(220, 435), new Point(220, 510));
		new GUIWire(submodelModifiable, andA00  .getPin("Y4"), andorA01.getPin("C4"), new Point(275, 410), new Point(275, 430), new Point(215, 430), new Point(215, 520));
		new GUIWire(submodelModifiable, andorA01.getPin("Y1"), andorA10.getPin("C1"), new Point(290, 490), new Point(290, 595), new Point(230, 595), new Point(230, 640));
		new GUIWire(submodelModifiable, andorA01.getPin("Y2"), andorA10.getPin("C2"), new Point(285, 500), new Point(285, 590), new Point(225, 590), new Point(225, 650));
		new GUIWire(submodelModifiable, andorA01.getPin("Y3"), andorA10.getPin("C3"), new Point(280, 510), new Point(280, 585), new Point(220, 585), new Point(220, 660));
		new GUIWire(submodelModifiable, andorA01.getPin("Y4"), andorA10.getPin("C4"), new Point(275, 520), new Point(275, 580), new Point(215, 580), new Point(215, 670));
		new GUIWire(submodelModifiable, andorA10.getPin("Y1"), andorA11.getPin("C1"), new Point(290, 640), new Point(290, 745), new Point(230, 745), new Point(230, 790));
		new GUIWire(submodelModifiable, andorA10.getPin("Y2"), andorA11.getPin("C2"), new Point(285, 650), new Point(285, 740), new Point(225, 740), new Point(225, 800));
		new GUIWire(submodelModifiable, andorA10.getPin("Y3"), andorA11.getPin("C3"), new Point(280, 660), new Point(280, 735), new Point(220, 735), new Point(220, 810));
		new GUIWire(submodelModifiable, andorA10.getPin("Y4"), andorA11.getPin("C4"), new Point(275, 670), new Point(275, 730), new Point(215, 730), new Point(215, 820));
		new GUIWire(submodelModifiable, andorA11.getPin("Y1"), QA1                           , new Point(300, 790), new Point(300,  50));
		new GUIWire(submodelModifiable, andorA11.getPin("Y2"), QA2                           , new Point(305, 800), new Point(305, 150));
		new GUIWire(submodelModifiable, andorA11.getPin("Y3"), QA3                           , new Point(310, 810), new Point(310, 250));
		new GUIWire(submodelModifiable, andorA11.getPin("Y4"), QA4                           , new Point(315, 820), new Point(315, 350));
		new GUIWire(submodelModifiable, andB00  .getPin("Y1"), andorB01.getPin("C1"), new Point(190, 330), new Point(190, 430), new Point(130, 430), new Point(130, 440));
		new GUIWire(submodelModifiable, andB00  .getPin("Y2"), andorB01.getPin("C2"), new Point(185, 340), new Point(185, 425), new Point(125, 425), new Point(125, 450));
		new GUIWire(submodelModifiable, andB00  .getPin("Y3"), andorB01.getPin("C3"), new Point(180, 350), new Point(180, 420), new Point(120, 420), new Point(120, 460));
		new GUIWire(submodelModifiable, andB00  .getPin("Y4"), andorB01.getPin("C4"), new Point(175, 360), new Point(175, 415), new Point(115, 415), new Point(115, 470));
		new GUIWire(submodelModifiable, andorB01.getPin("Y1"), andorB10.getPin("C1"), new Point(190, 440), new Point(190, 580), new Point(130, 580), new Point(130, 590));
		new GUIWire(submodelModifiable, andorB01.getPin("Y2"), andorB10.getPin("C2"), new Point(185, 450), new Point(185, 575), new Point(125, 575), new Point(125, 600));
		new GUIWire(submodelModifiable, andorB01.getPin("Y3"), andorB10.getPin("C3"), new Point(180, 460), new Point(180, 570), new Point(120, 570), new Point(120, 610));
		new GUIWire(submodelModifiable, andorB01.getPin("Y4"), andorB10.getPin("C4"), new Point(175, 470), new Point(175, 565), new Point(115, 565), new Point(115, 620));
		new GUIWire(submodelModifiable, andorB10.getPin("Y1"), andorB11.getPin("C1"), new Point(190, 590), new Point(190, 730), new Point(130, 730), new Point(130, 740));
		new GUIWire(submodelModifiable, andorB10.getPin("Y2"), andorB11.getPin("C2"), new Point(185, 600), new Point(185, 725), new Point(125, 725), new Point(125, 750));
		new GUIWire(submodelModifiable, andorB10.getPin("Y3"), andorB11.getPin("C3"), new Point(180, 610), new Point(180, 720), new Point(120, 720), new Point(120, 760));
		new GUIWire(submodelModifiable, andorB10.getPin("Y4"), andorB11.getPin("C4"), new Point(175, 620), new Point(175, 715), new Point(115, 715), new Point(115, 770));
		new GUIWire(submodelModifiable, andorB11.getPin("Y1"), QB1                           , new Point(190, 740), new Point(190, 880), new Point(325, 880), new Point(325, 450));
		new GUIWire(submodelModifiable, andorB11.getPin("Y2"), QB2                           , new Point(185, 750), new Point(185, 885), new Point(330, 885), new Point(330, 550));
		new GUIWire(submodelModifiable, andorB11.getPin("Y3"), QB3                           , new Point(180, 760), new Point(180, 890), new Point(335, 890), new Point(335, 650));
		new GUIWire(submodelModifiable, andorB11.getPin("Y4"), QB4                           , new Point(175, 770), new Point(175, 895), new Point(340, 895), new Point(340, 750));
		//@formatter:on

		addHighLevelStateSubcomponentID("c00", cell00);
		addHighLevelStateSubcomponentID("c01", cell01);
		addHighLevelStateSubcomponentID("c10", cell10);
		addHighLevelStateSubcomponentID("c11", cell11);
		addAtomicHighLevelStateID("q");
	}

	@Override
	public void setAtomicHighLevelState(String stateID, Object newState)
	{
		switch (stateID)
		{
		case "q":
			BitVector newStateCasted = (BitVector) newState;
			setHighLevelState("c00.q", newStateCasted.subVector(0, 4));
			setHighLevelState("c01.q", newStateCasted.subVector(4, 8));
			setHighLevelState("c10.q", newStateCasted.subVector(8, 12));
			setHighLevelState("c11.q", newStateCasted.subVector(12, 16));
			break;
		default:
			// should not happen because we tell SubmodelComponent to only allow these state IDs.
			throw new IllegalStateException("Illegal atomic state ID: " + stateID);
		}
	}

	@Override
	public Object getAtomicHighLevelState(String stateID)
	{
		switch (stateID)
		{
		case "q":
			BitVector q00 = (BitVector) getHighLevelState("c00.q");
			BitVector q01 = (BitVector) getHighLevelState("c01.q");
			BitVector q10 = (BitVector) getHighLevelState("c10.q");
			BitVector q11 = (BitVector) getHighLevelState("c11.q");
			return q00.concat(q01).concat(q10).concat(q11);
		default:
			// should not happen because we tell SubmodelComponent to only allow these state IDs.
			throw new IllegalStateException("Illegal atomic state ID: " + stateID);
		}
	}

	static
	{
		IndirectGUIComponentCreator.setComponentSupplier(GUIram2.class.getCanonicalName(), (m, p, n) -> new GUIram2(m, n));
	}
}