package net.mograsim.plugin.asm.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

public class ValidatorDocumentSetupParticipant implements IDocumentSetupParticipant, IDocumentSetupParticipantExtension
{

	@Override
	public void setup(IDocument document)
	{
	}

	@Override
	public void setup(IDocument document, IPath location, LocationKind locationKind)
	{
		if (locationKind == LocationKind.IFILE)
		{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
			document.addDocumentListener(new AsmDocumentValidator(file));
		}
	}

}
