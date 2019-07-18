package net.mograsim.plugin.asm.editor;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import net.mograsim.plugin.AsmOps;

public class AsmContentAssistProcessor implements IContentAssistProcessor
{

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		String text = viewer.getDocument().get();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		if (text.length() >= natureTag.length()
//				&& text.substring(offset - natureTag.length(), offset).equals(natureTag)) {
//			IProjectNatureDescriptor[] natureDescriptors = workspace.getNatureDescriptors();
//			ICompletionProposal[] proposals = new ICompletionProposal[natureDescriptors.length];
//			for (int i = 0; i < natureDescriptors.length; i++) {
//				IProjectNatureDescriptor descriptor = natureDescriptors[i];
//				proposals[i] = new CompletionProposal(descriptor.getNatureId(), offset, 0,
//						descriptor.getNatureId().length());
//			}
//			return proposals;
//		}
//		if (text.length() >= projectReferenceTag.length()
//				&& text.substring(offset - projectReferenceTag.length(), offset).equals(projectReferenceTag)) {
//			IProject[] projects = workspace.getRoot().getProjects();
//			ICompletionProposal[] proposals = new ICompletionProposal[projects.length];
//			for (int i = 0; i < projects.length; i++) {
//				proposals[i] = new CompletionProposal(projects[i].getName(), offset, 0, projects[i].getName().length());
//			}
//			return proposals;
//		}
//		return new ICompletionProposal[0];
//		text.
		return AsmOps.ops.stream().map(o -> new AsmOperationProposal(o, "", offset)).toArray(ICompletionProposal[]::new);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '\n', '\r' }; // NON-NLS-1
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters()
	{
		return null;
	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		return null;
	}

	private class AsmOperationProposal implements ICompletionProposal, ICompletionProposalExtension4
	{

		private String asmOp;
		private String desc;
		private int offset;

		public AsmOperationProposal(String asmOp, String desc, int offset)
		{
			this.asmOp = asmOp;
			this.desc = desc;
			this.offset = offset;
		}

		@Override
		public boolean isAutoInsertable()
		{
			return true;
		}

		@Override
		public void apply(IDocument document)
		{
			try
			{
				document.replace(offset, 0, asmOp);
			}
			catch (BadLocationException e)
			{
				// ignore
			}
		}

		@Override
		public Point getSelection(IDocument document)
		{
			return new Point(offset + asmOp.length(), 0);
		}

		@Override
		public String getAdditionalProposalInfo()
		{
			return desc;
		}

		@Override
		public String getDisplayString()
		{
			return asmOp;
		}

		@Override
		public Image getImage()
		{
			return null; // TODO image?
		}

		@Override
		public IContextInformation getContextInformation()
		{
			return new IContextInformation()
			{

				@Override
				public String getInformationDisplayString()
				{
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Image getImage()
				{
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getContextDisplayString()
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
		}

	}

}