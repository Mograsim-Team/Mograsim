package net.mograsim.plugin.wizards.newWizards;

public class NewWizardMPROM extends BasicNewWizard
{
	@Override
	public boolean performFinish()
	{
		setFileExtension("mprom");
		return super.performFinish();
	}

	@Override
	public String getWindowTitle()
	{
		return "Create new Mapping PROM";
	}
}
