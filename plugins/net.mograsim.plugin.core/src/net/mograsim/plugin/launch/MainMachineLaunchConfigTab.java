package net.mograsim.plugin.launch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import net.mograsim.plugin.nature.MograsimNature;
import net.mograsim.plugin.util.FileExtensionViewerFilter;
import net.mograsim.plugin.util.ImageDescriptorWithMargins;
import net.mograsim.plugin.util.ProjectViewerFilter;

//a big part of this class is stolen from org.eclipse.jdt.debug.ui
public class MainMachineLaunchConfigTab extends AbstractLaunchConfigurationTab
{
	private Text projSelText;
	private Text mpmFileSelText;
	private Text mpromFileSelText;
	private Text initialRAMFileSelText;

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new FillLayout());
		Composite innerParent = new Composite(parent, SWT.NONE);
		setControl(innerParent);

		innerParent.setLayout(new GridLayout(3, false));

		this.projSelText = addResourceSelector(innerParent, "P&roject:", this::chooseMograsimProject);

		this.mpmFileSelText = addResourceSelector(innerParent, "&MPM:", this::chooseMPMFile);

		this.mpromFileSelText = addResourceSelector(innerParent, "M&PROM (optional):", this::chooseMPROMFile);

		this.initialRAMFileSelText = addResourceSelector(innerParent, "Initial &RAM (optional):", this::chooseInitialRAMFile);
	}

	private Text addResourceSelector(Composite innerParent, String label, Supplier<String> chooser)
	{
		Label swtLabel = new Label(innerParent, SWT.NONE);
		swtLabel.setText(label);
		swtLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		Text text = new Text(innerParent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(e -> updateLaunchConfigurationDialog());

		swtLabel.addListener(SWT.FocusIn, e -> text.setFocus());

		Button browseButton = new Button(innerParent, SWT.PUSH);
		GridData projSelButtonData = new GridData();
		projSelButtonData.widthHint = calculateWidthHint(browseButton);
		projSelButtonData.horizontalAlignment = SWT.FILL;
		browseButton.setLayoutData(projSelButtonData);
		browseButton.setText("&Browse...");
		browseButton.addListener(SWT.Selection, e ->
		{
			String chosen = chooser.get();
			if (chosen != null)
				text.setText(chosen);
		});
		return text;
	}

	private static int calculateWidthHint(Control c)
	{
		int wHint = new PixelConverter(c).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(wHint, c.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	private String chooseMograsimProject()
	{
		// TODO this seems very ugly, especially hardcoded width/height
		WorkbenchLabelProvider renderer = new WorkbenchLabelProvider()
		{
			@Override
			protected ImageDescriptor decorateImage(ImageDescriptor input, Object element)
			{
				return new ImageDescriptorWithMargins(input, new Point(20, 16));
			}
		};
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), renderer);
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select a Mograsim project");
		dialog.setElements(filterOpenMograsimProjects(ResourcesPlugin.getWorkspace().getRoot().getProjects()));
		if (dialog.open() == Window.OK)
			return ((IProject) dialog.getFirstResult()).getName();
		return null;
	}

	private String chooseMPMFile()
	{
		return chooseFile("MPM", "mpm");
	}

	private String chooseMPROMFile()
	{
		return chooseFile("MPROM", "mprom");
	}

	private String chooseInitialRAMFile()
	{
		return chooseFile("Initial RAM", "mem");
	}

	private String chooseFile(String type, String fileext)
	{
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(type + " Selection");
		dialog.setMessage("Select a ." + fileext + " file");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.addFilter(new FileExtensionViewerFilter(fileext));
		dialog.addFilter(new ProjectViewerFilter(getSelectedProject()));

		if (dialog.open() == Window.OK)
			return ((IResource) dialog.getResult()[0]).getProjectRelativePath().toPortableString();
		return null;
	}

	private IProject getSelectedProject()
	{
		String projName = projSelText.getText().trim();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.validateName(projName, IResource.PROJECT).isOK())
			return workspace.getRoot().getProject(projName);
		return null;
	}

	private static IProject[] filterOpenMograsimProjects(IProject[] projects)
	{
		return Arrays.stream(projects).filter(p ->
		{
			try
			{
				return p.isAccessible() && p.hasNature(MograsimNature.NATURE_ID);
			}
			catch (CoreException e)
			{
				throw new RuntimeException(e);
			}
		}).toArray(IProject[]::new);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO don't let the user have to specify everything
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		projSelText.setText(getStringAttribSafe(configuration, MachineLaunchConfigType.PROJECT_ATTR, ""));
		mpmFileSelText.setText(getStringAttribSafe(configuration, MachineLaunchConfigType.MPM_FILE_ATTR, ""));
		mpromFileSelText.setText(getStringAttribSafe(configuration, MachineLaunchConfigType.MPROM_FILE_ATTR, ""));
		initialRAMFileSelText.setText(getStringAttribSafe(configuration, MachineLaunchConfigType.INITIAL_RAM_FILE_ATTR, ""));
	}

	private String getStringAttribSafe(ILaunchConfiguration configuration, String attrib, String defaultValue)
	{
		try
		{
			return configuration.getAttribute(attrib, defaultValue);
		}
		catch (CoreException e)
		{
			setErrorMessage(e.getStatus().getMessage());
		}
		return defaultValue;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		String projName = projSelText.getText().trim();
		String mpmFileName = mpmFileSelText.getText().trim();
		String mpromFileName = mpromFileSelText.getText().trim();
		String initialRAMFileName = initialRAMFileSelText.getText().trim();

		Set<IResource> associatedResources = new HashSet<>();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.validateName(projName, IResource.PROJECT).isOK())
		{
			IProject project = workspace.getRoot().getProject(projName);
			try
			{
				if (project != null && project.isAccessible() && project.hasNature(MograsimNature.NATURE_ID))
				{
					associatedResources.add(project);

					IResource mpmFile = project.findMember(mpmFileName);
					if (mpmFile != null && mpmFile.exists() && mpmFile.getType() == IResource.FILE)
						associatedResources.add(mpmFile);

					IResource mpromFile = project.findMember(mpromFileName);
					if (mpromFile != null && mpromFile.exists() && mpromFile.getType() == IResource.FILE)
						associatedResources.add(mpromFile);

					IResource ramFile = project.findMember(initialRAMFileName);
					if (ramFile != null && ramFile.exists() && ramFile.getType() == IResource.FILE)
						associatedResources.add(ramFile);
				}
			}
			catch (CoreException e)
			{
				setErrorMessage(e.getStatus().getMessage());
			}
		}
		configuration.setMappedResources(associatedResources.toArray(IResource[]::new));
		configuration.setAttribute(MachineLaunchConfigType.PROJECT_ATTR, projName);
		configuration.setAttribute(MachineLaunchConfigType.MPM_FILE_ATTR, mpmFileName);
		configuration.setAttribute(MachineLaunchConfigType.MPROM_FILE_ATTR, mpromFileName);
		configuration.setAttribute(MachineLaunchConfigType.INITIAL_RAM_FILE_ATTR, initialRAMFileName);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		setMessage(null);
		String projName = projSelText.getText().trim();
		if (projName.length() == 0)
			return setErrorAndReturnFalse("No project specified");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projName, IResource.PROJECT);
		if (!status.isOK())
			return setErrorAndReturnFalse("Illegal project name: {0}: {1}", projName, status.getMessage());

		IProject project = workspace.getRoot().getProject(projName);
		if (!project.exists())
			return setErrorAndReturnFalse("Project {0} does not exist", projName);
		if (!project.isOpen())
			return setErrorAndReturnFalse("Project {0} is closed", projName);
		try
		{
			if (!project.hasNature(MograsimNature.NATURE_ID))
				return setErrorAndReturnFalse("Project {0} is not a Mograsim project", projName);
		}
		catch (CoreException e)
		{
			return setErrorAndReturnFalse(e.getStatus().getMessage());
		}

		String mpmFileName = mpmFileSelText.getText().trim();
		if (mpmFileName.length() == 0)
			return setErrorAndReturnFalse("No MPM file specified");
		IResource mpmResource = project.findMember(mpmFileName);
		if (mpmResource == null || !mpmResource.exists())
			return setErrorAndReturnFalse("MPM file {0} does not exist", mpmFileName);
		if (mpmResource.getType() != IResource.FILE)
			return setErrorAndReturnFalse("MPM file {0} is not a file", mpmFileName);

		String mpromFileName = mpromFileSelText.getText().trim();
		if (mpromFileName.length() > 0)
		{
			IResource mpromResource = project.findMember(mpromFileName);
			if (mpromResource == null || !mpromResource.exists())
				return setErrorAndReturnFalse("MPROM file {0} does not exist", mpromFileName);
			if (mpromResource.getType() != IResource.FILE)
				return setErrorAndReturnFalse("MPROM file {0} is not a file", mpromFileName);
		}

		String initialRAMFileName = initialRAMFileSelText.getText().trim();
		if (initialRAMFileName.length() > 0)
		{
			IResource initialRAMResource = project.findMember(initialRAMFileName);
			if (initialRAMResource == null || !initialRAMResource.exists())
				return setErrorAndReturnFalse("Initial RAM file {0} does not exist", initialRAMFileName);
			if (initialRAMResource.getType() != IResource.FILE)
				return setErrorAndReturnFalse("Initial RAM file {0} is not a file", initialRAMFileName);
		}

		return true;
	}

	private boolean setErrorAndReturnFalse(String message, String... params)
	{
		setErrorMessage(NLS.bind(message, params));
		return false;
	}

	@Override
	public String getName()
	{
		return "Main";
	}
}