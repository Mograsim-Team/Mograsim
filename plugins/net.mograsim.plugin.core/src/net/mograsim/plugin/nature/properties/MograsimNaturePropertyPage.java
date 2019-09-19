package net.mograsim.plugin.nature.properties;

import java.util.Optional;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.ProjectMachineContext;

public class MograsimNaturePropertyPage extends PropertyPage
{

	private static final String WARNING = "Changing the Mograsim machine can completely break your project. Be careful.";
	private static final String MACHINE_LABEL = "Machine ID";
	private static final String MACHINE_PROPERTY = "net.mograsim.projectMachineId";
	private static final String DEFAULT_MACHINE = "Am2900";

	private Combo machineSelect;
	private String defaultId;

	private MachineContext machineContext;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public MograsimNaturePropertyPage()
	{
		super();
	}

	private void addFirstSection(Composite parent)
	{
		Composite composite = createDefaultComposite(parent);

		// Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(WARNING);
	}

	private void addSeparator(Composite parent)
	{
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent)
	{
		Composite composite = createDefaultComposite(parent);

		// Label for machine
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(MACHINE_LABEL);

		// Machine choice
		machineSelect = new Combo(parent, SWT.BORDER);
		GridData gd = new GridData();
		machineSelect.setLayoutData(gd);

		Optional<String> currentId = machineContext.getMachineId();

		if (currentId.isPresent())
			machineSelect.add(currentId.get());

		for (String machineId : MachineRegistry.getInstalledMachines().keySet())
		{
			if (currentId.isPresent() && currentId.get().equals(machineId))
				continue;
			machineSelect.add(machineId);
		}

		defaultId = currentId.orElse(DEFAULT_MACHINE);

		machineSelect.select(machineSelect.indexOf(defaultId));
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent)
	{
		machineContext = ProjectMachineContext.getMachineContextOf(getElement());

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults()
	{
		super.performDefaults();
		// Populate the owner text field with the default value
		machineSelect.select(machineSelect.indexOf(DEFAULT_MACHINE));
	}

	public boolean performOk()
	{
		int selected = machineSelect.getSelectionIndex();
		String newId = machineSelect.getItem(selected);
		return machineContext.setMachineId(newId);
	}

}