package net.mograsim.machine.standard.memory;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelAndGate;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer.CenteredTextParams;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.machine.MainMemoryDefinition;

public class ModelMemoryWA extends ModelComponent
{
	private final MainMemoryDefinition		definition;
	private final Pin						addrPin, dataPin, rWPin, clock;
	private CoreWordAddressableMemory	memory;
	private final static int				width	= 100, height = 300;
	private Renderer						symbolRenderer;
	private Renderer						outlineRenderer;

	public ModelMemoryWA(LogicModelModifiable model, MainMemoryDefinition definition, String name)
	{
		super(model, name,false);
		this.definition = definition;

		CenteredTextParams renderer1Params = new CenteredTextParams();
		renderer1Params.text = "RAM";
		renderer1Params.fontHeight = 24;
		this.symbolRenderer = new CenteredTextSymbolRenderer(this, renderer1Params);
		this.outlineRenderer = new DefaultOutlineRenderer(this);

		setSize(width, height);
		//TODO check the usages
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, 0, 10));
		addPin(dataPin = new Pin(model, this, "D", definition.getCellWidth(), PinUsage.TRISTATE, 0, 30));
		addPin(rWPin = new Pin(model, this, "RW", 1, PinUsage.INPUT, 0, 50));
		addPin(clock = new Pin(model, this, "C", 1, PinUsage.INPUT, 0, 70));
		
		init();
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
	
	public Pin getClockPin()
	{
		return clock;
	}

	public void setCoreModelBinding(CoreWordAddressableMemory memory)
	{
		this.memory = memory;
	}

	public MainMemoryDefinition getDefinition()
	{
		return definition;
	}

	public CoreWordAddressableMemory getMemory()
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
		return "MemoryWA";
	}

	@Override
	public ModelMemoryWAParams getParamsForSerializing(IdentifyParams idParams)
	{
		ModelMemoryWAParams params = new ModelMemoryWAParams();
		params.addrBits = definition.getMemoryAddressBits();
		params.cellWidth = definition.getCellWidth();
		params.minAddr = definition.getMinimalAddress();
		params.maxAddr = definition.getMaximalAddress();
		return params;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new WordAddressableMemoryAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelAndGate.class.getCanonicalName(), (m, p, n) ->
		{
			ModelMemoryWAParams params = JsonHandler.fromJsonTree(p, ModelMemoryWAParams.class);
			return new ModelMemoryWA(m, MainMemoryDefinition.create(params.addrBits, params.cellWidth, params.minAddr, params.maxAddr), n);
		});
	}

	public static class ModelMemoryWAParams
	{
		public int	addrBits;
		public int	cellWidth;
		public long	minAddr;
		public long	maxAddr;
	}
}
