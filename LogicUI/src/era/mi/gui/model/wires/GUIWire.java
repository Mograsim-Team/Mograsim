package era.mi.gui.model.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import era.mi.gui.ColorHelper;
import era.mi.gui.model.ViewModel;
import era.mi.logic.types.BitVectorFormatter;
import era.mi.logic.wires.Wire.ReadEnd;
import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Point;

public class GUIWire
{
	private final ViewModel model;
	private Pin pin1;
	private Pin pin2;
	private double[] path;

	private final List<Consumer<? super GUIWire>> wireChangedListeners;

	private ReadEnd end;

	public GUIWire(ViewModel model, Pin pin1, Pin pin2, Point... path)
	{
		this.model = model;
		this.path = new double[path.length * 2 + 4];
		for (int srcI = 0, dstI = 2; srcI < path.length; srcI++, dstI += 2)
		{
			this.path[dstI + 0] = path[srcI].x;
			this.path[dstI + 1] = path[srcI].y;
		}

		this.pin1 = pin1;
		this.pin2 = pin2;

		wireChangedListeners = new ArrayList<>();

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
		end.addObserver((i, o) -> callWireChangedListeners());
	}

	// @formatter:off
	public void addWireChangedListener   (Consumer<? super GUIWire> listener) {wireChangedListeners.add   (listener);}

	public void removeWireChangedListener(Consumer<? super GUIWire> listener) {wireChangedListeners.remove(listener);}

	private void callWireChangedListeners() {wireChangedListeners.forEach(l -> l.accept(this));}
	// @formatter:on

}