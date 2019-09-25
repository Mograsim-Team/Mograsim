package net.mograsim.plugin.editors;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.machine.Machine;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.ProjectMachineContext;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.mi.ActiveInstructionPreviewContentProvider;
import net.mograsim.plugin.tables.mi.InstructionTable;

//TODO what if we open multiple editors?
//TODO actually save / load register and latch states
public class SimulationViewEditor extends EditorPart
{
	private MachineContext context;

	private LogicExecuter exec;
	private Machine machine;

	private Composite parent;
	private Button sbseButton;
	private Button pauseButton;
	private Slider simSpeedSlider;
	private LogicUICanvas canvas;
	private Label noMachineLabel;

	private MemoryCellModifiedListener currentRegisteredCellListener;

	@Override
	public void createPartControl(Composite parent)
	{
		this.parent = parent;
		// initialize UI
		parent.setLayout(new GridLayout());

		noMachineLabel = new Label(parent, SWT.NONE);
		noMachineLabel.setText("No machine present...");// TODO internationalize?
		addSimulationControlWidgets(parent);
		recreateContextDependentControls();
	}

	private void recreateContextDependentControls()
	{
		if (parent == null)
			// createPartControls has not been called yet
			return;

		if (canvas != null)
			canvas.dispose();
		if (exec != null)
			exec.stopLiveExecution();

		Optional<Machine> machineOptional;
		if (context != null && (machineOptional = context.getActiveMachine()).isPresent())
		{
			noMachineLabel.setVisible(false);
			sbseButton.setEnabled(true);
			pauseButton.setEnabled(true);
			simSpeedSlider.setEnabled(true);

			if (machine != null)
				machine.getMicroInstructionMemory().deregisterCellModifiedListener(currentRegisteredCellListener);

			machine = machineOptional.get();
			canvas = new LogicUICanvas(parent, SWT.NONE, machine.getModel());
			canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(canvas);
			userInput.buttonDrag = 3;
			userInput.buttonZoom = 2;
			userInput.enableUserInput();

			// initialize Instruction preview
			InstructionTable instPreview = new InstructionTable(parent, new DisplaySettings());
			instPreview.getTableViewer().getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			instPreview.setContentProvider(new ActiveInstructionPreviewContentProvider(instPreview.getTableViewer()));
			AssignableMicroInstructionMemory mIMemory = machine.getMicroInstructionMemory();
			instPreview.bindMicroInstructionMemory(mIMemory);
			currentRegisteredCellListener = a -> instPreview.refresh();
			mIMemory.registerCellModifiedListener(currentRegisteredCellListener);

			GridData uiData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
			canvas.setLayoutData(uiData);

			// initialize executer
			exec = new LogicExecuter(machine.getTimeline());
			exec.startLiveExecution();
		} else
		{
			noMachineLabel.setVisible(true);
			sbseButton.setEnabled(false);
			pauseButton.setEnabled(false);
			simSpeedSlider.setEnabled(false);
		}
	}

	private void addSimulationControlWidgets(Composite parent)
	{
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(7, false));

		sbseButton = new Button(c, SWT.CHECK);
		pauseButton = new Button(c, SWT.TOGGLE);
		LogicObserver clockObserver = o ->
		{
			if (((CoreClock) o).isOn())
			{
				exec.pauseLiveExecution();
				if (!pauseButton.isDisposed())
					Display.getDefault().asyncExec(() ->
					{
						if (!pauseButton.isDisposed())
							pauseButton.setSelection(false);
						setPauseText(pauseButton, false);
					});
			}
		};

		sbseButton.setText("Step by step execution");
		sbseButton.addListener(SWT.Selection, e ->
		{
			CoreClock cl = machine.getClock();
			if (sbseButton.getSelection())
				cl.registerObserver(clockObserver);
			else
				cl.deregisterObserver(clockObserver);
		});
		sbseButton.setSelection(false);

		pauseButton.setSelection(true);
		setPauseText(pauseButton, false);

		pauseButton.addListener(SWT.Selection, e ->
		{
			setPauseText(pauseButton, false);
			if (pauseButton.getSelection())
			{
				exec.unpauseLiveExecution();
			} else
			{
				exec.pauseLiveExecution();
			}
		});
		pauseButton.addMouseTrackListener(new MouseTrackListener()
		{
			@Override
			public void mouseHover(MouseEvent e)
			{
				// nothing
			}

			@Override
			public void mouseExit(MouseEvent e)
			{
				setPauseText(pauseButton, false);
			}

			@Override
			public void mouseEnter(MouseEvent e)
			{
				setPauseText(pauseButton, true);
			}
		});

		Label speedLabel = new Label(c, SWT.NONE);
		speedLabel.setText("Simulation Speed: ");

		simSpeedSlider = new Slider(c, SWT.NONE);
		simSpeedSlider.setMinimum(1);
		simSpeedSlider.setMaximum(100 + simSpeedSlider.getThumb());
		simSpeedSlider.setIncrement(1);

		Label speedPercentageLabel = new Label(c, SWT.NONE);
		speedPercentageLabel.setText("100%");

		simSpeedSlider.addListener(SWT.Selection, e ->
		{
			int selection = simSpeedSlider.getSelection();
			speedPercentageLabel.setText(selection + "%");

			exec.setSpeedPercentage(simSpeedSlider.getSelection());
		});
		simSpeedSlider.setSelection(100);

		c.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		c.pack();
	}

	private static void setPauseText(Button pauseButton, boolean hovered)
	{
		if (hovered)
			if (pauseButton.getSelection())
				pauseButton.setText("Pause?");
			else
				pauseButton.setText("Resume?");
		else if (pauseButton.getSelection())
			pauseButton.setText("Running");
		else
			pauseButton.setText("Paused");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) input;
			context = ProjectMachineContext.getMachineContextOf(fileInput.getFile().getProject());
			recreateContextDependentControls();

			setPartName(fileInput.getName());
			open(fileInput.getFile());
		} else
			throw new IllegalArgumentException("SimulationViewEditor can only be used with Files");

		setSite(site);
		setInput(input);
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput)
			SafeRunnable.getRunner().run(() -> save(((IFileEditorInput) input).getFile(), monitor));
		else
			throw new IllegalArgumentException("SimulationViewEditor can only be used with Files");
	}

	private void save(IFile file, IProgressMonitor monitor) throws CoreException
	{
		file.setContents(new ByteArrayInputStream("actual contents will go here".getBytes()), 0, monitor);
	}

	private void open(IFile file)
	{
		// do nothing yet
	}

	@Override
	public void doSaveAs()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void setFocus()
	{
		canvas.setFocus();
	}

	@Override
	public void dispose()
	{
		exec.stopLiveExecution();
		super.dispose();
	}
}