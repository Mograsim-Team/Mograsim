package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.WordAddressableMemoryAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.Preferences;

public class GUIMemoryWA extends GUIComponent
{
	private final static String paramAddr = "addrBits", paramWordWidth = "wordWidth", paramMaxAddr = "maxAddr", paramMinAddr = "minAddr";
	private final int addressBits, wordWidth;
	public final long maximalAddress, minimalAddress;
	private final Pin addrPin, dataPin, rWPin;
	private final static int width = 100, height = 300;

	public GUIMemoryWA(ViewModelModifiable model, int addressBits, int wordWidth, long maximalAddress, long minimalAddress, String name)
	{
		super(model, name);
		this.addressBits = addressBits;
		this.wordWidth = wordWidth;
		this.maximalAddress = maximalAddress;
		this.minimalAddress = minimalAddress;
		setSize(width, height);
		addPin(addrPin = new Pin(this, "A", addressBits, 0, 10));
		addPin(dataPin = new Pin(this, "D", wordWidth, 0, 30));
		addPin(rWPin = new Pin(this, "RW", 1, 0, 50));
	}

	public Pin getAddressPin()
	{
		return addrPin;
	}

	public Pin getDataPin()
	{
		return dataPin;
	}

	public Pin getReadWritePin()
	{
		return rWPin;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		// TODO This is copied from SimpleRectangularGUIGate; do this via delegation instead
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getPosX(), getPosY(), width, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 24, oldFont.getStyle());
		gc.setFont(labelFont);
		String label = "RAM";
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if (textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (width - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	@Override
	public JsonElement getParamsForSerializing(IdentifierGetter idGetter)
	{
		JsonObject obj = new JsonObject();
		obj.addProperty(paramAddr, addressBits);
		obj.addProperty(paramWordWidth, wordWidth);
		obj.addProperty(paramMaxAddr, maximalAddress);
		obj.addProperty(paramMinAddr, minimalAddress);
		return obj;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new WordAddressableMemoryAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUIAndGate.class.getCanonicalName(), (m, p, n) ->
		{
			JsonObject obj = p.getAsJsonObject();
			return new GUIMemoryWA(m, obj.get(paramAddr).getAsInt(), obj.get(paramWordWidth).getAsInt(), obj.get(paramMaxAddr).getAsLong(),
					obj.get(paramMinAddr).getAsLong(), n);
		});
	}
}
