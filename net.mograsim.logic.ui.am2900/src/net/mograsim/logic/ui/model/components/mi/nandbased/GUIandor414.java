package net.mograsim.logic.ui.model.components.mi.nandbased;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SimpleRectangularSubmodelComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.model.wires.Pin;

public class GUIandor414 extends SimpleRectangularSubmodelComponent
{
	public GUIandor414(ViewModelModifiable model)
	{
		super(model, 1, "GUIandor414");
		setSubmodelScale(.4);
		setInputPins("C1", "C2", "C3", "C4", "A1", "A2", "A3", "A4", "B");
		setOutputPins("Y1", "Y2", "Y3", "Y4");
		initSubmodelComponents();
	}

	@SuppressWarnings("unused") // for GUIWires being created
	private void initSubmodelComponents()
	{
		Pin C1 = getSubmodelPin("C1");
		Pin C2 = getSubmodelPin("C2");
		Pin C3 = getSubmodelPin("C3");
		Pin C4 = getSubmodelPin("C4");
		Pin A1 = getSubmodelPin("A1");
		Pin A2 = getSubmodelPin("A2");
		Pin A3 = getSubmodelPin("A3");
		Pin A4 = getSubmodelPin("A4");
		Pin B = getSubmodelPin("B");
		Pin Y1 = getSubmodelPin("Y1");
		Pin Y2 = getSubmodelPin("Y2");
		Pin Y3 = getSubmodelPin("Y3");
		Pin Y4 = getSubmodelPin("Y4");

		GUIand41 and = new GUIand41(submodelModifiable);
		GUIor_4 or = new GUIor_4(submodelModifiable);

		and.moveTo(15, 137.5);
		or.moveTo(35, 37.5);

		new GUIWire(submodelModifiable, A1, and.getPin("A1"), new Point(10, 112.5), new Point(10, 142.5));
		new GUIWire(submodelModifiable, A2, and.getPin("A2"), new Point(5, 137.5), new Point(5, 152.5));
		new GUIWire(submodelModifiable, A3, and.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, A4, and.getPin("A4"), new Point(5, 187.5), new Point(5, 172.5));
		new GUIWire(submodelModifiable, B, and.getPin("B"), new Point(10, 212.5), new Point(10, 182.5));
		new GUIWire(submodelModifiable, C1, or.getPin("A1"), new Point(10, 12.5), new Point(10, 42.5));
		new GUIWire(submodelModifiable, C2, or.getPin("A2"), new Point(5, 37.5), new Point(5, 52.5));
		new GUIWire(submodelModifiable, C3, or.getPin("A3"), new Point[0]);
		new GUIWire(submodelModifiable, C4, or.getPin("A4"), new Point(5, 87.5), new Point(5, 72.5));
		new GUIWire(submodelModifiable, and.getPin("Y1"), or.getPin("B1"), new Point(70, 142.5), new Point(70, 120), new Point(30, 120),
				new Point(30, 82.5));
		new GUIWire(submodelModifiable, and.getPin("Y2"), or.getPin("B2"), new Point(65, 152.5), new Point(65, 125), new Point(25, 125),
				new Point(25, 92.5));
		new GUIWire(submodelModifiable, and.getPin("Y3"), or.getPin("B3"), new Point(60, 162.5), new Point(60, 130), new Point(20, 130),
				new Point(20, 102.5));
		new GUIWire(submodelModifiable, and.getPin("Y4"), or.getPin("B4"), new Point(55, 172.5), new Point(55, 135), new Point(15, 135),
				new Point(15, 112.5));
		new GUIWire(submodelModifiable, or.getPin("Y1"), Y1, new Point(75, 42.5), new Point(75, 12.5));
		new GUIWire(submodelModifiable, or.getPin("Y2"), Y2, new Point(80, 52.5), new Point(80, 37.5));
		new GUIWire(submodelModifiable, or.getPin("Y3"), Y3, new Point[0]);
		new GUIWire(submodelModifiable, or.getPin("Y4"), Y4, new Point(80, 72.5), new Point(80, 87.5));
	}
}