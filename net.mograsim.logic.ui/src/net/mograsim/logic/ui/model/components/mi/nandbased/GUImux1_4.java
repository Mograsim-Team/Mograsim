package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

/**
 * @formatter:off
 * Inputs:
 * 0: select
 * 1: I0_0
 * 2: I0_1
 * 3: I0_2
 * 4: I0_3
 * 5: I1_0
 * 6: I1_1
 * 7: I1_2
 * 8: I1_3
 * Outputs:
 * 0: Y0
 * 1: Y1
 * 2: Y2
 * 3: Y3
 * @formatter:on
 */
public class GUImux1_4 extends SimpleRectangularSubmodelComponent
{
	public GUImux1_4(ViewModelModifiable model)
	{
		super(model, 1, "GUImux1_4");
		setSubmodelScale(.4);
		setInputCount(9);
		setOutputCount(4);
		initSubmodelComponents();
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents()
	{
		Pin S0 = getInputSubmodelPins().get(0);
		Pin I0_1 = getInputSubmodelPins().get(1);
		Pin I0_2 = getInputSubmodelPins().get(2);
		Pin I0_3 = getInputSubmodelPins().get(3);
		Pin I0_4 = getInputSubmodelPins().get(4);
		Pin I1_1 = getInputSubmodelPins().get(5);
		Pin I1_2 = getInputSubmodelPins().get(6);
		Pin I1_3 = getInputSubmodelPins().get(7);
		Pin I1_4 = getInputSubmodelPins().get(8);
		Pin Y1 = getOutputSubmodelPins().get(0);
		Pin Y2 = getOutputSubmodelPins().get(1);
		Pin Y3 = getOutputSubmodelPins().get(2);
		Pin Y4 = getOutputSubmodelPins().get(3);

		GUImux1 mux1 = new GUImux1(submodelModifiable);
		GUImux1 mux2 = new GUImux1(submodelModifiable);
		GUImux1 mux3 = new GUImux1(submodelModifiable);
		GUImux1 mux4 = new GUImux1(submodelModifiable);

		WireCrossPoint cp1 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp2 = new WireCrossPoint(submodelModifiable, 1);
		WireCrossPoint cp3 = new WireCrossPoint(submodelModifiable, 1);

		mux1.moveTo(30, 7.5);
		mux2.moveTo(30, 42.5);
		mux3.moveTo(30, 77.5);
		mux4.moveTo(30, 112.5);
		cp1.moveTo(25, 12.5);
		cp2.moveTo(25, 47.5);
		cp3.moveTo(25, 82.5);

		new GUIWire(submodelModifiable, S0, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, mux1.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, I0_1, mux1.getInputPins().get(1), new Point(5, 37.5), new Point(5, 22.5));
		new GUIWire(submodelModifiable, I1_1, mux1.getInputPins().get(2), new Point(10, 137.5), new Point(10, 32.5));
		new GUIWire(submodelModifiable, mux1.getOutputPins().get(0), Y1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, mux2.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, I0_2, mux2.getInputPins().get(1), new Point(5, 62.5), new Point(5, 57.5));
		new GUIWire(submodelModifiable, I1_2, mux2.getInputPins().get(2), new Point(15, 162.5), new Point(15, 67.5));
		new GUIWire(submodelModifiable, mux2.getOutputPins().get(0), Y2);
		new GUIWire(submodelModifiable, cp2, cp3, new Point[0]);
		new GUIWire(submodelModifiable, cp3, mux3.getInputPins().get(0), new Point[0]);
		new GUIWire(submodelModifiable, I0_3, mux3.getInputPins().get(1), new Point(5, 87.5), new Point(5, 92.5));
		new GUIWire(submodelModifiable, I1_3, mux3.getInputPins().get(2), new Point(20, 187.5), new Point(20, 102.5));
		new GUIWire(submodelModifiable, mux3.getOutputPins().get(0), Y3);
		new GUIWire(submodelModifiable, cp3, mux4.getInputPins().get(0), new Point(25, 117.5));
		new GUIWire(submodelModifiable, I0_4, mux4.getInputPins().get(1), new Point(5, 112.5), new Point(5, 127.5));
		new GUIWire(submodelModifiable, I1_4, mux4.getInputPins().get(2), new Point(25, 212.5), new Point(25, 137.5));
		new GUIWire(submodelModifiable, mux4.getOutputPins().get(0), Y4);

	}
}