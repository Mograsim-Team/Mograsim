package net.mograsim.logic.model.editor.ui;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import net.haspamelodica.swt.helper.gcs.TranslatedGC;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.logic.model.editor.Editor;
import net.mograsim.logic.model.editor.handles.Handle;

//TODO: Remove Inheritance 
public class EditorCanvas extends LogicUICanvas
{
	private Collection<Handle> handles;

	public EditorCanvas(Composite parent, int style, Editor editor)
	{
		super(parent, style, editor.toBeEdited.submodel, editor.renderPrefs);

		handles = editor.handleManager.getHandles();
		editor.handleManager.addHandleAddedListener(h ->
		{
			redrawThreadsafe();
			h.addRedrawListener(this::redrawThreadsafe);
		});
		// TODO: Is this even necessary? The Handle should be finalized by the gc
		editor.handleManager.addHandleRemovedListener(h ->
		{
			redrawThreadsafe();
			h.removeRedrawListener(this::redrawThreadsafe);
		});

		addZoomedRenderer(gc ->
		{
			Rectangle visibleRegion = new Rectangle(-offX / zoom, -offY / zoom, gW / zoom, gH / zoom);

			TranslatedGC tgc = new TranslatedGC(gc, 0.0d, 0.0d, 1 / editor.toBeEdited.getSubmodelScale(), false);
			editor.toBeEdited.getOutlineRenderer().render(tgc, renderPrefs,
					new Rectangle(-offX / zoom, -offY / zoom, gW / zoom, gH / zoom));

			handles.forEach(h -> h.render(gc, visibleRegion));
		});
	}
}
