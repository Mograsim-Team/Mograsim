package net.mograsim.logic.ui.model.components;

import java.util.List;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.Pin;

public class AtomicAm2901NANDBased extends GUIComponent
{
	public final List<String> inputNames;
	public final List<String> outputNames;

	public AtomicAm2901NANDBased(ViewModelModifiable model)
	{
		super(model);

		this.inputNames = List.of("I8", "I7", "I6", "I5", "I4", "I3", "I2", "I1", "I0", "C", "Cn", "D1", "D2", "D3", "D4", "A0", "A1", "A2",
				"A3", "B0", "B1", "B2", "B3", "IRAMn", "IRAMn+3", "IQn", "IQn+3");
		this.outputNames = List.of("Y1", "Y2", "Y3", "Y4", "F=0", "Cn+4", "OVR", "F3_ORAMn+3", "ORAMn", "OQn", "OQn+3");
		setSize(50, inputNames.size() * 10);
		for (int i = 0; i < inputNames.size(); i++)
			addPin(new Pin(this, inputNames.get(i), 1, 0, 5 + 10 * i));
		for (int i = 0; i < outputNames.size(); i++)
			addPin(new Pin(this, outputNames.get(i), 1, 50, 5 + 10 * i));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getBounds().x;
		double posY = getBounds().y;

		gc.drawRectangle(getBounds());
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 4, oldFont.getStyle());
		gc.setFont(labelFont);
		for (int i = 0; i < inputNames.size(); i++)
		{
			Point textExtent = gc.textExtent(inputNames.get(i));
			gc.drawText(inputNames.get(i), posX, posY + 5 + 10 * i - textExtent.y / 2, true);
		}
		for (int i = 0; i < outputNames.size(); i++)
		{
			Point textExtent = gc.textExtent(outputNames.get(i));
			gc.drawText(outputNames.get(i), posX + 50 - textExtent.x, posY + 5 + 10 * i - textExtent.y / 2, true);
		}
		gc.setFont(oldFont);
	}
}