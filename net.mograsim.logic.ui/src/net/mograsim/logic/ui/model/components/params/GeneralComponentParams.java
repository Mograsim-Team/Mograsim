package net.mograsim.logic.ui.model.components.params;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.components.params.SubComponentParams.InnerWireParams;

public class GeneralComponentParams
{
	public double innerScale;
	public InnerComponentParams[] subComps;
	public InnerWireParams[] innerWires;

	public static class InnerComponentParams
	{
		public Point pos;
		public String type;
		public int logicWidth;
	}
}
