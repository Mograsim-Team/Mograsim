package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUIram2 extends SimpleRectangularSubmodelComponent
{
	public GUIram2(ViewModelModifiable model)
	{
		super(model, 1, "GUIram2");
		setSubmodelScale(.1);
		setInputCount(9);
		setOutputCount(8);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin A0 = getInputSubmodelPins().get(0);
		Pin A1 = getInputSubmodelPins().get(1);
		Pin B0 = getInputSubmodelPins().get(2);
		Pin B1 = getInputSubmodelPins().get(3);
		Pin WE = getInputSubmodelPins().get(4);
		Pin D1 = getInputSubmodelPins().get(5);
		Pin D2 = getInputSubmodelPins().get(6);
		Pin D3 = getInputSubmodelPins().get(7);
		Pin D4 = getInputSubmodelPins().get(8);
		Pin QA1 = getOutputSubmodelPins().get(0);
		Pin QA2 = getOutputSubmodelPins().get(1);
		Pin QA3 = getOutputSubmodelPins().get(2);
		Pin QA4 = getOutputSubmodelPins().get(3);
		Pin QB1 = getOutputSubmodelPins().get(4);
		Pin QB2 = getOutputSubmodelPins().get(5);
		Pin QB3 = getOutputSubmodelPins().get(6);
		Pin QB4 = getOutputSubmodelPins().get(7);

		GUIdemux2 demuxA = new GUIdemux2(submodelModifiable);
		GUIdemux2 demuxB = new GUIdemux2(submodelModifiable);
		GUIand41 weAndB = new GUIand41(submodelModifiable);
		GUIdlatch4 cell00 = new GUIdlatch4(submodelModifiable);
		GUIdlatch4 cell01 = new GUIdlatch4(submodelModifiable);
		GUIdlatch4 cell10 = new GUIdlatch4(submodelModifiable);
		GUIdlatch4 cell11 = new GUIdlatch4(submodelModifiable);
		GUIand41 andA00 = new GUIand41(submodelModifiable);
		GUIandor414 andorA01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorA11 = new GUIandor414(submodelModifiable);
		GUIand41 andB00 = new GUIand41(submodelModifiable);
		GUIandor414 andorB01 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB10 = new GUIandor414(submodelModifiable);
		GUIandor414 andorB11 = new GUIandor414(submodelModifiable);

		WireCrossPoint cpB00 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB01 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB10 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpB11 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD100 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD200 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD300 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cpD400 = new WireCrossPoint(submodelModifiable, 1);
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

		new GUIWire(submodelModifiable, A0, demuxA.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, A1, demuxA.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, B0, demuxB.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, B1, demuxB.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(0), cpB00, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(1), cpB01, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(2), cpB10, new Point[0]);
		new GUIWire(submodelModifiable, demuxB.getOutputPins().get(3), cpB11, new Point[0]);
		new GUIWire(submodelModifiable, cpB00, weAndB.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpB01, weAndB.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpB10, weAndB.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpB11, weAndB.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, WE, weAndB.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(0), cell00.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(1), cell01.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(2), cell10.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, weAndB.getOutputPins().get(3), cell11.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, D1, cpD100, new Point[0]);
		new GUIWire(submodelModifiable, D2, cpD200, new Point[0]);
		new GUIWire(submodelModifiable, D3, cpD300, new Point[0]);
		new GUIWire(submodelModifiable, D4, cpD400, new Point[0]);
		new GUIWire(submodelModifiable, cpD100, cell00.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpD200, cell00.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpD300, cell00.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpD400, cell00.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpD100, cpD101, new Point[0]);
		new GUIWire(submodelModifiable, cpD200, cpD201, new Point[0]);
		new GUIWire(submodelModifiable, cpD300, cpD301, new Point[0]);
		new GUIWire(submodelModifiable, cpD400, cpD401, new Point[0]);
		new GUIWire(submodelModifiable, cpD101, cell01.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpD201, cell01.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpD301, cell01.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpD401, cell01.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpD101, cpD110, new Point[0]);
		new GUIWire(submodelModifiable, cpD201, cpD210, new Point[0]);
		new GUIWire(submodelModifiable, cpD301, cpD310, new Point[0]);
		new GUIWire(submodelModifiable, cpD401, cpD410, new Point[0]);
		new GUIWire(submodelModifiable, cpD110, cell10.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpD210, cell10.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpD310, cell10.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpD410, cell10.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpD110, cell11.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpD210, cell11.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpD310, cell11.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpD410, cell11.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(0), cpQ100, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(1), cpQ200, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(2), cpQ300, new Point[0]);
		new GUIWire(submodelModifiable, cell00.getOutputPins().get(3), cpQ400, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(0), cpQ101, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(1), cpQ201, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(2), cpQ301, new Point[0]);
		new GUIWire(submodelModifiable, cell01.getOutputPins().get(3), cpQ401, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(0), cpQ110, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(1), cpQ210, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(2), cpQ310, new Point[0]);
		new GUIWire(submodelModifiable, cell10.getOutputPins().get(3), cpQ410, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(0), cpQ111, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(1), cpQ211, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(2), cpQ311, new Point[0]);
		new GUIWire(submodelModifiable, cell11.getOutputPins().get(3), cpQ411, new Point[0]);
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(0), andA00.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(1), andorA01.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(2), andorA10.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, demuxA.getOutputPins().get(3), andorA11.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpB00, andB00.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpB01, andorB01.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpB10, andorB10.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpB11, andorB11.getInputPins().get(8), new Point[0]);
		new GUIWire(submodelModifiable, cpQ100, andA00.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpQ200, andA00.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpQ300, andA00.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpQ400, andA00.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpQ101, andorA01.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ201, andorA01.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ301, andorA01.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ401, andorA01.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpQ110, andorA10.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ210, andorA10.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ310, andorA10.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ410, andorA10.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpQ111, andorA11.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ211, andorA11.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ311, andorA11.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ411, andorA11.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpQ100, andB00.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, cpQ200, andB00.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, cpQ300, andB00.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, cpQ400, andB00.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, cpQ101, andorB01.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ201, andorB01.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ301, andorB01.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ401, andorB01.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpQ110, andorB10.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ210, andorB10.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ310, andorB10.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ410, andorB10.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, cpQ111, andorB11.getInputPins().get(4), new Point[0]);
		new GUIWire(submodelModifiable, cpQ211, andorB11.getInputPins().get(5), new Point[0]);
		new GUIWire(submodelModifiable, cpQ311, andorB11.getInputPins().get(6), new Point[0]);
		new GUIWire(submodelModifiable, cpQ411, andorB11.getInputPins().get(7), new Point[0]);
		new GUIWire(submodelModifiable, andA00.getOutputPins().get(0), andorA01.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andA00.getOutputPins().get(1), andorA01.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andA00.getOutputPins().get(2), andorA01.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andA00.getOutputPins().get(3), andorA01.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(0), andorA10.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(1), andorA10.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(2), andorA10.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andorA01.getOutputPins().get(3), andorA10.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(0), andorA11.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(1), andorA11.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(2), andorA11.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andorA10.getOutputPins().get(3), andorA11.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(0), QA1, new Point[0]);
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(1), QA2, new Point[0]);
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(2), QA3, new Point[0]);
		new GUIWire(submodelModifiable, andorA11.getOutputPins().get(3), QA4, new Point[0]);
		new GUIWire(submodelModifiable, andB00.getOutputPins().get(0), andorB01.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andB00.getOutputPins().get(1), andorB01.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andB00.getOutputPins().get(2), andorB01.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andB00.getOutputPins().get(3), andorB01.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(0), andorB10.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(1), andorB10.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(2), andorB10.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andorB01.getOutputPins().get(3), andorB10.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(0), andorB11.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(1), andorB11.getInputPins().get(1), new Point[0]);
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(2), andorB11.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, andorB10.getOutputPins().get(3), andorB11.getInputPins().get(3), new Point[0]);
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(0), QB1, new Point[0]);
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(1), QB2, new Point[0]);
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(2), QB3, new Point[0]);
		new GUIWire(submodelModifiable, andorB11.getOutputPins().get(3), QB4, new Point[0]);
	}
}