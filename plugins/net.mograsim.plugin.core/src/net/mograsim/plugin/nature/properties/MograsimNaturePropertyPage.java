package net.mograsim.plugin.nature.properties;

import java.util.Optional;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.MachineContextSwtTools;
import net.mograsim.plugin.nature.MachineContextSwtTools.MachineCombo;
import net.mograsim.plugin.nature.ProjectMachineContext;

public class MograsimNaturePropertyPage extends PropertyPage
{
	// TODO i10n
	private static final String WARNING = "Changing the Mograsim machine can completely break your project. Be careful.";
	private static final String MACHINE_LABEL = "Machine definition";
	private static final String MACHINE_PROPERTY = "net.mograsim.projectMachineId";
	private static final String DEFAULT_MACHINE = "Am2900Simple";// TODO don't hardcode that here!

	private MachineCombo machineSelect;
	private Label machineDescription;
	private MachineDefinition defaultMachineDefinition;

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
		Composite composite = createDefaultComposite(parent, false);

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
		Composite composite = createDefaultComposite(parent, true);

		// Label for machine
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(MACHINE_LABEL);

		// Machine choice
		machineSelect = MachineContextSwtTools.createMachineSelector(composite, SWT.NONE);

		machineDescription = new Label(composite, SWT.WRAP);
		machineSelect.addListener(md -> machineDescription.setText(md.getDescription()));
		machineDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Optional<MachineDefinition> currentMachineDefinition = machineContext.getMachineDefinition();

		if (currentMachineDefinition.isPresent())
			machineSelect.setSelection(currentMachineDefinition.get());

		defaultMachineDefinition = currentMachineDefinition.orElseGet(() -> MachineRegistry.getMachine(DEFAULT_MACHINE));
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

	protected void performDefaults()
	{
		super.performDefaults();
		// Populate the owner text field with the default value
		machineSelect.setSelection(defaultMachineDefinition);
	}

	public boolean performOk()
	{
		return machineContext.setMachineId(machineSelect.getSelection().getId());
	}

}