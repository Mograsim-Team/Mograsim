package net.mograsim.logic.model.model.components.atomic;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.SplitterAdapter;
import net.mograsim.preferences.ColorDefinition;
import net.mograsim.preferences.ColorManager;
import net.mograsim.preferences.Preferences;

public class GUISplitter extends GUIComponent
{
	private static final double width = 20;
	private static final double heightPerPin = 10;

	public final int logicWidth;

	private ReadEnd inputEnd;
	private ReadEnd[] outputEnds;

	public GUISplitter(ViewModelModifiable model, int logicWidth, String name)
	{
		super(model, name);
		this.logicWidth = logicWidth;
		setSize(width, logicWidth * heightPerPin);
		addPin(new Pin(this, "I", logicWidth, 0, logicWidth * heightPerPin / 2));
		double outputHeight = 0;
		for (int i = 0; i < logicWidth; i++, outputHeight += 10)
			addPin(new Pin(this, "O" + i, 1, width, outputHeight));
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		double posX = getPosX();
		double posY = getPosY();

		ColorDefinition c = BitVectorFormatter.formatAsColor(inputEnd);
		if (c != null)
			gc.setForeground(ColorManager.current().toColor(c));
		gc.drawLine(posX, posY + heightPerPin * logicWidth / 2, posX + width / 2, posY + heightPerPin * logicWidth / 2);
		gc.setForeground(Preferences.current().getColor("net.mograsim.logic.model.color.foreground"));
		gc.drawLine(posX + width / 2, posY, posX + width / 2, posY + heightPerPin * (logicWidth - 1));
		double outputHeight = posY;
		for (int i = 0; i < logicWidth; i++, outputHeight += 10)
		{
			c = BitVectorFormatter.formatAsColor(outputEnds[i]);
			if (c != null)
				gc.setForeground(ColorManager.current().toColor(c));
			gc.drawLine(posX + width / 2, outputHeight, posX + width, outputHeight);
		}
	}

	public void setLogicModelBinding(ReadEnd inputEnd, ReadEnd[] outputEnds)
	{
		this.inputEnd = inputEnd;
		this.outputEnds = outputEnds;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new SplitterAdapter());
	}
}