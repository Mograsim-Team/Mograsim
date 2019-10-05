package net.mograsim.plugin.views;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.IDebugContextManager;
import org.eclipse.debug.ui.contexts.IDebugContextService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.haspamelodica.swt.helper.input.DoubleInput;
import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.machine.Machine;
import net.mograsim.machine.Memory.MemoryCellModifiedListener;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.plugin.launch.MachineDebugContextListener;
import net.mograsim.plugin.launch.MachineDebugTarget;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.mi.ActiveInstructionPreviewContentProvider;
import net.mograsim.plugin.tables.mi.InstructionTable;
import net.mograsim.plugin.util.OverlappingFillLayout;
import net.mograsim.preferences.Preferences;

public class SimulationView extends ViewPart
{
	private static final int SIM_SPEED_SCALE_STEPS = 50;
	private static final double SIM_SPEED_SCALE_STEP_FACTOR = 1.32;
	private static final double SIM_SPEED_SCALE_STEP_FACTOR_LOG = Math.log(SIM_SPEED_SCALE_STEP_FACTOR);

	private final Set<Control> controlsToDisableWhenNoMachinePresent;
	private Button sbseButton;
	private Scale simSpeedScale;
	private DoubleInput simSpeedInput;
	private Composite contextDependentControlsParent;
	private Composite canvasParent;
	private InstructionTable instPreview;
	private ActiveInstructionPreviewContentProvider contentProvider;
	private Label noRunningMachineLabel;

	private MachineDebugTarget debugTarget;
	private LogicUICanvas canvas;

	private final MemoryCellModifiedListener memCellListener;
	private final LogicObserver clockObserver;
	private final MachineDebugContextListener debugContextListener;
	private final Consumer<Double> executionSpeedListener;

	public SimulationView()
	{
		controlsToDisableWhenNoMachinePresent = new HashSet<>();
		memCellListener = a -> instPreview.refresh();
		// TODO could this be a breakpoint?
		clockObserver = o ->
		{
			if (((CoreClock) o).isOn())
				SafeRunner.run(() -> debugTarget.suspend());
		};
		debugContextListener = new MachineDebugContextListener()
		{
			@Override
			public void machineDebugContextChanged(Optional<MachineDebugTarget> oldTarget, Optional<MachineDebugTarget> newTarget)
			{
				SimulationView.this.debugContextChanged(newTarget);
			}
		};
		executionSpeedListener = this::speedFactorChanged;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		// initialize UI
		parent.setLayout(new GridLayout());

		addSimulationControlWidgets(parent);

		Composite contextDependentControlsParentParent = new Composite(parent, SWT.NONE);
		contextDependentControlsParentParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		contextDependentControlsParentParent.setLayout(new OverlappingFillLayout());

		noRunningMachineLabel = new Label(contextDependentControlsParentParent, SWT.NONE);
		noRunningMachineLabel.setText("No machine running && selected in the Debug view...");

		contextDependentControlsParent = new Composite(contextDependentControlsParentParent, SWT.NONE);
		GridLayout contexDependentControlsLayout = new GridLayout();
		contexDependentControlsLayout.marginWidth = 0;
		contexDependentControlsLayout.marginHeight = 0;
		contextDependentControlsParent.setLayout(contexDependentControlsLayout);

		canvasParent = new Composite(contextDependentControlsParent, SWT.NONE);
		canvasParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		canvasParent.setLayout(new FillLayout());

		addInstructionPreviewControlWidgets(contextDependentControlsParent);

		IDebugContextManager debugCManager = DebugUITools.getDebugContextManager();
		IDebugContextService contextService = debugCManager.getContextService(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		contextService.addDebugContextListener(debugContextListener);
		debugContextListener.debugContextChanged(contextService.getActiveContext());
	}

	private void addSimulationControlWidgets(Composite parent)
	{
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		c.setLayout(new GridLayout(7, false));

		sbseButton = new Button(c, SWT.CHECK);
		controlsToDisableWhenNoMachinePresent.add(sbseButton);

		sbseButton.setText("Step by step execution");
		sbseButton.addListener(SWT.Selection, e ->
		{
			CoreClock cl = debugTarget.getMachine().getClock();
			if (sbseButton.getSelection())
				cl.registerObserver(clockObserver);
			else
				cl.deregisterObserver(clockObserver);
		});
		sbseButton.setSelection(false);

		Label simSpeedLabel = new Label(c, SWT.NONE);
		controlsToDisableWhenNoMachinePresent.add(simSpeedLabel);
		simSpeedLabel.setText("Simulation Speed: ");

		simSpeedScale = new Scale(c, SWT.NONE);
		controlsToDisableWhenNoMachinePresent.add(simSpeedScale);
		simSpeedScale.setMinimum(0);
		simSpeedScale.setMaximum(SIM_SPEED_SCALE_STEPS);
		simSpeedScale.setIncrement(1);
		simSpeedScale.setSelection(0);
		simSpeedScale.addListener(SWT.Selection, e ->
		{
			double speed = Math.pow(SIM_SPEED_SCALE_STEP_FACTOR, simSpeedScale.getSelection() - SIM_SPEED_SCALE_STEPS);
			// TODO: disable when debugTarget is not set
			debugTarget.setExecutionSpeed(speed);
		});

		simSpeedInput = new DoubleInput(c, SWT.NONE);
		controlsToDisableWhenNoMachinePresent.add(simSpeedInput);
		simSpeedInput.setPrecision(Preferences.current().getInt("net.mograsim.plugin.core.simspeedprecision"));
		simSpeedInput.addChangeListener(speed ->
		{
			if (speed != 0)
				debugTarget.setExecutionSpeed(speed);
			else
				debugTarget.setExecutionSpeed(Math.pow(10, -simSpeedInput.getPrecision()));
		});

		c.layout();
	}

	private void speedFactorChanged(double speed)
	{
		simSpeedInput.setValue(speed);
		int closestScalePos = (int) Math.round(Math.log(speed) / SIM_SPEED_SCALE_STEP_FACTOR_LOG + SIM_SPEED_SCALE_STEPS);
		simSpeedScale.setSelection(Math.min(Math.max(closestScalePos, 0), SIM_SPEED_SCALE_STEPS));
	}

	private void addInstructionPreviewControlWidgets(Composite parent)
	{
		instPreview = new InstructionTable(parent, new DisplaySettings(), getSite().getWorkbenchWindow().getWorkbench().getThemeManager());
		instPreview.getTableViewer().getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		contentProvider = new ActiveInstructionPreviewContentProvider(instPreview.getTableViewer());
		instPreview.setContentProvider(contentProvider);
	}

	private void debugContextChanged(Optional<MachineDebugTarget> newTarget)
	{
		// call binToDebugTarget even if we didn't find a selected MachineDebugTarget
		bindToDebugTarget(newTarget.orElse(null));
	}

	private void bindToDebugTarget(MachineDebugTarget debugTarget)
	{
		deregisterMachineDependentListeners();
		this.debugTarget = debugTarget;

		if (canvasParent == null)
			// createPartControls has not been called yet
			return;

		double offX;
		double offY;
		double zoom;
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

		if (debugTarget != null)
		{
			noRunningMachineLabel.setVisible(false);
			contextDependentControlsParent.setVisible(true);
			controlsToDisableWhenNoMachinePresent.forEach(c -> c.setEnabled(true));

			Machine machine = debugTarget.getMachine();

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

			// update preview
			contentProvider.setMachine(machine);

			// initialize executer
			debugTarget.addExecutionSpeedListener(executionSpeedListener);
			speedFactorChanged(debugTarget.getExecutionSpeed());
		} else
		{
			noRunningMachineLabel.setVisible(true);
			contextDependentControlsParent.setVisible(false);
			controlsToDisableWhenNoMachinePresent.forEach(c -> c.setEnabled(false));
			contentProvider.setMachine(null);
		}
	}

	private void deregisterMachineDependentListeners()
	{
		if (debugTarget != null)
		{
			debugTarget.removeExecutionSpeedListener(executionSpeedListener);
			debugTarget.getMachine().getMicroInstructionMemory().deregisterCellModifiedListener(memCellListener);
			debugTarget.getMachine().getClock().deregisterObserver(clockObserver);
			if (sbseButton != null && !sbseButton.isDisposed())
				sbseButton.setSelection(false);
		}
	}

	@Override
	public void setFocus()
	{
		if (canvas != null && !canvas.isDisposed())
			canvas.setFocus();
	}

	@Override
	public void dispose()
	{
		deregisterMachineDependentListeners();
		contentProvider.setMachine(null);
		DebugUITools.getDebugContextManager().removeDebugContextListener(debugContextListener);
		super.dispose();
	}
}