package net.mograsim.machine.standard.memory;

import org.eclipse.swt.graphics.Color;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Font;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIAndGate;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.machine.MainMemoryDefinition;
import net.mograsim.preferences.Preferences;

public class GUIMemoryWA extends GUIComponent
{
	private final MainMemoryDefinition		definition;
	private final Pin						addrPin, dataPin, rWPin;
	private WordAddressableMemoryComponent	memory;
	private final static int				width	= 100, height = 300;

	public GUIMemoryWA(ViewModelModifiable model, MainMemoryDefinition definition, String name)
	{
		super(model, name);
		this.definition = definition;
		setSize(width, height);
		//TODO check the usages
		addPin(addrPin = new Pin(this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, 0, 10));
		addPin(dataPin = new Pin(this, "D", definition.getCellWidth(), PinUsage.TRISTATE, 0, 30));
		addPin(rWPin = new Pin(this, "RW", 1, PinUsage.INPUT, 0, 50));
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

	public void setLogicModelBinding(WordAddressableMemoryComponent memory)
	{
		this.memory = memory;
	}

	public MainMemoryDefinition getDefinition()
	{
		return definition;
	}

	public WordAddressableMemoryComponent getMemory()
	{
		return memory;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		// TODO This is copied from SimpleRectangularGUIGate; do this via delegation instead
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if(foreground != null)
			gc.setForeground(foreground);
		gc.drawRectangle(getPosX(), getPosY(), width, height);
		Font oldFont = gc.getFont();
		Font labelFont = new Font(oldFont.getName(), 24, oldFont.getStyle());
		gc.setFont(labelFont);
		String label = "RAM";
		Point textExtent = gc.textExtent(label);
		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
		if(textColor != null)
			gc.setForeground(textColor);
		gc.drawText(label, getPosX() + (width - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
		gc.setFont(oldFont);
	}

	@Override
	public GUIMemoryWAParams getParamsForSerializing(IdentifierGetter idGetter)
	{
		GUIMemoryWAParams params = new GUIMemoryWAParams();
		params.addrBits = definition.getMemoryAddressBits();
		params.cellWidth = definition.getCellWidth();
		params.minAddr = definition.getMinimalAddress();
		params.maxAddr = definition.getMaximalAddress();
		return params;
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new WordAddressableMemoryAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUIAndGate.class.getCanonicalName(), (m, p, n) ->
		{
			GUIMemoryWAParams params = JsonHandler.fromJsonTree(p, GUIMemoryWAParams.class);
			return new GUIMemoryWA(m, MainMemoryDefinition.create(params.addrBits, params.cellWidth, params.minAddr, params.maxAddr), n);
		});
	}

	public static class GUIMemoryWAParams
	{
		public int	addrBits;
		public int	cellWidth;
		public long	minAddr;
		public long	maxAddr;
	}
}
