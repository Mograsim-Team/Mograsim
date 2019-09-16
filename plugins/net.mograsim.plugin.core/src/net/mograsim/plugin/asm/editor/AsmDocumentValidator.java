package net.mograsim.plugin.asm.editor;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class AsmDocumentValidator implements IDocumentListener
{

	private final IFile file;
	private IMarker marker;

	AsmDocumentValidator(IFile file)
	{
		this.file = file;
	}

	@Override
	public void documentChanged(DocumentEvent event)
	{
		if (this.marker != null)
		{
			try
			{
				this.marker.delete();
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
			this.marker = null;
		}
		try (StringReader reader = new StringReader(event.getDocument().get());)
		{
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			documentBuilder.parse(new InputSource(reader));
		}
		catch (Exception ex)
		{
			try
			{
				this.marker = file.createMarker(IMarker.PROBLEM);
				this.marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				this.marker.setAttribute(IMarker.MESSAGE, ex.getMessage());
				if (ex instanceof SAXParseException)
				{
					SAXParseException saxParseException = (SAXParseException) ex;
					int lineNumber = saxParseException.getLineNumber();
					int offset = event.getDocument().getLineInformation(lineNumber - 1).getOffset() + saxParseException.getColumnNumber()
							- 1;
					this.marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
					this.marker.setAttribute(IMarker.CHAR_START, offset);
					this.marker.setAttribute(IMarker.CHAR_END, offset + 1);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event)
	{
	}

}
