package net.mograsim.plugin.views;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.haspamelodica.swt.helper.zoomablecanvas.helper.ZoomableCanvasUserInput;
import net.mograsim.logic.core.LogicObserver;
import net.mograsim.logic.core.components.CoreClock;
import net.mograsim.logic.model.LogicExecuter;
import net.mograsim.logic.model.LogicUICanvas;
import net.mograsim.machine.Machine;
import net.mograsim.machine.mi.AssignableMicroInstructionMemory;
import net.mograsim.plugin.EclipsePreferences;
import net.mograsim.plugin.MachineContext;
import net.mograsim.plugin.MograsimActivator;
import net.mograsim.plugin.tables.DisplaySettings;
import net.mograsim.plugin.tables.mi.ActiveInstructionPreviewContentProvider;
import net.mograsim.plugin.tables.mi.InstructionTable;
import net.mograsim.preferences.Preferences;

public class LogicUIPart extends ViewPart
{
	@Inject
	private MPart part;

	private LogicExecuter exec;
	private LogicUICanvas ui;
	private Machine m;

	@Override
	public void dispose()
	{
		if (exec != null)
			exec.stopLiveExecution();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		// set preferences
		Preferences.setPreferences(new EclipsePreferences(PlatformUI.getWorkbench().getThemeManager().getCurrentTheme(),
				MograsimActivator.instance().getPreferenceStore()));

		m = MachineContext.getInstance().getMachine();

		// initialize UI
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);

		addSimulationControlWidgets(parent);

		ui = new LogicUICanvas(parent, SWT.NONE, m.getModel());
		ui.addTransformListener((x, y, z) -> part.setDirty(z < 1));
		ZoomableCanvasUserInput userInput = new ZoomableCanvasUserInput(ui);
		userInput.buttonDrag = 3;
		userInput.buttonZoom = 2;
		userInput.enableUserInput();

		GridData uiData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		ui.setLayoutData(uiData);

		// initialize Instruction preview
		InstructionTable instPreview = new InstructionTable(parent, new DisplaySettings());
		instPreview.setContentProvider(new ActiveInstructionPreviewContentProvider(instPreview.getTableViewer()));
		AssignableMicroInstructionMemory mIMemory = m.getMicroInstructionMemory();
		instPreview.bindMicroInstructionMemory(mIMemory);
		mIMemory.registerCellModifiedListener(a -> instPreview.refresh());
		mIMemory.registerMemoryReassignedListener(n -> instPreview.refresh());

		GridData previewData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		instPreview.getTableViewer().getTable().setLayoutData(previewData);

		// initialize executer
		exec = new LogicExecuter(m.getTimeline());

		// run it
		exec.startLiveExecution();
	}

	private void addSimulationControlWidgets(Composite parent)
	{
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(5, false));
		Button sbseButton = new Button(c, SWT.CHECK);
		Button pauseButton = new Button(c, SWT.TOGGLE);

		LogicObserver clockObserver = o ->
		{
			if (((CoreClock) o).isOn())
			{
				exec.pauseLiveExecution();
				Display.getDefault().asyncExec(() ->
				{
					pauseButton.setSelection(false);
					setPauseText(pauseButton, false);
				});
			}
		};

		sbseButton.addListener(SWT.Selection, e ->
		{
			String statusString = "disabled";
			CoreClock cl = m.getClock();
			if (sbseButton.getSelection())
			{
				cl.registerObserver(clockObserver);
				statusString = "enabled";
			} else
				cl.deregisterObserver(clockObserver);
			sbseButton.setToolTipText(String.format("Step by step execution: %s", statusString));
		});
		sbseButton.setSelection(false);

		pauseButton.setSelection(true);
		setPauseText(pauseButton, false);

		pauseButton.addListener(SWT.Selection, e ->
		{
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

		Slider slider = new Slider(c, SWT.NONE);
		slider.setMinimum(1);
		slider.setMaximum(100 + slider.getThumb());
		slider.setIncrement(1);

		Label speedPercentageLabel = new Label(c, SWT.NONE);
		speedPercentageLabel.setText("100%");

		slider.addListener(SWT.Selection, e ->
		{
			int selection = slider.getSelection();
			speedPercentageLabel.setText(selection + "%");

			exec.setSpeedPercentage(slider.getSelection());
		});
		slider.setSelection(100);

		c.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		c.pack();
		c.setVisible(true);
	}

	private static void setPauseText(Button pauseButton, boolean hovered)
	{
		if (hovered)
		{
			if (pauseButton.getSelection())
			{
				pauseButton.setText("Pause?");
			} else
			{
				pauseButton.setText("Resume?");
			}
		} else
		{
			if (pauseButton.getSelection())
			{
				pauseButton.setText("Running");
			} else
			{
				pauseButton.setText("Paused");
			}
		}
	}

	@Override
	public void setFocus()
	{
		ui.setFocus();
	}
}