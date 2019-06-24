package net.mograsim.logic.ui.model.components;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.wires.WireCrossPoint;
import net.mograsim.logic.ui.util.JsonHandler;

public class GUIComponentCreator
{
	private final static Map<String, String> componentMapping;
	private final static String componentMappingPath = "../net.mograsim.logic.ui.am2900/components/mapping.json"; // TODO: manage this
																													// somewhere else

	static
	{
		Map<String, String> tmp;
		try
		{
			tmp = JsonHandler.readJson(componentMappingPath, Map.class);
		}
		catch (IOException e)
		{
			System.err.println("Failed to initialize component mapping; Components cannot be created from file.");
			e.printStackTrace();
			tmp = new HashMap<>();
		}
		componentMapping = tmp;
	}

	public static GUIComponent create(ViewModelModifiable model, String name, Map<String, Object> params)
	{
		try
		{
			String path = componentMapping.get(name);
			if (path.startsWith("class:"))
			{
				path = path.substring(6);
				return createComponentFromClass(model, path, params);
			} else if (path.startsWith("file:"))
			{
				path = path.substring(5);
				return GUICustomComponentCreator.create(model, path);
			} else
				throw new IllegalArgumentException("Invalid submodel type! Type was neither prefixed by 'class:' nor by 'file:'");
		}
		catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | IllegalArgumentException e)
		{
			System.err.println("Failed to create requested component!");
			e.printStackTrace();
			return new SimpleRectangularSubmodelComponent(model, 1, "ERROR");
		}
	}

	private static GUIComponent createComponentFromClass(ViewModelModifiable model, String classname, Map<String, Object> params)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException,
			ClassNotFoundException
	{
		Class<?> c = Class.forName(classname);
		Object comp;
		if (SimpleRectangularGUIGate.class.isAssignableFrom(c) || WireCrossPoint.class.equals(c))
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class, int.class);
			comp = constructor.newInstance(model, ((Number) params.get(SimpleRectangularGUIGate.kLogicWidth)).intValue());
		} else
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class);
			comp = constructor.newInstance(model);
		}
		return (GUIComponent) comp;
	}
}
