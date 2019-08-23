package net.mograsim.logic.model.model.components.atomic;

import org.eclipse.swt.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.model.ViewModelModifiable;
import net.mograsim.logic.model.model.components.GUIComponent;
import net.mograsim.logic.model.model.wires.Pin;
import net.mograsim.logic.model.modeladapter.ViewLogicModelAdapter;
import net.mograsim.logic.model.modeladapter.componentadapters.TriStateBufferAdapter;
import net.mograsim.logic.model.serializing.IdentifierGetter;
import net.mograsim.logic.model.serializing.IndirectGUIComponentCreator;
import net.mograsim.preferences.Preferences;

public class GUITriStateBuffer extends GUIComponent
{

	private static final double width = 20;
	private static final double height = 20;
	private Pin input;
	private Pin output;
	private Pin enable;
	private double[] path;

	private GUITriStateBufferParams params;

	public GUITriStateBuffer(ViewModelModifiable model, GUITriStateBufferParams params)
	{
		this(model, params, null);
	}

	public GUITriStateBuffer(ViewModelModifiable model, GUITriStateBufferParams params, String name)
	{
		super(model, name);
		this.params = params;

		double wHalf = width / 2;
		double hHalf = height / 2;
		double wQuar = width / 4;
		double hQuar = height / 4;
		int ordi = params.orientation.ordinal();
		int isVerti = (ordi % 4) / 2;
		int isHori = 1 ^ isVerti;
		int isAlt = ordi / 4;
		int isInv = ordi % 2;
		int isStd = 1 ^ isInv;

		this.input = new Pin(this, "IN", params.logicWidth, width * isInv * isHori + wHalf * isVerti,
				height * isVerti * isStd + hHalf * isHori);
		this.output = new Pin(this, "OUT", params.logicWidth, width * isStd * isHori + wHalf * isVerti,
				height * isVerti * isInv + hHalf * isHori);
		this.enable = new Pin(this, "EN", 1, wQuar * isVerti + wHalf * (isAlt | isHori), hQuar * isHori + hHalf * (isAlt | isVerti));
		this.path = new double[] { width * (isStd ^ isHori), height * (isStd ^ isHori), width * isInv, height * isStd,
				width * isStd * isHori + wHalf * isVerti, height * isVerti * isInv + hHalf * isHori };

		setSize(width, height);
		addPin(input);
		addPin(output);
		addPin(enable);
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
//		Font oldFont = gc.getFont();
//		Font labelFont = new Font(oldFont.getName(), fontHeight, oldFont.getStyle());
//		gc.setFont(labelFont);
//		Point textExtent = gc.textExtent(label);
//		Color textColor = Preferences.current().getColor("net.mograsim.logic.model.color.text");
//		if (textColor != null)
//			gc.setForeground(textColor);
//		gc.drawText(label, getPosX() + (rectWidth - textExtent.x) / 2, getPosY() + (height - textExtent.y) / 2, true);
//		gc.setFont(oldFont);
	}

	@Override
	public JsonElement getParamsForSerializing(IdentifierGetter idGetter)
	{
		return new Gson().toJsonTree(params);
	}

	static
	{
		ViewLogicModelAdapter.addComponentAdapter(new TriStateBufferAdapter());
		IndirectGUIComponentCreator.setComponentSupplier(GUITriStateBuffer.class.getName(), (m, p, n) ->
		{
			GUITriStateBufferParams params = new Gson().fromJson(p, GUITriStateBufferParams.class);
			if (params == null)
				throw new JsonSyntaxException("Invalid!!!");
			return new GUITriStateBuffer(m, params, n);
		});
	}

	private static class GUITriStateBufferParams
	{
		int logicWidth;
		Orientation orientation;
	}

	public enum Orientation
	{
		RIGHT, LEFT, UP, DOWN, RIGHT_ALT, LEFT_ALT, UP_ALT, DOWN_ALT;
	}
}
