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
		Pin C1 = getInputSubmodelPins().get(0);
		Pin C2 = getInputSubmodelPins().get(1);
		Pin C3 = getInputSubmodelPins().get(2);
		Pin C4 = getInputSubmodelPins().get(3);
		Pin A1 = getInputSubmodelPins().get(4);
		Pin A2 = getInputSubmodelPins().get(5);
		Pin A3 = getInputSubmodelPins().get(6);
		Pin A4 = getInputSubmodelPins().get(7);
		Pin B = getInputSubmodelPins().get(8);
		Pin Y1 = getOutputSubmodelPins().get(0);
		Pin Y2 = getOutputSubmodelPins().get(1);
		Pin Y3 = getOutputSubmodelPins().get(2);
		Pin Y4 = getOutputSubmodelPins().get(3);

		GUIand41 and = new GUIand41(submodelModifiable);
		GUIor_4 or = new GUIor_4(submodelModifiable);

		and.moveTo(15, 137.5);
		or.moveTo(35, 37.5);

		new GUIWire(submodelModifiable, A1, and.getInputPins().get(0), new Point(10, 112.5), new Point(10, 142.5));
		new GUIWire(submodelModifiable, A2, and.getInputPins().get(1), new Point(5, 137.5), new Point(5, 152.5));
		new GUIWire(submodelModifiable, A3, and.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, A4, and.getInputPins().get(3), new Point(5, 187.5), new Point(5, 172.5));
		new GUIWire(submodelModifiable, B, and.getInputPins().get(4), new Point(10, 212.5), new Point(10, 182.5));
		new GUIWire(submodelModifiable, C1, or.getInputPins().get(0), new Point(10, 12.5), new Point(10, 42.5));
		new GUIWire(submodelModifiable, C2, or.getInputPins().get(1), new Point(5, 37.5), new Point(5, 52.5));
		new GUIWire(submodelModifiable, C3, or.getInputPins().get(2), new Point[0]);
		new GUIWire(submodelModifiable, C4, or.getInputPins().get(3), new Point(5, 87.5), new Point(5, 72.5));
		new GUIWire(submodelModifiable, and.getOutputPins().get(0), or.getInputPins().get(4), new Point(70, 142.5), new Point(70, 120),
				new Point(30, 120), new Point(30, 82.5));
		new GUIWire(submodelModifiable, and.getOutputPins().get(1), or.getInputPins().get(5), new Point(65, 152.5), new Point(65, 125),
				new Point(25, 125), new Point(25, 92.5));
		new GUIWire(submodelModifiable, and.getOutputPins().get(2), or.getInputPins().get(6), new Point(60, 162.5), new Point(60, 130),
				new Point(20, 130), new Point(20, 102.5));
		new GUIWire(submodelModifiable, and.getOutputPins().get(3), or.getInputPins().get(7), new Point(55, 172.5), new Point(55, 135),
				new Point(15, 135), new Point(15, 112.5));
		new GUIWire(submodelModifiable, or.getOutputPins().get(0), Y1, new Point(75, 42.5), new Point(75, 12.5));
		new GUIWire(submodelModifiable, or.getOutputPins().get(1), Y2, new Point(80, 52.5), new Point(80, 37.5));
		new GUIWire(submodelModifiable, or.getOutputPins().get(2), Y3, new Point[0]);
		new GUIWire(submodelModifiable, or.getOutputPins().get(3), Y4, new Point(80, 72.5), new Point(80, 87.5));
	}
}