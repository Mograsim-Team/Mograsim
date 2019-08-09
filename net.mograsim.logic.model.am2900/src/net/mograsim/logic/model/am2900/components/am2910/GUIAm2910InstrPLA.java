package net.mograsim.logic.model.am2900.components.am2910;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.Am2910InstrPLAAdapter;
import net.mograsim.logic.model.snippets.Renderer;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.CenteredTextSymbolRenderer.CenteredTextParams;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams;
import net.mograsim.logic.model.snippets.symbolrenderers.PinNamesSymbolRenderer.PinNamesParams.Position;

public class GUIAm2910InstrPLA extends GUIComponent
{
	private final Renderer renderer1;
	private final Renderer renderer2;
	private final Renderer renderer3;

	public GUIAm2910InstrPLA(ViewModelModifiable model, String name)
	{
		super(model, name);
		setSize(30, 85);
		addPin(new Pin(this, "PASS", 1, 0, 5));
		addPin(new Pin(this, "I3", 1, 0, 20));
		addPin(new Pin(this, "I2", 1, 0, 30));
		addPin(new Pin(this, "I1", 1, 0, 40));
		addPin(new Pin(this, "I0", 1, 0, 50));
		addPin(new Pin(this, "R=0", 1, 15, 0));
		addPin(new Pin(this, "_PL", 1, 5, 85));
		addPin(new Pin(this, "_MAP", 1, 15, 85));
		addPin(new Pin(this, "_VECT", 1, 25, 85));
		addPin(new Pin(this, "RWE", 1, 30, 5));
		addPin(new Pin(this, "RDEC", 1, 30, 15));
		addPin(new Pin(this, "YD", 1, 30, 25));
		addPin(new Pin(this, "YR", 1, 30, 35));
		addPin(new Pin(this, "YF", 1, 30, 45));
		addPin(new Pin(this, "YmuPC", 1, 30, 55));
		addPin(new Pin(this, "STKI0", 1, 30, 65));
		addPin(new Pin(this, "STKI1", 1, 30, 75));
		this.renderer1 = new DefaultOutlineRenderer(this);
		CenteredTextParams renderer2Params = new CenteredTextParams();
		renderer2Params.text = "Instr.\nPLA";
		renderer2Params.fontHeight = 5;
		this.renderer2 = new CenteredTextSymbolRenderer(this, renderer2Params);
		PinNamesParams renderer3Params = new PinNamesParams();
		renderer3Params.pinLabelHeight = 3.5;
		renderer3Params.pinLabelMargin = .5;
		PinNamesSymbolRenderer pinNamesRenderer;
		this.renderer3 = pinNamesRenderer = new PinNamesSymbolRenderer(this, renderer3Params);
		pinNamesRenderer.setPinPosition("PASS", Position.RIGHT);
		pinNamesRenderer.setPinPosition("I3", Position.RIGHT);
		pinNamesRenderer.setPinPosition("I2", Position.RIGHT);
		pinNamesRenderer.setPinPosition("I1", Position.RIGHT);
		pinNamesRenderer.setPinPosition("I0", Position.RIGHT);
		pinNamesRenderer.setPinPosition("R=0", Position.BOTTOM);
		pinNamesRenderer.setPinPosition("_PL", Position.TOP);
		pinNamesRenderer.setPinPosition("_MAP", Position.TOP);
		pinNamesRenderer.setPinPosition("_VECT", Position.TOP);
		pinNamesRenderer.setPinPosition("RWE", Position.LEFT);
		pinNamesRenderer.setPinPosition("RDEC", Position.LEFT);
		pinNamesRenderer.setPinPosition("YD", Position.LEFT);
		pinNamesRenderer.setPinPosition("YR", Position.LEFT);
		pinNamesRenderer.setPinPosition("YF", Position.LEFT);
		pinNamesRenderer.setPinPosition("YmuPC", Position.LEFT);
		pinNamesRenderer.setPinPosition("STKI0", Position.LEFT);
		pinNamesRenderer.setPinPosition("STKI1", Position.LEFT);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		renderer1.render(gc, visibleRegion);
		renderer2.render(gc, visibleRegion);
		renderer3.render(gc, visibleRegion);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new Am2910InstrPLAAdapter());
	}
}