package net.mograsim.logic.ui.model.components;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.ui.model.ViewModelModifiable;

public class SubmodelInterface extends GUIComponent
{
	public SubmodelInterface(ViewModelModifiable model)
	{
		super(model);
	}

	@Override
	public void render(GeneralGC gc, Rectangle visibleRegion)
	{// nothing to do here
	}
}