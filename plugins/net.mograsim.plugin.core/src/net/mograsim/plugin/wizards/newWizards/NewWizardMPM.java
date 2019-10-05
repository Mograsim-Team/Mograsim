package net.mograsim.plugin.wizards.newWizards;

public class NewWizardMPM extends BasicNewWizard
{
	@Override
	public boolean performFinish()
	{
		setFileExtension("mpm");
		return super.performFinish();
	}

	@Override
	public String getWindowTitle()
	{
		return "Create new Microprogram Memory";
	}
}
