package net.mograsim.plugin.tables.mi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.IDebugContextManager;
import org.eclipse.debug.ui.contexts.IDebugContextService;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import net.mograsim.machine.Machine;
import net.mograsim.machine.Machine.ActiveMicroInstructionChangedListener;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.MicroInstructionMemory;
import net.mograsim.machine.mi.MicroInstructionMemoryParseException;
import net.mograsim.machine.mi.MicroInstructionMemoryParser;
import net.mograsim.plugin.launch.MachineDebugContextListener;
import net.mograsim.plugin.launch.MachineDebugTarget;
import net.mograsim.plugin.launch.MachineLaunchConfigType.MachineLaunchParams;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.ProjectMachineContext;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.RadixSelector;

public class InstructionView extends EditorPart
{
	private InstructionTableContentProvider provider;
	private boolean dirty = false;
	private MicroInstructionMemory memory;
	private InstructionTable table;
	private MachineContext context;

	private IFile file;

	// Listeners
	private MemoryCellModifiedListener cellModifiedListener = address ->
	{
		setDirty(true);
		table.refresh();
	};

	private ActiveMicroInstructionChangedListener instChangeListener = (oldAddress, newAddress) ->
	{
		highlight((int) (newAddress - memory.getDefinition().getMinimalAddress()));
	};

	private MachineDebugContextListener debugContextListener = new MachineDebugContextListener()
	{
		@Override
		public void machineDebugContextChanged(Optional<MachineDebugTarget> oldTarget, Optional<MachineDebugTarget> newTarget)
		{
			instChangeListener.instructionChanged(-1, -1);
			oldTarget.ifPresent(target -> target.getMachine().removeActiveMicroInstructionChangedListener(instChangeListener));

			newTarget.ifPresent(target ->
			{
				MachineLaunchParams params = target.getLaunchParams();
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(params.getProjectPath());

				if (file.equals(project.getFile(params.getMpmPath())))
				{
					Machine m = target.getMachine();
					target.getMachine().addActiveMicroInstructionChangedListener(instChangeListener);
					instChangeListener.instructionChanged(-1, m.getActiveMicroInstructionAddress());
				}
			});
		}
	};

	@SuppressWarnings("unused")
	@Override
	public void createPartControl(Composite parent)
	{
		provider = new InstructionTableLazyContentProvider();
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		DisplaySettings displaySettings = new DisplaySettings();
		new RadixSelector(parent, displaySettings);

		table = new InstructionTable(parent, displaySettings, getSite().getWorkbenchWindow().getWorkbench().getThemeManager());
		table.setContentProvider(provider);
		table.bindMicroInstructionMemory(memory);

		GridData viewerData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		viewerData.horizontalSpan = 3;
		table.getTableViewer().getTable().setLayoutData(viewerData);

		IDebugContextManager debugCManager = DebugUITools.getDebugContextManager();
		IDebugContextService contextService = debugCManager.getContextService(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		contextService.addDebugContextListener(debugContextListener);
		debugContextListener.debugContextChanged(contextService.getActiveContext());
		parent.addDisposeListener(e -> contextService.removeDebugContextListener(debugContextListener));
	}

	public void highlight(int row)
	{
		table.highlight(row);
	}

	public void bindMicroInstructionMemory(MicroInstructionMemory memory)
	{
		if (this.memory != null)
		{
			this.memory.deregisterCellModifiedListener(cellModifiedListener);
		}
		this.memory = memory;
		if (memory != null)
		{
			this.memory.registerCellModifiedListener(cellModifiedListener);
		}
		if (table != null)
			table.bindMicroInstructionMemory(memory);
	}

	private void open(IFile file) throws IOException, MicroInstructionMemoryParseException, CoreException
	{
		bindMicroInstructionMemory(MicroInstructionMemoryParser.parseMemory(
				context.getMachineDefinition().orElseThrow(() -> new MicroInstructionMemoryParseException("No MachineDefinition assigned!"))
						.getMicroInstructionMemoryDefinition(),
				file.getContents()));
	}

	private void save(IFile file, IProgressMonitor progressMonitor) throws IOException, CoreException, MicroInstructionMemoryParseException
	{
		if (memory == null)
		{
			throw new MicroInstructionMemoryParseException(
					"Failed to write MicroprogrammingMemory to File. No MicroprogrammingMemory assigned.");
		}
		try (InputStream toWrite = MicroInstructionMemoryParser.write(memory))
		{
			file.setContents(toWrite, 0, progressMonitor);
		}
	}

	@Override
	public void setFocus()
	{
		table.getTableViewer().getControl().setFocus();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput pathInput = (IFileEditorInput) input;
			try
			{
				save(pathInput.getFile(), progressMonitor);
				setDirty(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressMonitor.setCanceled(true);
			}
		} else
			progressMonitor.setCanceled(true);
	}

	@Override
	public void doSaveAs()
	{
//		openSaveAsDialog();
	}

//	private void openSaveAsDialog()
//	{
//		FileDialog d = new FileDialog(table.getTableViewer().getTable().getShell(), SWT.SAVE);
//		d.open();
//		String filename = d.getFileName();
//		if (!filename.equals(""))
//		{
//			save(d.getFilterPath() + File.separator + filename);
//			setDirty(false);
//		}
//	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		try
		{
			if (input instanceof IFileEditorInput)
			{
				IFileEditorInput fileInput = (IFileEditorInput) input;
				file = fileInput.getFile();
				context = ProjectMachineContext.getMachineContextOf(file.getProject());

				setPartName(fileInput.getName());
				open(file);
			} else
				throw new IllegalArgumentException("Expected IFileEditorInput!");
		}
		catch (Exception e)
		{
			throw new PartInitException("Failed to read input!", e);
		}
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	private void setDirty(boolean value)
	{
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void dispose()
	{
		memory.deregisterCellModifiedListener(cellModifiedListener);
		super.dispose();
	}

	public IFile getFile()
	{
		return file;
	}
}
