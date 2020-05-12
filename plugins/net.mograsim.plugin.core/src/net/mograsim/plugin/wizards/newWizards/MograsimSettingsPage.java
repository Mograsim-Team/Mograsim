package net.mograsim.plugin.wizards.newWizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.mograsim.plugin.nature.MachineContextSwtTools;
import net.mograsim.plugin.nature.MachineContextSwtTools.MachineCombo;

public class MograsimSettingsPage extends WizardPage
{
	private MachineCombo machineSelect;
	private Label machineDescription;

	public MograsimSettingsPage(IStructuredSelection selection)
	{
		this();
	}

	public MograsimSettingsPage()
	{
		super("Mograsim Project Settings");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private void addFirstSection(Composite parent)
	{
		Composite composite = createDefaultComposite(parent, false);

		// Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText("Please configure the machine that Mograsim should use:");
	}

	private void addSeparator(Composite parent)
	{
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private Composite createDefaultComposite(Composite parent, boolean grabExcessVerticalSpace)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = grabExcessVerticalSpace;
		composite.setLayoutData(data);

		return composite;
	}

	private void addSecondSection(Composite parent)
	{
		Composite composite = createDefaultComposite(parent, true);

		// Label for machine
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("Machine definition");

		// Machine choice
		machineSelect = MachineContextSwtTools.createMachineSelector(composite, SWT.NONE);
		machineSelect.addListener(md -> setPageComplete(isValid()));

		machineDescription = new Label(composite, SWT.WRAP);
		machineSelect.addListener(md -> machineDescription.setText(md.getDescription()));
		machineDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
	}

	public boolean isValid()
	{
		return machineSelect.isValidSelection();
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
//			machineSelect.getCombo().getCCombo().setFocus();
		}
	}

	public final MograsimProjectConfig getMograsimProjectConfig()
	{
		return new MograsimProjectConfig(machineSelect.getSelection().getId());
	}

	public static final class MograsimProjectConfig
	{
		final String machineId;

		public MograsimProjectConfig(String machineId)
		{
			this.machineId = machineId;
		}

		public final String getMachineId()
		{
			return machineId;
		}
	}
}
