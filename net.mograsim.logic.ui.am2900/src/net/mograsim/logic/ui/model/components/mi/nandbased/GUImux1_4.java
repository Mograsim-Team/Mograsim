package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.submodels.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;

public class GUImux1_4 extends SimpleRectangularSubmodelComponent
{
	public GUImux1_4(ViewModelModifiable model)
	{
		super(model, 1, "GUImux1_4");
		setSubmodelScale(.4);
		setInputPins("S0", "I0_1", "I0_2", "I0_3", "I0_4", "I1_1", "I1_2", "I1_3", "I1_4");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents()
	{
		Pin S0 = getSubmodelPin("S0");
		Pin I0_1 = getSubmodelPin("I0_1");
		Pin I0_2 = getSubmodelPin("I0_2");
		Pin I0_3 = getSubmodelPin("I0_3");
		Pin I0_4 = getSubmodelPin("I0_4");
		Pin I1_1 = getSubmodelPin("I1_1");
		Pin I1_2 = getSubmodelPin("I1_2");
		Pin I1_3 = getSubmodelPin("I1_3");
		Pin I1_4 = getSubmodelPin("I1_4");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

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
		cp1.moveCenterTo(25, 12.5);
		cp2.moveCenterTo(25, 47.5);
		cp3.moveCenterTo(25, 82.5);

		new GUIWire(submodelModifiable, S0, cp1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, mux1.getPin("S0"), new Point[0]);
		new GUIWire(submodelModifiable, I0_1, mux1.getPin("I0"), new Point(5, 37.5), new Point(5, 22.5));
		new GUIWire(submodelModifiable, I1_1, mux1.getPin("I1"), new Point(10, 137.5), new Point(10, 32.5));
		new GUIWire(submodelModifiable, mux1.getPin("Y"), Y1, new Point[0]);
		new GUIWire(submodelModifiable, cp1, cp2, new Point[0]);
		new GUIWire(submodelModifiable, cp2, mux2.getPin("S0"), new Point[0]);
		new GUIWire(submodelModifiable, I0_2, mux2.getPin("I0"), new Point(5, 62.5), new Point(5, 57.5));
		new GUIWire(submodelModifiable, I1_2, mux2.getPin("I1"), new Point(15, 162.5), new Point(15, 67.5));
		new GUIWire(submodelModifiable, mux2.getPin("Y"), Y2);
		new GUIWire(submodelModifiable, cp2, cp3, new Point[0]);
		new GUIWire(submodelModifiable, cp3, mux3.getPin("S0"), new Point[0]);
		new GUIWire(submodelModifiable, I0_3, mux3.getPin("I0"), new Point(5, 87.5), new Point(5, 92.5));
		new GUIWire(submodelModifiable, I1_3, mux3.getPin("I1"), new Point(20, 187.5), new Point(20, 102.5));
		new GUIWire(submodelModifiable, mux3.getPin("Y"), Y3);
		new GUIWire(submodelModifiable, cp3, mux4.getPin("S0"), new Point(25, 117.5));
		new GUIWire(submodelModifiable, I0_4, mux4.getPin("I0"), new Point(5, 112.5), new Point(5, 127.5));
		new GUIWire(submodelModifiable, I1_4, mux4.getPin("I1"), new Point(25, 212.5), new Point(25, 137.5));
		new GUIWire(submodelModifiable, mux4.getPin("Y"), Y4);

	}
}