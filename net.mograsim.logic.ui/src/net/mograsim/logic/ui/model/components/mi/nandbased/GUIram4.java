package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIram4 extends SimpleRectangularSubmodelComponent
{
	public GUIram4(ViewModelModifiable model)
	{
		super(model, 1, "GUIram4");
		setSubmodelScale(.1);
		setInputCount(13);
		setOutputCount(8);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		//@formatter:off
		Pin  A0 = getInputSubmodelPins() .get( 0);
		Pin  A1 = getInputSubmodelPins() .get( 1);
		Pin  A2 = getInputSubmodelPins() .get( 2);
		Pin  A3 = getInputSubmodelPins() .get( 3);
		Pin  B0 = getInputSubmodelPins() .get( 4);
		Pin  B1 = getInputSubmodelPins() .get( 5);
		Pin  B2 = getInputSubmodelPins() .get( 6);
		Pin  B3 = getInputSubmodelPins() .get( 7);
		Pin  WE = getInputSubmodelPins() .get( 8);
		Pin  D1 = getInputSubmodelPins() .get( 9);
		Pin  D2 = getInputSubmodelPins() .get(10);
		Pin  D3 = getInputSubmodelPins() .get(11);
		Pin  D4 = getInputSubmodelPins() .get(12);
		Pin QA1 = getOutputSubmodelPins().get( 0);
		Pin QA2 = getOutputSubmodelPins().get( 1);
		Pin QA3 = getOutputSubmodelPins().get( 2);
		Pin QA4 = getOutputSubmodelPins().get( 3);
		Pin QB1 = getOutputSubmodelPins().get( 4);
		Pin QB2 = getOutputSubmodelPins().get( 5);
		Pin QB3 = getOutputSubmodelPins().get( 6);
		Pin QB4 = getOutputSubmodelPins().get( 7);

		GUIdemux2   demuxA   = new GUIdemux2  (submodelModifiable);
		GUIdemux2   demuxB   = new GUIdemux2  (submodelModifiable);
		GUIand41    weAndB   = new GUIand41   (submodelModifiable);
		GUIram2     cell00   = new GUIram2    (submodelModifiable);
		GUIram2     cell01   = new GUIram2    (submodelModifiable);
		GUIram2     cell10   = new GUIram2    (submodelModifiable);
		GUIram2     cell11   = new GUIram2    (submodelModifiable);
		GUIand41    andB00   = new GUIand41   (submodelModifiable);
		GUIandor414 andorB01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB11 = new GUIandor414(submodelModifiable);
		GUIand41    andA00   = new GUIand41   (submodelModifiable);
		GUIandor414 andorA01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA11 = new GUIandor414(submodelModifiable);

		WireCrossPoint cpB00  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB01  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB10  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB11  = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_101 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_201 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_301 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_401 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_110 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_210 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_310 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_410 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_111 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_211 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_311 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD_411 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB101 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB201 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB301 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB401 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB110 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB210 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB310 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB410 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB1in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB2in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB3in = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpAB4in = new WireCrossPoint(submodelModifiable, 1);

		demuxA  .moveTo( 55,  45);
		demuxB  .moveTo( 55, 150);
		weAndB  .moveTo(235, 150);
		cell00  .moveTo( 80, 330);
		cell01  .moveTo( 80, 480);
		cell10  .moveTo( 80, 630);
		cell11  .moveTo( 80, 780);
		andB00  .moveTo(250, 375);
		andorB01.moveTo(250, 485);
		andorB10.moveTo(250, 635);
		andorB11.moveTo(250, 785);
		andA00  .moveTo(155, 325);
		andorA01.moveTo(155, 435);
		andorA10.moveTo(155, 585);
		andorA11.moveTo(155, 735);
		cpB00 .moveCenterTo(230, 155);
		cpB01 .moveCenterTo(225, 165);
		cpB10 .moveCenterTo(220, 175);
		cpB11 .moveCenterTo(215, 185);
		cpD_101.moveCenterTo( 60, 535);
		cpD_201.moveCenterTo( 65, 545);
		cpD_301.moveCenterTo( 70, 555);
		cpD_401.moveCenterTo( 75, 565);
		cpD_110.moveCenterTo( 60, 685);
		cpD_210.moveCenterTo( 65, 695);
		cpD_310.moveCenterTo( 70, 705);
		cpD_410.moveCenterTo( 75, 715);
		cpD_111.moveCenterTo( 60, 835);
		cpD_211.moveCenterTo( 65, 845);
		cpD_311.moveCenterTo( 70, 855);
		cpD_411.moveCenterTo( 75, 865);
		cpAB101.moveCenterTo( 40, 485);
		cpAB201.moveCenterTo( 45, 495);
		cpAB301.moveCenterTo( 50, 505);
		cpAB401.moveCenterTo( 55, 515);
		cpAB110.moveCenterTo( 40, 635);
		cpAB210.moveCenterTo( 45, 645);
		cpAB310.moveCenterTo( 50, 655);
		cpAB410.moveCenterTo( 55, 665);
		cpAB1in.moveCenterTo( 40, 335);
		cpAB2in.moveCenterTo( 45, 350);
		cpAB3in.moveCenterTo( 50, 650);
		cpAB4in.moveCenterTo( 55, 750);

		new GUIWire(submodelModifiable, A0, demuxA.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, A1, demuxA.getInputPins().get(1), new Point(10, 150), new Point(10,  60));
		new GUIWire(submodelModifiable, B0, demuxB.getInputPins().get(0), new Point( 5, 450), new Point( 5, 155));
		new GUIWire(submodelModifiable, B1, demuxB.getInputPins().get(1), new Point(10, 550), new Point(10, 165));
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(0), cpB00, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(1), cpB01, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(2), cpB10, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(3), cpB11, new Point[0]);
		new GUIWire(submodelModifiable, cpB00, weAndB.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpB01, weAndB.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpB10, weAndB.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpB11, weAndB.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, WE, weAndB.getInputPins().get(4), new Point(15, 850), new Point(15, 195));
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(0), cell00.getInputPins().get(4), new Point(290, 155), new Point(290, 230), new Point(35, 230), new Point(35, 375));
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(1), cell01.getInputPins().get(4), new Point(285, 165), new Point(285, 225), new Point(30, 225), new Point(30, 525));
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(2), cell10.getInputPins().get(4), new Point(280, 175), new Point(280, 220), new Point(25, 220), new Point(25, 675));
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(3), cell11.getInputPins().get(4), new Point(275, 185), new Point(275, 215), new Point(20, 215), new Point(20, 825));
		new GUIWire(submodelModifiable, cpAB101, cpAB1in                     , new Point[0]);
		new GUIWire(submodelModifiable, cpAB1in, cell00.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpAB201, cpAB2in                     , new Point[0]);
		new GUIWire(submodelModifiable, cpAB2in, cell00.getInputPins().get(1), new Point(45, 345));
		new GUIWire(submodelModifiable, cpAB301, cell00.getInputPins().get(2), new Point(50, 355));
		new GUIWire(submodelModifiable, cpAB401, cell00.getInputPins().get(3), new Point(55, 365));
		new GUIWire(submodelModifiable, cpAB101, cell01.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpAB201, cell01.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpAB301, cell01.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpAB401, cell01.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpAB101, cpAB110                      , new Point[0]);
		new GUIWire(submodelModifiable, cpAB201, cpAB210                      , new Point[0]);
		new GUIWire(submodelModifiable, cpAB301, cpAB3in                      , new Point[0]);
		new GUIWire(submodelModifiable, B2, cpAB3in                          , new Point[0]);
		new GUIWire(submodelModifiable, cpAB3in, cpAB310                      , new Point[0]);
		new GUIWire(submodelModifiable, cpAB401, cpAB410                      , new Point[0]);
		new GUIWire(submodelModifiable, cpAB110, cell10.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpAB210, cell10.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpAB310, cell10.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpAB410, cell10.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpAB110, cell11.getInputPins().get(0), new Point(40, 785));
		new GUIWire(submodelModifiable, cpAB210, cell11.getInputPins().get(1), new Point(45, 795));
		new GUIWire(submodelModifiable, cpAB310, cell11.getInputPins().get(2), new Point(50, 805));
		new GUIWire(submodelModifiable, cpAB410, cpAB4in                     , new Point[0]);
		new GUIWire(submodelModifiable, cpAB4in, cell11.getInputPins().get(3), new Point(55, 815));
		new GUIWire(submodelModifiable, A2, cpAB1in                          , new Point(40, 250));
		new GUIWire(submodelModifiable, A3, cpAB2in                          , new Point[0]);
		new GUIWire(submodelModifiable, B3, cpAB4in                          , new Point[0]);
		new GUIWire(submodelModifiable, cpD_101, cell00.getInputPins().get(5), new Point(60, 385));
		new GUIWire(submodelModifiable, cpD_201, cell00.getInputPins().get(6), new Point(65, 395));
		new GUIWire(submodelModifiable, cpD_301, cell00.getInputPins().get(7), new Point(70, 405));
		new GUIWire(submodelModifiable, cpD_401, cell00.getInputPins().get(8), new Point(75, 415));
		new GUIWire(submodelModifiable, cpD_101, cell01.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpD_201, cell01.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpD_301, cell01.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpD_401, cell01.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpD_101, cpD_110                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_201, cpD_210                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_301, cpD_310                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_401, cpD_410                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_110, cell10.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpD_210, cell10.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpD_310, cell10.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpD_410, cell10.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpD_110, cpD_111                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_210, cpD_211                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_310, cpD_311                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_410, cpD_411                      , new Point[0]);
		new GUIWire(submodelModifiable, cpD_111, cell11.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpD_211, cell11.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpD_311, cell11.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpD_411, cell11.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, D1, cpD_111                          , new Point(60,  950));
		new GUIWire(submodelModifiable, D2, cpD_211                          , new Point(65, 1050));
		new GUIWire(submodelModifiable, D3, cpD_311                          , new Point(70, 1150));
		new GUIWire(submodelModifiable, D4, cpD_411                          , new Point(75, 1250));
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(0), andA00  .getInputPins().get(4), new Point(135, 50), new Point(135, 370));
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(1), andorA01.getInputPins().get(8), new Point(130, 60), new Point(130, 520));
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(2), andorA10.getInputPins().get(8), new Point(125, 70), new Point(125, 670));
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(3), andorA11.getInputPins().get(8), new Point(120, 80), new Point(120, 820));
		new GUIWire(submodelModifiable, cpB00 , andB00  .getInputPins().get(4), new Point(230, 420));
		new GUIWire(submodelModifiable, cpB01 , andorB01.getInputPins().get(8), new Point(225, 570));
		new GUIWire(submodelModifiable, cpB10 , andorB10.getInputPins().get(8), new Point(220, 720));
		new GUIWire(submodelModifiable, cpB11 , andorB11.getInputPins().get(8), new Point(215, 870));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(4), andB00  .getInputPins().get(0), new Point(140, 375), new Point(140, 380));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(5), andB00  .getInputPins().get(1), new Point(140, 385), new Point(140, 390));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(6), andB00  .getInputPins().get(2), new Point(140, 395), new Point(140, 400));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(7), andB00  .getInputPins().get(3), new Point(140, 405), new Point(140, 410));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(4), andorB01.getInputPins().get(4), new Point(140, 525), new Point(140, 530));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(5), andorB01.getInputPins().get(5), new Point(140, 535), new Point(140, 540));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(6), andorB01.getInputPins().get(6), new Point(140, 545), new Point(140, 550));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(7), andorB01.getInputPins().get(7), new Point(140, 555), new Point(140, 560));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(4), andorB10.getInputPins().get(4), new Point(140, 675), new Point(140, 680));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(5), andorB10.getInputPins().get(5), new Point(140, 685), new Point(140, 690));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(6), andorB10.getInputPins().get(6), new Point(140, 695), new Point(140, 700));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(7), andorB10.getInputPins().get(7), new Point(140, 705), new Point(140, 710));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(4), andorB11.getInputPins().get(4), new Point(140, 825), new Point(140, 830));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(5), andorB11.getInputPins().get(5), new Point(140, 835), new Point(140, 840));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(6), andorB11.getInputPins().get(6), new Point(140, 845), new Point(140, 850));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(7), andorB11.getInputPins().get(7), new Point(140, 855), new Point(140, 860));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(0), andA00  .getInputPins().get(0), new Point(140, 335), new Point(140, 330));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(1), andA00  .getInputPins().get(1), new Point(140, 345), new Point(140, 340));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(2), andA00  .getInputPins().get(2), new Point(140, 355), new Point(140, 350));
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(3), andA00  .getInputPins().get(3), new Point(140, 365), new Point(140, 360));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(0), andorA01.getInputPins().get(4), new Point(140, 485), new Point(140, 480));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(1), andorA01.getInputPins().get(5), new Point(140, 495), new Point(140, 490));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(2), andorA01.getInputPins().get(6), new Point(140, 505), new Point(140, 500));
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(3), andorA01.getInputPins().get(7), new Point(140, 515), new Point(140, 510));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(0), andorA10.getInputPins().get(4), new Point(140, 635), new Point(140, 630));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(1), andorA10.getInputPins().get(5), new Point(140, 645), new Point(140, 640));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(2), andorA10.getInputPins().get(6), new Point(140, 655), new Point(140, 650));
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(3), andorA10.getInputPins().get(7), new Point(140, 665), new Point(140, 660));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(0), andorA11.getInputPins().get(4), new Point(140, 785), new Point(140, 780));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(1), andorA11.getInputPins().get(5), new Point(140, 795), new Point(140, 790));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(2), andorA11.getInputPins().get(6), new Point(140, 805), new Point(140, 800));
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(3), andorA11.getInputPins().get(7), new Point(140, 815), new Point(140, 810));
		new GUIWire(submodelModifiable, andB00  .getOutputPins().get(0), andorB01.getInputPins().get(0), new Point(305, 380), new Point(305, 445), new Point(245, 445), new Point(245, 490));
		new GUIWire(submodelModifiable, andB00  .getOutputPins().get(1), andorB01.getInputPins().get(1), new Point(300, 390), new Point(300, 440), new Point(240, 440), new Point(240, 500));
		new GUIWire(submodelModifiable, andB00  .getOutputPins().get(2), andorB01.getInputPins().get(2), new Point(295, 400), new Point(295, 435), new Point(235, 435), new Point(235, 510));
		new GUIWire(submodelModifiable, andB00  .getOutputPins().get(3), andorB01.getInputPins().get(3), new Point(290, 410), new Point(290, 430), new Point(230, 430), new Point(230, 520));
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(0), andorB10.getInputPins().get(0), new Point(305, 490), new Point(305, 595), new Point(245, 595), new Point(245, 640));
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(1), andorB10.getInputPins().get(1), new Point(300, 500), new Point(300, 590), new Point(240, 590), new Point(240, 650));
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(2), andorB10.getInputPins().get(2), new Point(295, 510), new Point(295, 585), new Point(235, 585), new Point(235, 660));
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(3), andorB10.getInputPins().get(3), new Point(290, 520), new Point(290, 580), new Point(230, 580), new Point(230, 670));
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(0), andorB11.getInputPins().get(0), new Point(305, 640), new Point(305, 745), new Point(245, 745), new Point(245, 790));
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(1), andorB11.getInputPins().get(1), new Point(300, 650), new Point(300, 740), new Point(240, 740), new Point(240, 800));
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(2), andorB11.getInputPins().get(2), new Point(295, 660), new Point(295, 735), new Point(235, 735), new Point(235, 810));
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(3), andorB11.getInputPins().get(3), new Point(290, 670), new Point(290, 730), new Point(230, 730), new Point(230, 820));
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(0), QB1                           , new Point(330, 790), new Point(330, 450));
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(1), QB2                           , new Point(335, 800), new Point(335, 550));
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(2), QB3                           , new Point(340, 810), new Point(340, 650));
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(3), QB4                           , new Point(345, 820), new Point(345, 750));
		new GUIWire(submodelModifiable, andA00  .getOutputPins().get(0), andorA01.getInputPins().get(0), new Point(210, 330), new Point(210, 430), new Point(150, 430), new Point(150, 440));
		new GUIWire(submodelModifiable, andA00  .getOutputPins().get(1), andorA01.getInputPins().get(1), new Point(205, 340), new Point(205, 425), new Point(145, 425), new Point(145, 450));
		new GUIWire(submodelModifiable, andA00  .getOutputPins().get(2), andorA01.getInputPins().get(2), new Point(200, 350), new Point(200, 420), new Point(140, 420), new Point(140, 460));
		new GUIWire(submodelModifiable, andA00  .getOutputPins().get(3), andorA01.getInputPins().get(3), new Point(195, 360), new Point(195, 415), new Point(135, 415), new Point(135, 470));
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(0), andorA10.getInputPins().get(0), new Point(210, 440), new Point(210, 580), new Point(150, 580), new Point(150, 590));
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(1), andorA10.getInputPins().get(1), new Point(205, 450), new Point(205, 575), new Point(145, 575), new Point(145, 600));
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(2), andorA10.getInputPins().get(2), new Point(200, 460), new Point(200, 570), new Point(140, 570), new Point(140, 610));
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(3), andorA10.getInputPins().get(3), new Point(195, 470), new Point(195, 565), new Point(135, 565), new Point(135, 620));
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(0), andorA11.getInputPins().get(0), new Point(210, 590), new Point(210, 730), new Point(150, 730), new Point(150, 740));
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(1), andorA11.getInputPins().get(1), new Point(205, 600), new Point(205, 725), new Point(145, 725), new Point(145, 750));
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(2), andorA11.getInputPins().get(2), new Point(200, 610), new Point(200, 720), new Point(140, 720), new Point(140, 760));
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(3), andorA11.getInputPins().get(3), new Point(195, 620), new Point(195, 715), new Point(135, 715), new Point(135, 770));
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(0), QA1                           , new Point(210, 740), new Point(210, 880), new Point(310, 880), new Point(310,  50));
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(1), QA2                           , new Point(205, 750), new Point(205, 885), new Point(315, 885), new Point(315, 150));
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(2), QA3                           , new Point(200, 760), new Point(200, 890), new Point(320, 890), new Point(320, 250));
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(3), QA4                           , new Point(195, 770), new Point(195, 895), new Point(325, 895), new Point(325, 350));
		//@formatter:on
	}
}