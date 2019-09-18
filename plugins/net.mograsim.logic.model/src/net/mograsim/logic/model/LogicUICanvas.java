package net.mograsim.logic.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.haspamelodica.swt.helper.swtobjectwrappers.Rectangle;
import net.haspamelodica.swt.helper.zoomablecanvas.ZoomableCanvas;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.model.LogicModel;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.model.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.preferences.Preferences;

/**
 * Simulation visualizer canvas.
 * 
 * @author Daniel Kirschten
 */
public class LogicUICanvas extends ZoomableCanvas
{
	private final LogicModel model;

	public LogicUICanvas(Composite parent, int style, LogicModel model)
	{
		super(parent, style, Preferences.current().getBoolean("net.mograsim.logic.model.improvetext"));

		this.model = model;

		LogicUIRenderer renderer = new LogicUIRenderer(model);
		addZoomedRenderer(gc ->
		{
			Color background = Preferences.current().getColor("net.mograsim.logic.model.color.background");
			if (background != null)
				setBackground(background);// this.setBackground, not gc.setBackground to have the background fill the canvas
			renderer.render(gc, new Rectangle(-offX / zoom, -offY / zoom, gW / zoom, gH / zoom));
		});
		model.setRedrawHandler(this::redrawThreadsafe);

		addListener(SWT.MouseDown, this::mouseDown);

		if (Preferences.current().getBoolean("net.mograsim.logic.model.debug.openhlsshell"))
			openDebugSetHighLevelStateShell(model);
	}

	private void mouseDown(Event e)
	{
		if (e.button == 1)
		{
			Point click = canvasToWorldCoords(e.x, e.y);
			for (ModelComponent component : model.getComponentsByName().values())
				if (component.getBounds().contains(click) && component.clicked(click.x, click.y))
				{
					redraw();
					break;
				}
		}
	}

	private void openDebugSetHighLevelStateShell(LogicModel model)
	{
		Shell debugShell = new Shell();
		debugShell.setLayout(new GridLayout(2, false));
		new Label(debugShell, SWT.NONE).setText("Target component: ");
		Combo componentSelector = new Combo(debugShell, SWT.DROP_DOWN | SWT.READ_ONLY);
		componentSelector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		List<ModelComponent> componentsByItemIndex = new ArrayList<>();
		List<LogicModel> models = new ArrayList<>();
		AtomicBoolean recalculateQueued = new AtomicBoolean();
		AtomicReference<Consumer<? super ModelComponent>> compAdded = new AtomicReference<>();
		AtomicReference<Consumer<? super ModelComponent>> compRemoved = new AtomicReference<>();
		compAdded.set(c -> compsChanged(compAdded.get(), compRemoved.get(), c, models, componentsByItemIndex, componentSelector, model,
				recalculateQueued, true));
		compRemoved.set(c -> compsChanged(compAdded.get(), compRemoved.get(), c, models, componentsByItemIndex, componentSelector, model,
				recalculateQueued, false));
		iterateModelTree(compAdded.get(), compRemoved.get(), model, models, true);
		debugShell.addListener(SWT.Dispose, e -> models.forEach(m ->
		{
			m.removeComponentAddedListener(compAdded.get());
			m.removeComponentRemovedListener(compRemoved.get());
		}));
		queueRecalculateComponentSelector(recalculateQueued, componentsByItemIndex, componentSelector, model);
		new Label(debugShell, SWT.NONE).setText("Target state ID: ");
		Text stateIDText = new Text(debugShell, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		stateIDText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		new Label(debugShell, SWT.NONE).setText("Value type: ");
		Composite radioGroup = new Composite(debugShell, SWT.NONE);
		radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout radioGroupLayout = new GridLayout(2, false);
		radioGroupLayout.marginHeight = 0;
		radioGroupLayout.marginWidth = 0;
		radioGroup.setLayout(radioGroupLayout);
		Button radioBit = new Button(radioGroup, SWT.RADIO);
		radioBit.setText("Single bit");
		Button radioBitVector = new Button(radioGroup, SWT.RADIO);
		radioBitVector.setText("Bitvector");
		new Label(debugShell, SWT.NONE).setText("Value string representation: \n(Bit vectors: MSBit...LSBit)");
		Text valueText = new Text(debugShell, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		valueText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Button send = new Button(debugShell, SWT.PUSH);
		send.setText("Send!");
		Button get = new Button(debugShell, SWT.PUSH);
		get.setText("Get!");
		Text output = new Text(debugShell, SWT.READ_ONLY);
		output.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		Listener sendAction = e ->
		{
			try
			{
				int componentIndex = componentSelector.getSelectionIndex();
				if (componentIndex < 0 || componentIndex >= componentsByItemIndex.size())
					throw new RuntimeException("No component selected");
				ModelComponent target = componentsByItemIndex.get(componentIndex);
				String valueString = valueText.getText();
				Object value;
				if (radioBit.getSelection())
					value = Bit.parse(valueString);
				else if (radioBitVector.getSelection())
					value = BitVector.parse(valueString);
				else
					throw new RuntimeException("No value type selected");
				target.setHighLevelState(stateIDText.getText(), value);
				output.setText("Success!");
			}
			catch (Exception x)
			{
				output.setText(x.getClass().getSimpleName() + (x.getMessage() == null ? "" : ": " + x.getMessage()));
			}
		};
		Listener getAction = e ->
		{
			try
			{
				if (componentSelector.getSelectionIndex() >= componentsByItemIndex.size())
					throw new RuntimeException("No valid component selected");
				output.setText("Success! Value: \r\n"
						+ componentsByItemIndex.get(componentSelector.getSelectionIndex()).getHighLevelState(stateIDText.getText()));
			}
			catch (Exception x)
			{
				output.setText(x.getClass().getSimpleName() + (x.getMessage() == null ? "" : ": " + x.getMessage()));
			}
		};
		send.addListener(SWT.Selection, sendAction);
		valueText.addListener(SWT.DefaultSelection, sendAction);
		get.addListener(SWT.Selection, getAction);
		stateIDText.addListener(SWT.DefaultSelection, getAction);
		debugShell.open();
	}

	private void compsChanged(Consumer<? super ModelComponent> compAdded, Consumer<? super ModelComponent> compRemoved, ModelComponent c,
			List<LogicModel> models, List<ModelComponent> componentsByItemIndex, Combo componentSelector, LogicModel model,
			AtomicBoolean recalculateQueued, boolean add)
	{
		iterateSubmodelTree(compAdded, compRemoved, c, models, add);
		queueRecalculateComponentSelector(recalculateQueued, componentsByItemIndex, componentSelector, model);
	}

	private void iterateSubmodelTree(Consumer<? super ModelComponent> compAdded, Consumer<? super ModelComponent> compRemoved,
			ModelComponent c, List<LogicModel> models, boolean add)
	{
		if (c instanceof SubmodelComponent)
			iterateModelTree(compAdded, compRemoved, ((SubmodelComponent) c).submodel, models, add);
	}

	private void iterateModelTree(Consumer<? super ModelComponent> compAdded, Consumer<? super ModelComponent> compRemoved,
			LogicModel model, List<LogicModel> models, boolean add)
	{
		if (add ^ models.contains(model))
		{
			if (add)
			{
				models.add(model);
				model.addComponentAddedListener(compAdded);
				model.addComponentRemovedListener(compRemoved);
			} else
			{
				models.remove(model);
				model.removeComponentAddedListener(compAdded);
				model.removeComponentRemovedListener(compRemoved);
			}
			for (ModelComponent c : model.getComponentsByName().values())
				iterateSubmodelTree(compAdded, compRemoved, c, models, add);
		}
	}

	private void queueRecalculateComponentSelector(AtomicBoolean recalculateQueued, List<ModelComponent> componentsByItemIndex,
			Combo componentSelector, LogicModel model)
	{
		if (recalculateQueued.compareAndSet(false, true))
			getDisplay().asyncExec(() -> recalculateComponentSelector(recalculateQueued, componentsByItemIndex, componentSelector, model));
	}

	private void recalculateComponentSelector(AtomicBoolean recalculateQueued, List<ModelComponent> componentsByItemIndex,
			Combo componentSelector, LogicModel model)
	{
		recalculateQueued.set(false);
		componentsByItemIndex.clear();
		componentSelector.setItems();
		addComponentSelectorItems(componentsByItemIndex, "", componentSelector, model);
	}

	private void addComponentSelectorItems(List<ModelComponent> componentsByItemIndex, String base, Combo componentSelector,
			LogicModel model)
	{
		model.getComponentsByName().values().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).forEach(c ->
		{
			if (!(c instanceof ModelWireCrossPoint || c instanceof SubmodelInterface))
			{
				String item = base + c.getName();
				componentsByItemIndex.add(c);
				componentSelector.add(item);
				if (c instanceof SubmodelComponent)
					addComponentSelectorItems(componentsByItemIndex, item + " -> ", componentSelector, ((SubmodelComponent) c).submodel);
			}
		});
	}
}