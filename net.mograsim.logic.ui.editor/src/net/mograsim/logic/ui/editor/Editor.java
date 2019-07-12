package net.mograsim.logic.ui.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.haspamelodica.swt.helper.swtobjectwrappers.Point;
import net.mograsim.logic.ui.editor.handles.ComponentHandle;
import net.mograsim.logic.ui.editor.handles.Handle;
import net.mograsim.logic.ui.editor.handles.HandleManager;
import net.mograsim.logic.ui.editor.handles.PinHandle;
import net.mograsim.logic.ui.editor.states.StateManager;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.serializing.DeserializedSubmodelComponent;
import net.mograsim.logic.ui.serializing.IndirectGUIComponentCreator;

public final class Editor
{
	final Selection selection = new Selection();
	final Set<ComponentInfo> copyBuffer = new HashSet<>();
	public final DeserializedSubmodelComponent toBeEdited;
	public final HandleManager handleManager;
	final static Map<GUIComponent, String> identifierPerComponent = new HashMap<>();
	public final EditorGUI gui;
	public final StateManager stateManager;
	private final SaveLoadManager saveManager;
	Snapping snapping = Snapping.ABSOLUTE;
	private double snapX = 5, snapY = 5;
	public final DialogManager dialogManager;

	public Editor(DeserializedSubmodelComponent toBeEdited)
	{
		this.toBeEdited = toBeEdited;
		handleManager = new HandleManager(this);
		gui = new EditorGUI(this);
		stateManager = new StateManager(this);
		handleManager.init();
		saveManager = new SaveLoadManager(this);
		dialogManager = new DialogManager(gui.shell);

		toBeEdited.submodel.addComponentRemovedListener(c -> identifierPerComponent.remove(c));
		
		gui.open();
	}
	
	public ViewModelModifiable getSubmodel()
	{
		return toBeEdited.getSubmodelModifiable();
	}

	public Selection getSelection()
	{
		return selection;
	}

	//TODO: Remove this error prone method: Relative offset may change between multiple moves,
	//because Handles have different ways of responding to reqMove(...), causing strange behaviour
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
			if(cInfo.isPresent())
				copyBuffer.add(cInfo.get());
		}
	}

	public void paste(double x, double y)
	{
		selection.clear();
		for (ComponentInfo info : copyBuffer)
		{
			GUIComponent comp = addComponent(info.identifier, info.params);
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

	public void addComponent(double x, double y)
	{
		GUIComponent c = addComponent(gui.getAddListSelected(), new JsonObject());
		selection.clear();
		selection.add(handleManager.getHandle(c));
		moveSelection(x, y);
	}
	
	private GUIComponent addComponent(String identifier, JsonElement params)
	{
		GUIComponent comp = IndirectGUIComponentCreator.createComponent(toBeEdited.getSubmodelModifiable(), identifier,
				params);
		identifierPerComponent.put(comp, identifier);
		return comp;
	}
	
	public static String getIdentifier(GUIComponent c)
	{
		return identifierPerComponent.get(c);
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
			newP.x -= newP.x % snapX;
			newP.y -= newP.y % snapY;
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

	public Point getCanvasMousePosition()
	{
		//TODO
		org.eclipse.swt.graphics.Point canvasLoc = gui.logicCanvas.getLocation(),
				mouseLoc = gui.display.getCursorLocation(), shellLoc = gui.shell.getLocation();
		return new Point(mouseLoc.x - shellLoc.x - canvasLoc.x, mouseLoc.y - shellLoc.y - canvasLoc.y);
	}

	public Point getWorldMousePosition()
	{
		return gui.logicCanvas.displayToWorldCoords(getCanvasMousePosition());
	}

	public void addWire(PinHandle a, PinHandle b)
	{
		new GUIWire(toBeEdited.getSubmodelModifiable(), a.getPin(), b.getPin(), new Point[0]);
	}

	public static enum Snapping
	{
		OFF, ABSOLUTE;
	}
	
	public static void main(String[] args)
	{
		SaveLoadManager.openLoadDialog();
	}
}
