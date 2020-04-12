package net.mograsim.logic.model.editor;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.editor.handles.ComponentHandle;
import net.mograsim.logic.model.editor.handles.Handle;
import net.mograsim.logic.model.editor.handles.HandleManager;
import net.mograsim.logic.model.editor.handles.PinHandle;
import net.mograsim.logic.model.editor.states.StateManager;
import net.mograsim.logic.model.editor.ui.DialogManager;
import net.mograsim.logic.model.editor.ui.EditorGUI;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.wires.ModelWire;
import net.mograsim.logic.model.preferences.DefaultRenderPreferences;
import net.mograsim.logic.model.preferences.RenderPreferences;
import net.mograsim.logic.model.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.snippets.highlevelstatehandlers.DefaultHighLevelStateHandler;
import net.mograsim.logic.model.snippets.outlinerenderers.DefaultOutlineRenderer;
import net.mograsim.logic.model.snippets.symbolrenderers.DefaultSymbolRenderer;

public final class Editor
{
	private static final boolean FINE_SNAP = true;
	final Selection selection = new Selection();
	final Set<ComponentInfo> copyBuffer = new HashSet<>();
	public final DeserializedSubmodelComponent toBeEdited;
	public final HandleManager handleManager;
	public final EditorGUI gui;
	public final RenderPreferences renderPrefs;
	public final StateManager stateManager;
	private final SaveLoadManager saveManager;
	private Snapping snapping = Snapping.ABSOLUTE;
	private double snapX = FINE_SNAP ? 2.5 : 5, snapY = snapX;
	public final DialogManager dialogManager;
	public final EditorUserInput userInput;

	public Editor(DeserializedSubmodelComponent toBeEdited)
	{
		this.toBeEdited = toBeEdited;
		handleManager = new HandleManager(this);
		renderPrefs = new DefaultRenderPreferences();
		gui = new EditorGUI(this);
		userInput = new EditorUserInput(this);
		stateManager = new StateManager(this);
		handleManager.init();
		saveManager = new SaveLoadManager(this);
		dialogManager = new DialogManager(gui.shell);

		gui.open();
	}

	public LogicModelModifiable getSubmodel()
	{
		return toBeEdited.getSubmodelModifiable();
	}

	public Selection getSelection()
	{
		return selection;
	}

	// TODO: Remove this error prone method: Relative offset may change between multiple moves,
	// because Handles have different ways of responding to reqMove(...), causing strange behaviour
	@Deprecated
	public void moveSelection(double x, double y)
	{
		Point ref = selection.getTopLeft();
		Point snapped = new Point(x, y);
		applySnapping(snapped);

		for (Handle c : selection)
		{
			double newX, newY;
			newX = snapped.x + c.getPosX() - ref.x;
			newY = snapped.y + c.getPosY() - ref.y;
			c.reqMove(newX, newY);
		}
	}

	public void moveHandles(double x, double y, Map<Handle, Point> handleOffsetMap)
	{
		Point snapped = new Point(x, y);
		applySnapping(snapped);

		for (Handle c : handleOffsetMap.keySet())
		{
			Point offset = handleOffsetMap.get(c);
			double newX, newY;
			newX = snapped.x + offset.x;
			newY = snapped.y + offset.y;
			c.reqMove(newX, newY);
		}
	}

	public void deleteSelection()
	{
		selection.forEach(h -> h.reqDelete());
		selection.clear();
	}

	public void copy()
	{
		copyBuffer.clear();
		Point refPoint = selection.getTopLeft();
		for (Handle h : selection)
		{
			Optional<ComponentInfo> cInfo = h.reqCopy(refPoint);
			if (cInfo.isPresent())
				copyBuffer.add(cInfo.get());
		}
	}

	public void paste(double x, double y)
	{
		selection.clear();
		for (ComponentInfo info : copyBuffer)
		{
			ModelComponent comp = addComponent(info.identifier, info.params);
			ComponentHandle h = handleManager.getHandle(comp);
			h.reqMove(info.relX, info.relY);
			selection.add(h);
		}
		moveSelection(x, y);
	}

	public void save()
	{
		saveManager.save();
	}

	public void saveAs()
	{
		saveManager.openSaveAsDialog();
	}

	public void addComponent(double x, double y)
	{
		boolean successful = false;
		JsonElement params = JsonNull.INSTANCE;
		outer: while (!successful)
		{
			String selected = gui.getAddListSelected();
			try
			{
				ModelComponent c = addComponent(selected, params);
				selection.clear();
				selection.add(handleManager.getHandle(c));
				moveSelection(x, y);
				successful = true;
			}
			catch (@SuppressWarnings("unused") UnsupportedOperationException | JsonSyntaxException | NumberFormatException
					| NullPointerException e)
			{
				String result = DialogManager.openMultiLineTextDialog("Add component", "Create", "Cancel", "Parameters:");
				if (result == null)
					break outer;
				params = new JsonParser().parse(result);
			}
		}
	}

	private ModelComponent addComponent(String identifier, JsonElement params)
	{
		return IndirectModelComponentCreator.createComponent(toBeEdited.getSubmodelModifiable(), identifier, params);
	}

	public void duplicate()
	{
		copy();
		Point origin = selection.getTopLeft();
		paste(origin.x + 20, origin.y + 20);
	}

	private void applySnapping(Point newP)
	{
		switch (snapping)
		{
		case OFF:
			break;
		case ABSOLUTE:
			newP.x = (int) (newP.x / snapX + .5) * snapX;
			newP.y = (int) (newP.y / snapY + .5) * snapY;
			break;
		default:
			break;
		}
	}

	public static class ComponentInfo
	{
		public final double relX, relY;
		public final String identifier;
		public final JsonElement params;

		public ComponentInfo(double relX, double relY, String identifier, JsonElement params)
		{
			this.relX = relX;
			this.relY = relY;
			this.identifier = identifier;
			this.params = params;
		}
	}

	@SuppressWarnings("unused")
	public void addWire(PinHandle a, PinHandle b)
	{
		new ModelWire(toBeEdited.getSubmodelModifiable(), a.getPin(), b.getPin(), new Point[0]);
	}

	public static enum Snapping
	{
		OFF, ABSOLUTE;

		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	public static void main(String[] args)
	{
		Am2900Loader.setup();
		openNewEditor();
//		SaveLoadManager.openLoadDialog();
	}

	@SuppressWarnings("unused") // Editor
	public static void openNewEditor()
	{
		DeserializedSubmodelComponent toBeEdited = new DeserializedSubmodelComponent(new LogicModelModifiable(), null, null, null);
		toBeEdited.setOutlineRenderer(new DefaultOutlineRenderer(toBeEdited));
		toBeEdited.setSymbolRenderer(new DefaultSymbolRenderer(toBeEdited));
		toBeEdited.setHighLevelStateHandler(new DefaultHighLevelStateHandler());
		new Editor(toBeEdited);
	}

	public Snapping getSnapping()
	{
		return snapping;
	}

	public void setSnapping(Snapping snapping)
	{
		this.snapping = snapping;
	}
}
