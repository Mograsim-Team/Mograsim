package net.mograsim.machine.standard.memory;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.SimpleRectangularLikeSymbolRenderer.SimpleRectangularLikeParams;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.machine.MainMemoryDefinition;

public class ModelMemoryWA extends ModelComponent
{
	private final MainMemoryDefinition		definition;
	private final Pin						addrPin, dataPin, rWPin;
	private WordAddressableMemoryComponent	memory;
	private final static int				width	= 100, height = 300;
	private Renderer						symbolRenderer;
	private Renderer						outlineRenderer;

	public ModelMemoryWA(LogicModelModifiable model, MainMemoryDefinition definition, String name)
	{
		super(model, name, false);
		this.definition = definition;

		SimpleRectangularLikeParams rendererParams = new SimpleRectangularLikeParams();
		rendererParams.centerText = "RAM";
		rendererParams.centerTextHeight = 24;
		rendererParams.horizontalComponentCenter = width / 100;
		rendererParams.pinLabelHeight = 17.5;
		rendererParams.pinLabelMargin = 2.5;
		this.symbolRenderer = new SimpleRectangularLikeSymbolRenderer(this, rendererParams);
		this.outlineRenderer = new DefaultOutlineRenderer(this);

		setSize(width, height);
		//TODO check the usages
		addPin(addrPin = new Pin(model, this, "A", definition.getMemoryAddressBits(), PinUsage.INPUT, 0, 10));
		addPin(dataPin = new Pin(model, this, "D", definition.getCellWidth(), PinUsage.TRISTATE, 0, 30));
		addPin(rWPin = new Pin(model, this, "RW", 1, PinUsage.INPUT, 0, 50));

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

	public void setCoreModelBinding(WordAddressableMemoryComponent memory)
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
		IndirectModelComponentCreator.setComponentSupplier(ModelMemoryWA.class.getCanonicalName(), (m, p, n) ->
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
