package net.mograsim.logic.ui.model.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.mograsim.logic.ui.ColorHelper;
import net.mograsim.logic.ui.model.ViewModel;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.core.types.BitVectorFormatter;
import net.mograsim.logic.core.wires.Wire.ReadEnd;

public class GUIWire
{
	private final ViewModel model;
	public final int logicWidth;
	private Pin pin1;
	private Pin pin2;
	private double[] path;

	private final List<Consumer<? super GUIWire>> wireLookChangedListeners;

	private ReadEnd end;

	public GUIWire(ViewModel model, Pin pin1, Pin pin2, Point... path)
	{
		this.model = model;
		this.logicWidth = pin1.logicWidth;
		if (pin2.logicWidth != pin1.logicWidth)
			throw new IllegalArgumentException("Can't connect pins of different logic width");
		this.path = new double[path.length * 2 + 4];
		for (int srcI = 0, dstI = 2; srcI < path.length; srcI++, dstI += 2)
		{
			this.path[dstI + 0] = path[srcI].x;
			this.path[dstI + 1] = path[srcI].y;
		}

		this.pin1 = pin1;
		this.pin2 = pin2;

		wireLookChangedListeners = new ArrayList<>();

		pin1.addPinMovedListener(p -> pin1Moved());
		pin2.addPinMovedListener(p -> pin2Moved());
		pin1Moved();
		pin2Moved();

		model.wireCreated(this);
	}

	private void pin1Moved()
	{
		Point pos = pin1.getPos();
		this.path[0] = pos.x;
		this.path[1] = pos.y;
	}

	private void pin2Moved()
	{
		Point pos = pin2.getPos();
		this.path[this.path.length - 2] = pos.x;
		this.path[this.path.length - 1] = pos.y;
	}

	public void destroy()
	{
		model.wireDestroyed(this);
	}

	public void render(GeneralGC gc)
	{
		ColorHelper.executeWithDifferentForeground(gc, BitVectorFormatter.formatAsColor(end), () -> gc.drawPolyline(path));
	}

	public void setLogicModelBinding(ReadEnd end)
	{
		this.end = end;
		end.addObserver((i, o) -> callWireLookChangedListeners());
	}

	public Pin getPin1()
	{
		return pin1;
	}

	public Pin getPin2()
	{
		return pin2;
	}

	// @formatter:off
	public void addWireLookChangedListener   (Consumer<? super GUIWire> listener) {wireLookChangedListeners.add   (listener);}

	public void removeWireLookChangedListener(Consumer<? super GUIWire> listener) {wireLookChangedListeners.remove(listener);}

	private void callWireLookChangedListeners() {wireLookChangedListeners.forEach(l -> l.accept(this));}
	// @formatter:on

}