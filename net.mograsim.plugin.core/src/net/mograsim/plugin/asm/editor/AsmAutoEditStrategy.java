package net.mograsim.plugin.asm.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class AsmAutoEditStrategy implements IAutoEditStrategy
{

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		if (!">".equals(command.text))
		{ // NON-NLS-1
			return;
		}
		try
		{
			IRegion region = document.getLineInformationOfOffset(command.offset);
			String line = document.get(region.getOffset(), command.offset - region.getOffset());
			int index = line.lastIndexOf('<');
			if (index != -1 && (index != line.length() - 1) && line.charAt(index + 1) != '/')
			{
				String tag = line.substring(index + 1);
				command.caretOffset = command.offset + 1;
				command.text += "</" + tag + command.text; // NON-NLS-1
				command.shiftsCaret = false;
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
	}

}
