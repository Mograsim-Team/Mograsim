package net.mograsim.plugin.wizards.newWizards;

public class NewWizardMEM extends BasicNewWizard
{
	@Override
	public boolean performFinish()
	{
		setFileExtension("mem");
		return super.performFinish();
	}

	@Override
	public String getWindowTitle()
	{
		return "Create new Main Memory";
	}
}
