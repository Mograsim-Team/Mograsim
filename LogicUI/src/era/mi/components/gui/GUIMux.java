package era.mi.components.gui;

import era.mi.logic.components.Mux;
import era.mi.logic.wires.WireArray;
import net.haspamelodica.swt.helper.gcs.GeneralGC;

public class GUIMux extends Mux implements BasicGUIComponent
{
	private final int inputCount;

	public GUIMux(int processTime, WireArray out, WireArray select, WireArray... inputs)
	{
		super(processTime, out, select, inputs);
		this.inputCount = inputs.length;
	}
	@Override
	public void render(GeneralGC gc)
	{
		double height = inputCount * 10;
		if(height < 20)
			height = 20;
		gc.drawPolygon(new double[] {
				0, 0,
				20, 10,
				20, height + 10,
				0, height + 20});
	}
}