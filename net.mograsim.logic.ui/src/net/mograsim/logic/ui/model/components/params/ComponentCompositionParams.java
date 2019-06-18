package net.mograsim.logic.ui.model.components.params;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.model.components.params.SubmodelComponentParams.InnerWireParams;

public class ComponentCompositionParams
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
