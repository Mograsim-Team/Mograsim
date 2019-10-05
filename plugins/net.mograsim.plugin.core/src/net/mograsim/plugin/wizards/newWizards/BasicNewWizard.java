package net.mograsim.plugin.wizards.newWizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public abstract class BasicNewWizard extends Wizard implements INewWizard
{
	private IStructuredSelection selection;
	private WizardNewFileCreationPage page;

	public void setFileExtension(String fileExtension)
	{
		page.setFileExtension(fileExtension);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.selection = selection;
	}

	@Override
	public void addPages()
	{
		addPage(page = new WizardNewFileCreationPage("Create", selection));
	}

	@Override
	public boolean performFinish()
	{
		IFile file = page.createNewFile();
		if (file != null)
			return true;
		return false;
	}
}
