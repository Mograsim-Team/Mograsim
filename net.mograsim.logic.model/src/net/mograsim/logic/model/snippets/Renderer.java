package net.mograsim.logic.model.snippets;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public interface Renderer
{
	public void render(GeneralGC gc, Rectangle visibleRegion);
}