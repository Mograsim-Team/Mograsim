package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.JsonSyntaxException;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.OrientationCalculator;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.model.wires.PinUsage;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.TriStateBufferAdapter;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;
import net.mograsim.preferences.Preferences;

public class ModelTriStateBuffer extends ModelComponent
{

	private static final double width = 20;
	private static final double height = 20;
	private Pin input;
	private Pin output;
	private Pin enable;
	private double[] path;

	private ModelTriStateBufferParams params;
	private OrientationCalculator oc;

	public ModelTriStateBuffer(ViewModelModifiable model, ModelTriStateBufferParams params)
	{
		this(model, params, null);
	}

	public ModelTriStateBuffer(ViewModelModifiable model, ModelTriStateBufferParams params, String name)
	{
		super(model, name);
		this.params = params;

		oc = new OrientationCalculator(params.orientation, width, height);

		double wHalf = width / 2;
		double hHalf = height / 2;
		double hQuar = height / 4;

		this.input = new Pin(this, "IN", params.logicWidth, PinUsage.INPUT, oc.newX(0, hHalf), oc.newY(0, hHalf));
		this.output = new Pin(this, "OUT", params.logicWidth, PinUsage.OUTPUT, oc.newX(width, hHalf), oc.newY(width, hHalf));
		this.enable = new Pin(this, "EN", 1, PinUsage.INPUT, oc.newX(wHalf, hQuar), oc.newY(wHalf, hQuar));
		this.path = new double[] { oc.newX(0, 0), oc.newY(0, 0), oc.newX(width, hHalf), oc.newY(width, hHalf), oc.newX(0, height),
				oc.newY(0, height) };

		setSize(oc.width(), oc.height());
		addPin(input);
		addPin(output);
		addPin(enable);
	}

	public final Pin getInputPin()
	{
		return input;
	}

	public final Pin getOutputPin()
	{
		return output;
	}

	public final Pin getEnablePin()
	{
		return enable;
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{
		Color foreground = Preferences.current().getColor("net.mograsim.logic.model.color.foreground");
		if (foreground != null)
			gc.setForeground(foreground);
		double x = getPosX();
		double y = getPosY();
		gc.drawPolygon(new double[] { x + path[0], y + path[1], x + path[2], y + path[3], x + path[4], y + path[5] });
	}

	@Override
	public String getIDForSerializing(IdentifyParams idParams)
	{
		return "TriStateBuffer";
	}

	@Override
	public ModelTriStateBufferParams getParamsForSerializing(IdentifyParams idParams)
	{
		return params;
	}

	static
	{
		LogicCoreAdapter.addComponentAdapter(new TriStateBufferAdapter());
		IndirectModelComponentCreator.setComponentSupplier(ModelTriStateBuffer.class.getName(), (m, p, n) ->
		{
			ModelTriStateBufferParams params = JsonHandler.fromJsonTree(p, ModelTriStateBufferParams.class);
			if (params == null)
				throw new JsonSyntaxException("Invalid!!!");
			return new ModelTriStateBuffer(m, params, n);
		});
	}

	public static class ModelTriStateBufferParams
	{
		int logicWidth;
		Orientation orientation;

		public ModelTriStateBufferParams(int logicWidth, Orientation orientation)
		{
			this.logicWidth = logicWidth;
			this.orientation = orientation;
		}
	}
}
