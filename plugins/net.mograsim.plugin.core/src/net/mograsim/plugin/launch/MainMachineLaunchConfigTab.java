package net.mograsim.plugin.launch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import net.mograsim.plugin.nature.MograsimNature;
import net.mograsim.plugin.util.ImageDescriptorWithMargins;

//a big part of this class is stolen from org.eclipse.jdt.debug.ui
public class MainMachineLaunchConfigTab extends AbstractLaunchConfigurationTab
{
	private Text projSelText;

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new FillLayout());
		Composite innerParent = new Composite(parent, SWT.NONE);
		setControl(innerParent);

		innerParent.setLayout(new GridLayout());

		Group projSelGroup = new Group(innerParent, SWT.NONE);
		projSelGroup.setLayout(new GridLayout(2, false));
		projSelGroup.setText("&Project:");
		projSelGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		projSelText = new Text(projSelGroup, SWT.BORDER);
		projSelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		projSelText.addModifyListener(e -> updateLaunchConfigurationDialog());
		Button projSelButton = new Button(projSelGroup, SWT.PUSH);
		GridData projSelButtonData = new GridData();
		projSelButtonData.widthHint = calculateWidthHint(projSelButton);
		projSelButtonData.horizontalAlignment = SWT.FILL;
		projSelButton.setLayoutData(projSelButtonData);
		projSelButton.setText("&Browse...");
		projSelButton.addListener(SWT.Selection, e ->
		{
			IProject choosedProject = chooseMograsimProject();
			if (choosedProject != null)
				projSelText.setText(choosedProject.getName());
		});

		// TODO MPM / RAM selectors
	}

	private static int calculateWidthHint(Control c)
	{
		int wHint = new PixelConverter(c).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(wHint, c.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	private IProject chooseMograsimProject()
	{
		WorkbenchLabelProvider renderer = new WorkbenchLabelProvider()
		{
			@Override
			protected ImageDescriptor decorateImage(ImageDescriptor input, Object element)
			{
				return new ImageDescriptorWithMargins(input, new Point(22, 16));
			}
		};
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), renderer);
		dialog.setTitle("title");
		dialog.setMessage("message");
		dialog.setElements(filterOpenMograsimProjects(ResourcesPlugin.getWorkspace().getRoot().getProjects()));
		if (dialog.open() == Window.OK)
			return (IProject) dialog.getFirstResult();
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
		String projName = "";
		try
		{
			projName = configuration.getAttribute(MachineLaunchConfigType.PROJECT_ATTR, "");
		}
		catch (CoreException e)
		{
			setErrorMessage(e.getStatus().getMessage());
		}
		projSelText.setText(projName);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		Set<IResource> associatedResources = new HashSet<>();
		String projName = projSelText.getText().trim();
		if (projName.length() != 0)
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(projName);
			try
			{
				if (project != null && project.isAccessible() && project.hasNature(MograsimNature.NATURE_ID))
					associatedResources.add(project);
			}
			catch (CoreException e)
			{
				setErrorMessage(e.getStatus().getMessage());
			}
		}
		configuration.setMappedResources(associatedResources.toArray(IResource[]::new));
		configuration.setAttribute(MachineLaunchConfigType.PROJECT_ATTR, projName);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		setMessage(null);
		String projName = projSelText.getText().trim();
		if (projName.length() == 0)
		{
			setErrorMessage("No project specified");
			return false;
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(projName, IResource.PROJECT);
		if (!status.isOK())
		{
			setErrorMessage(NLS.bind("Illegal project name: {0}", new String[] { status.getMessage() }));
			return false;
		}
		IProject project = workspace.getRoot().getProject(projName);
		if (!project.exists())
		{
			setErrorMessage(NLS.bind("Project {0} does not exist", new String[] { projName }));
			return false;
		}
		if (!project.isOpen())
		{
			setErrorMessage(NLS.bind("Project {0} is closed", new String[] { projName }));
			return false;
		}
		try
		{
			if (!project.hasNature(MograsimNature.NATURE_ID))
			{
				setErrorMessage(NLS.bind("Project {0} is not a Mograsim project", new String[] { projName }));
				return false;
			}
		}
		catch (CoreException e)
		{
			setErrorMessage(e.getStatus().getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String getName()
	{
		return "testlaunchconfigtabname";
	}
}