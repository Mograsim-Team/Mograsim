package net.mograsim.logic.model.snippets;

import net.haspamelodica.swt.helper.gcs.GeneralGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.JSONSerializable;

public interface Renderer extends JSONSerializable
{
	public void render(GeneralGC gc, RenderPreferences renderPrefs, Rectangle visibleRegion);
}