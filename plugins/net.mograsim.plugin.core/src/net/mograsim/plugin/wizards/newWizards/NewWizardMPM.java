package net.mograsim.plugin.wizards.newWizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewWizardMPM extends Wizard implements INewWizard
{

	private IStructuredSelection selection;
	private WizardPageMPM page;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.selection = selection;
	}

	@Override
	public void addPages()
	{
		addPage(page = new WizardPageMPM(selection));
	}

	@Override
	public String getWindowTitle()
	{
		return "Create new Microprogram Memory";
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
