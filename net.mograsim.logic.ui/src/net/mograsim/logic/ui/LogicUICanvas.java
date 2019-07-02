package net.mograsim.logic.ui;

import java.util.ArrayList;
import java.util.List;

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
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelComponent;
import net.mograsim.logic.ui.model.components.submodels.SubmodelInterface;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.preferences.Preferences;

/**
 * Simulation visualizer canvas.
 * 
 * @author Daniel Kirschten
 */
public class LogicUICanvas extends ZoomableCanvas
{
	private static final boolean OPEN_DEBUG_SETHIGHLEVELSTATE_SHELL = false;

	private final ViewModel model;

	public LogicUICanvas(Composite parent, int style, ViewModel model)
	{
		super(parent, style);

		this.model = model;

		LogicUIRenderer renderer = new LogicUIRenderer(model);
		addZoomedRenderer(gc ->
		{
			Color background = Preferences.current().getColor("net.mograsim.logic.ui.color.background");
			if (background != null)
				setBackground(background);// this.setBackground, not gc.setBackground to have the background fill the canvas
			renderer.render(gc, new Rectangle(-offX / zoom, -offY / zoom, gW / zoom, gH / zoom));
		});
		model.addRedrawListener(this::redrawThreadsafe);

		addListener(SWT.MouseDown, this::mouseDown);

		if (OPEN_DEBUG_SETHIGHLEVELSTATE_SHELL)
			openDebugSetHighLevelStateShell(model);
	}

	private void mouseDown(Event e)
	{
		if (e.button == 1)
		{
			Point click = displayToWorldCoords(e.x, e.y);
			for (GUIComponent component : model.getComponents())
				if (component.getBounds().contains(click) && component.clicked(click.x, click.y))
				{
					redraw();
					break;
				}
		}
	}

	private void openDebugSetHighLevelStateShell(ViewModel model)
	{
		Shell debugShell = new Shell();
		debugShell.setLayout(new GridLayout(2, false));
		new Label(debugShell, SWT.NONE).setText("Target component: ");
		Combo componentSelector = new Combo(debugShell, SWT.DROP_DOWN | SWT.READ_ONLY);
		componentSelector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		List<GUIComponent> componentsByItemIndex = new ArrayList<>();
		model.addComponentAddedListener(c -> recalculateComponentSelector(componentsByItemIndex, componentSelector, model));
		model.addComponentRemovedListener(c -> recalculateComponentSelector(componentsByItemIndex, componentSelector, model));
		recalculateComponentSelector(componentsByItemIndex, componentSelector, model);
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
				GUIComponent target = componentsByItemIndex.get(componentIndex);
				String valueString = valueText.getText();
				Object value;
				if (radioBit.getSelection())
					value = Bit.parse(valueString);
				else if (radioBitVector.getSelection())
					value = BitVector.parseMSBFirst(valueString);
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

	private void recalculateComponentSelector(List<GUIComponent> componentsByItemIndex, Combo componentSelector, ViewModel model)
	{
		componentsByItemIndex.clear();
		componentSelector.setItems();
		addComponentSelectorItems(componentsByItemIndex, "", componentSelector, model);
	}

	private void addComponentSelectorItems(List<GUIComponent> componentsByItemIndex, String base, Combo componentSelector, ViewModel model)
	{
		for (GUIComponent c : model.getComponents())
			if (!(c instanceof WireCrossPoint || c instanceof SubmodelInterface))
			{
				String item = base + c.getIdentifier();
				componentsByItemIndex.add(c);
				componentSelector.add(item);
				if (c instanceof SubmodelComponent)
					addComponentSelectorItems(componentsByItemIndex, item + " -> ", componentSelector, ((SubmodelComponent) c).submodel);
			}
	}

}