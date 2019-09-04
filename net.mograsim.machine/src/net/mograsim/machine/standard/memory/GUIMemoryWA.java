package net.mograsim.machine.standard.memory;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.components.atomic.GUIAndGate;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer.CenteredTextParams;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.machine.MainMemoryDefinition;

public class GUIMemoryWA extends GUIComponent
{
	private final MainMemoryDefinition		definition;
	private final Pin						addrPin, dataPin, rWPin;
	private WordAddressableMemoryComponent	memory;
	private final static int				width	= 100, height = 300;
	private Renderer						symbolRenderer;
	private Renderer						outlineRenderer;

	public GUIMemoryWA(ViewModelModifiable model, MainMemoryDefinition definition, String name)
	{
		super(model, name);
		this.definition = definition;

		CenteredTextParams renderer1Params = new CenteredTextParams();
		renderer1Params.text = "RAM";
		renderer1Params.fontHeight = 24;
		this.symbolRenderer = new CenteredTextSymbolRenderer(this, renderer1Params);
		this.outlineRenderer = new DefaultOutlineRenderer(this);

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
		symbolRenderer.render(gc, visibleRegion);
		outlineRenderer.render(gc, visibleRegion);
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "GUIMemoryWA";//TODO
	}

	@Override
	public GUIMemoryWAParams getParamsForSerializing(IdentifyParams idParams)
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
