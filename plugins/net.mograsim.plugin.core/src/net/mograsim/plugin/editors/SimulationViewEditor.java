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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import net.haspamelodica.swt.helper.input.DoubleInput;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.machine.Machine;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.plugin.nature.MachineContext;
import net.mograsim.plugin.nature.MachineContext.ActiveMachineListener;
import net.mograsim.plugin.nature.ProjectMachineContext;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.mi.ActiveInstructionPreviewContentProvider;
import net.mograsim.plugin.tables.mi.InstructionTable;
import net.mograsim.preferences.Preferences;

//TODO what if we open multiple editors?
//TODO actually save / load register and latch states
public class SimulationViewEditor extends EditorPart
{
	private static final int SIM_SPEED_SCALE_STEPS = 50;
	private static final double SIM_SPEED_SCALE_STEP_FACTOR = 1.32;
	private static final double SIM_SPEED_SCALE_STEP_FACTOR_LOG = Math.log(SIM_SPEED_SCALE_STEP_FACTOR);

	private MachineContext context;

	private LogicExecuter exec;
	private Machine machine;

	private Composite parent;
	private Button resetButton;
	private Button sbseButton;
	private Button pauseButton;
	private Scale simSpeedScale;
	private DoubleInput simSpeedInput;
	private Composite canvasParent;
	private LogicUICanvas canvas;
	private InstructionTable instPreview;
	private Label noMachineLabel;

	private ActiveMachineListener activeMachineListener;
	private MemoryCellModifiedListener memCellListener;
	private LogicObserver clockObserver;

	public SimulationViewEditor()
	{
		activeMachineListener = m -> recreateContextDependentControls();
		memCellListener = a -> instPreview.refresh();
		clockObserver = o ->
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
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.parent = parent;
		// initialize UI
		parent.setLayout(new GridLayout());

		noMachineLabel = new Label(parent, SWT.NONE);
		noMachineLabel.setText("No machine present...");// TODO internationalize?
		addSimulationControlWidgets(parent);
		canvasParent = new Composite(parent, SWT.NONE);
		canvasParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		canvasParent.setLayout(new FillLayout());
		addInstructionPreviewControlWidgets(parent);
		recreateContextDependentControls();
	}

	private void recreateContextDependentControls()
	{
		if (parent == null)
			// createPartControls has not been called yet
			return;

		double offX;
		double offY;
		double zoom;
		stopExecAndDeregisterContextDependentListeners();
		if (canvas != null)
		{
			offX = canvas.getOffX();
			offY = canvas.getOffY();
			zoom = canvas.getZoom();
			canvas.dispose();
		} else
		{
			offX = 0;
			offY = 0;
			zoom = -1;
		}

		Optional<Machine> machineOptional;
		if (context != null && (machineOptional = context.getActiveMachine()).isPresent())
		{
			noMachineLabel.setVisible(false);
			resetButton.setEnabled(true);
			sbseButton.setEnabled(true);
			pauseButton.setEnabled(true);
			simSpeedScale.setEnabled(true);
			simSpeedInput.setEnabled(true);

			machine = machineOptional.get();
			canvas = new LogicUICanvas(canvasParent, SWT.NONE, machine.getModel());
			canvas.addListener(SWT.MouseDown, e -> canvas.setFocus());
			ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(canvas);
			userInput.buttonDrag = Preferences.current().getInt("net.mograsim.logic.model.button.drag");
			userInput.buttonZoom = Preferences.current().getInt("net.mograsim.logic.model.button.zoom");
			userInput.enableUserInput();
			if (zoom > 0)
			{
				canvas.moveTo(offX, offY, zoom);
				canvas.commitTransform();
			}

			AssignableMicroInstructionMemory mIMemory = machine.getMicroInstructionMemory();
			instPreview.bindMicroInstructionMemory(mIMemory);
			mIMemory.registerCellModifiedListener(memCellListener);

			canvasParent.layout();

			// initialize executer
			exec = new LogicExecuter(machine.getTimeline());
			updateSpeedFactorFromInput(simSpeedInput.getValue());
			updatePausedState();
			exec.startLiveExecution();
		} else
		{
			noMachineLabel.setVisible(true);
			resetButton.setEnabled(false);
			sbseButton.setEnabled(false);
			pauseButton.setEnabled(false);
			simSpeedScale.setEnabled(false);
			simSpeedInput.setEnabled(false);
		}
	}

	private void stopExecAndDeregisterContextDependentListeners()
	{
		if (exec != null)
			exec.stopLiveExecution();
		if (machine != null)
		{
			machine.getMicroInstructionMemory().deregisterCellModifiedListener(memCellListener);
			machine.getClock().deregisterObserver(clockObserver);
		}
	}

	private void addSimulationControlWidgets(Composite parent)
	{
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		c.setLayout(new GridLayout(6, false));

		resetButton = new Button(c, SWT.PUSH);
		resetButton.setText("Reset machine");
		resetButton.addListener(SWT.Selection, e -> context.getActiveMachine().get().reset());

		sbseButton = new Button(c, SWT.CHECK);
		pauseButton = new Button(c, SWT.TOGGLE);

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

		pauseButton.addListener(SWT.Selection, e -> updatePausedState());
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

		new Label(c, SWT.NONE).setText("Simulation Speed: ");

		simSpeedScale = new Scale(c, SWT.NONE);
		simSpeedScale.setMinimum(0);
		simSpeedScale.setMaximum(SIM_SPEED_SCALE_STEPS);
		simSpeedScale.setIncrement(1);
		simSpeedScale.setSelection(0);
		simSpeedScale.addListener(SWT.Selection, e -> updateSpeedFactorFromScale());

		simSpeedInput = new DoubleInput(c, SWT.NONE);
		simSpeedInput.setPrecision(Preferences.current().getInt("net.mograsim.plugin.core.simspeedprecision"));
		simSpeedInput.addChangeListener(this::updateSpeedFactorFromInput);

		updateSpeedFactorFromScale();

		c.layout();
	}

	private void updatePausedState()
	{
		setPauseText(pauseButton, false);
		if (exec != null)
			if (pauseButton.getSelection())
				exec.unpauseLiveExecution();
			else
				exec.pauseLiveExecution();
	}

	private void updateSpeedFactorFromScale()
	{
		double factor = Math.pow(SIM_SPEED_SCALE_STEP_FACTOR, simSpeedScale.getSelection() - SIM_SPEED_SCALE_STEPS);
		simSpeedInput.setValue(factor);
		if (exec != null)
			exec.setSpeedFactor(factor);
	}

	private void updateSpeedFactorFromInput(double factor)
	{
		double factorCheckedFor0;
		if (factor != 0)
			factorCheckedFor0 = factor;
		else
		{
			factorCheckedFor0 = Math.pow(10, -simSpeedInput.getPrecision());
			simSpeedInput.setValue(factorCheckedFor0);
		}
		int closestScalePos = (int) Math.round(Math.log(factorCheckedFor0) / SIM_SPEED_SCALE_STEP_FACTOR_LOG + SIM_SPEED_SCALE_STEPS);
		simSpeedScale.setSelection(Math.min(Math.max(closestScalePos, 0), SIM_SPEED_SCALE_STEPS));
		if (exec != null)
			exec.setSpeedFactor(factorCheckedFor0);
	}

	private void addInstructionPreviewControlWidgets(Composite parent)
	{
		instPreview = new InstructionTable(parent, new DisplaySettings());
		instPreview.getTableViewer().getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		instPreview.setContentProvider(new ActiveInstructionPreviewContentProvider(instPreview.getTableViewer()));
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
			context.activateMachine();
			context.addActiveMachineListener(activeMachineListener);
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
		stopExecAndDeregisterContextDependentListeners();
		context.removeActiveMachineListener(activeMachineListener);
		super.dispose();
	}
}