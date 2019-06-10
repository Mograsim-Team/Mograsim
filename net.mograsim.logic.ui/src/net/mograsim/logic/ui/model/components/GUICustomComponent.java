package net.mograsim.logic.ui.model.components;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.ComponentParams.InnerComponentParams;
import net.mograsim.logic.ui.model.components.ComponentParams.InnerWireParams;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class GUICustomComponent extends SimpleRectangularSubmodelComponent
{
	private String path = "NONE";

	public static GUICustomComponent create(ViewModelModifiable model, String path)
	{
		try
		{
			ComponentParams params = ComponentParams.readJSON(path);
			return create(model, params);
		}
		catch (IOException e)
		{
			System.err.println(String.format("Failed to create custom component from invalid path: %s", path));
			e.printStackTrace();
		}
		return new GUICustomComponent(model, 0, "ERROR");
	}

	public static GUICustomComponent create(ViewModelModifiable model, ComponentParams params)
	{
		GUICustomComponent comp = new GUICustomComponent(model, params.logicWidth, params.displayName);
		comp.setSubmodelScale(params.innerScale);
		comp.setInputCount(params.inputCount);
		comp.setOutputCount(params.outputCount);
		comp.initSubmodelComponents(params);
		return comp;
	}

	private GUICustomComponent(ViewModelModifiable model, int logicWidth, String displayName)
	{
		super(model, logicWidth, displayName);
	}

	@SuppressWarnings("unused")
	private void initSubmodelComponents(ComponentParams params)
	{
		try
		{
			GUIComponent[] components = new GUIComponent[params.subComps.length];
			for (int i = 0; i < components.length; i++)
			{
				InnerComponentParams cParams = params.subComps[i];
				String path = cParams.type;
				if (path.startsWith("class:"))
				{
					path = path.substring(6);
					components[i] = createComponent(path, cParams.logicWidth);
					components[i].moveTo(cParams.pos.x, cParams.pos.y);
				} else if (path.startsWith("file:"))
				{
					path = path.substring(5);
					components[i] = create(submodelModifiable, path);
					components[i].moveTo(cParams.pos.x, cParams.pos.y);
				} else
					throw new IllegalArgumentException("Invalid submodel type! Type was neither prefixed by 'class:' nor by 'file:'");
			}

			for (int i = 0; i < params.innerWires.length; i++)
			{
				InnerWireParams innerWire = params.innerWires[i];
				new GUIWire(submodelModifiable,
						submodelModifiable.getComponents().get(innerWire.pin1.compId).getPins().get(innerWire.pin1.pinIndex),
						submodelModifiable.getComponents().get(innerWire.pin2.compId).getPins().get(innerWire.pin2.pinIndex),
						innerWire.path);
			}
		}
		catch (Exception e)
		{
			System.err.println("Failed to create custom component!");
			e.printStackTrace();
		}
	}

	private GUIComponent createComponent(String classname, int logicWidth) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		Class<?> c = Class.forName(classname);
		Object comp;
		try
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class);
			comp = constructor.newInstance(submodelModifiable);
		}
		catch (@SuppressWarnings("unused") NoSuchMethodException e)
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class, int.class);
			comp = constructor.newInstance(submodelModifiable, logicWidth);
		}

		if (comp instanceof GUIComponent)
			return (GUIComponent) comp;
		throw new IllegalArgumentException("Class given as subcomponent was not a GUIComponent!");
	}

	public String getPath()
	{
		return path;
	}
}
