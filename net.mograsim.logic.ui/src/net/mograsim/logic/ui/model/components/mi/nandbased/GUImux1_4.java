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

		GUImux1 mux1 = new GUImux1(submodelModifiable);
		GUImux1 mux2 = new GUImux1(submodelModifiable);
		GUImux1 mux3 = new GUImux1(submodelModifiable);
		GUImux1 mux4 = new GUImux1(submodelModifiable);

		GUImux1 mux[] = { mux1, mux2, mux3, mux4 };
		WireCrossPoint[] cps = new WireCrossPoint[mux.length - 1];

		int muxXOffset = 25, muxYOffset = 20;
		Pin test = getInputPins().get(0);
		Pin test2 = mux[0].getInputPins().get(1);
		for (int i = 0; i < mux.length; i++)
		{
			mux[i].moveTo(muxXOffset, muxYOffset + mux[0].getBounds().height * i);
			Pin inPin = getInputSubmodelPins().get(i + 1), muxPin = mux[i].getInputPins().get(1);

			new GUIWire(submodelModifiable, inPin, muxPin, new Point(inPin.getPos().x + 5, inPin.getPos().y),
					new Point(inPin.getPos().x + 5, muxPin.getPos().y));
			new GUIWire(submodelModifiable, getInputSubmodelPins().get(mux.length + i + 1), mux[i].getInputPins().get(2));
			new GUIWire(submodelModifiable, mux[i].getOutputPins().get(0), getOutputSubmodelPins().get(i));

			if (i != mux.length - 1)
			{
				WireCrossPoint cp = new WireCrossPoint(submodelModifiable, logicWidth);
				cps[i] = cp;
				Pin p = mux[i].getInputPins().get(0);
				cp.moveTo(muxXOffset - 3, p.getPos().y);
				new GUIWire(submodelModifiable, cp, mux[i].getInputPins().get(0));
				if (i > 0)
					new GUIWire(submodelModifiable, cps[i - 1], cp);
				else
					new GUIWire(submodelModifiable, getInputSubmodelPins().get(0), cp);
			} else
			{
				new GUIWire(submodelModifiable, cps[i - 1], mux[i].getInputPins().get(0),
						new Point(cps[i - 1].getPin().getPos().x, mux[i].getInputPins().get(0).getPos().y));
			}
		}
	}
}