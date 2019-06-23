package net.mograsim.plugin.asm.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.Reconciler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

public class AsmReconciler extends Reconciler
{

	private AsmReconcilerStrategy fStrategy;

	public AsmReconciler()
	{
		fStrategy = new AsmReconcilerStrategy();
		this.setReconcilingStrategy(fStrategy, IDocument.DEFAULT_CONTENT_TYPE);
	}

	@Override
	public void install(ITextViewer textViewer)
	{
		super.install(textViewer);
		ProjectionViewer pViewer = (ProjectionViewer) textViewer;
		fStrategy.setProjectionViewer(pViewer);
	}
}