package net.mograsim.logic.ui.serializing.snippets;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;

public interface Renderer
{
	public void render(GeneralGC t, Rectangle u);
}