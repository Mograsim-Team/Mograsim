package net.mograsim.plugin.wizards.newWizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class WizardPageMPM extends WizardNewFileCreationPage
{

	public WizardPageMPM(IStructuredSelection selection)
	{
		super("Create ", selection);
		setFileExtension("mpm");
	}
}
