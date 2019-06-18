package net.mograsim.logic.ui.model.components;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.ComponentCompositionParams.InnerComponentParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.ComponentCompositionParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.InnerWireParams;
import net.mograsim.logic.ui.model.components.SubmodelComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.model.wires.GUIWire;

/**
 * Creates {@link SubmodelComponent}s from {@link SubmodelComponentParams}
 */
public final class GUICustomComponentCreator
{
	private static final String rectC = SimpleRectangularSubmodelComponent.class.getSimpleName();

	/**
	 * Creates a {@link SubmodelComponent} from the {@link SubmodelComponentParams}, specified at the given path. The returned
	 * SubmodelComponent can also be e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the
	 * {@link SubmodelComponentParams} describe.
	 * 
	 * @param path The path of the file describing the {@link SubmodelComponentParams}, which define the new {@link SubmodelComponent}
	 * @return A new SubmodelComponent, as described in the file located at the given path
	 */
	public static SubmodelComponent create(ViewModelModifiable model, String path)
	{
		try
		{
			SubmodelComponentParams params = SubmodelComponentParams.readJson(path);
			SubmodelComponent ret = create(model, params, path);
			return ret;
		}
		catch (IOException e)
		{
			System.err.println("Failed to construct GUICustomComponent. Parameters were not found.");
			e.printStackTrace();
		}
		return new SimpleRectangularSubmodelComponent(model, 0, "ERROR");
	}

	/**
	 * Creates a {@link SubmodelComponent} from the specified {@link SubmodelComponentParams}. The returned SubmodelComponent can also be
	 * e.g. a {@link SimpleRectangularSubmodelComponent}, depending on what the {@link SubmodelComponentParams} describe.
	 * 
	 * @param params The parameters describing the {@link SubmodelComponent}
	 * @param path   This value is used when the new {@link SubmodelComponent} is an inner component to a different SubmodelComponent, which
	 *               is being saved to a file; Then, the new {@link SubmodelComponent} is referenced by its given path within the file.
	 * @return A new SubmodelComponent, as described by the {@link SubmodelComponentParams}
	 */
	public static SubmodelComponent create(ViewModelModifiable model, SubmodelComponentParams params, String path)
	{
		SubmodelComponent comp = null;
		if (rectC.equals(params.type))
		{
			comp = createRectComponent(model, params);
		}

		if (comp == null)
		{
			comp = createSubmodelComponent(model, params);
		}
		comp.identifierDelegate = () -> "file:".concat(path);
		initInnerComponents(comp, params.composition);
		return comp;
	}

	// May return null
	private static SimpleRectangularSubmodelComponent createRectComponent(ViewModelModifiable model, SubmodelComponentParams params)
	{
		try
		{
			Map<String, Object> m = params.specialized;
			SimpleRectangularSubmodelComponent rect = new SimpleRectangularSubmodelComponent(model,
					((Number) m.get(SimpleRectangularSubmodelComponent.kLogicWidth)).intValue(),
					(String) m.get(SimpleRectangularSubmodelComponent.kLabel));
			rect.setSubmodelScale(params.composition.innerScale);
			rect.setSize(params.width, params.height);
			rect.setInputCount(((Number) m.get(SimpleRectangularSubmodelComponent.kInCount)).intValue());
			rect.setOutputCount(((Number) m.get(SimpleRectangularSubmodelComponent.kOutCount)).intValue());
			return rect;
		}
		catch (ClassCastException | NullPointerException e)
		{
			System.err.println("Failed to specialize component!");
			e.printStackTrace();
			return null;
		}
	}

	private static SubmodelComponent createSubmodelComponent(ViewModelModifiable model, SubmodelComponentParams params)
	{
		// As SubmodelComponent is abstract, for now SubmodelComponents are instantiated as SimpleRectangularSubmodelComponents
		SubmodelComponent comp = new SimpleRectangularSubmodelComponent(model, 0, "");
		comp.setSubmodelScale(params.composition.innerScale);
		comp.setSize(params.width, params.height);
		for (InterfacePinParams iPinParams : params.interfacePins)
		{
			comp.addSubmodelInterface(iPinParams.name, iPinParams.logicWidth, iPinParams.location.x, iPinParams.location.y);
		}
		return comp;
	}

	@SuppressWarnings("unused")
	private static void initInnerComponents(SubmodelComponent comp, ComponentCompositionParams params)
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
					components[i] = createInnerComponentFromClass(comp, path, cParams.logicWidth);
					components[i].moveTo(cParams.pos.x, cParams.pos.y);
				} else if (path.startsWith("file:"))
				{
					path = path.substring(5);
					components[i] = create(comp.submodelModifiable, path);
					components[i].moveTo(cParams.pos.x, cParams.pos.y);
				} else
					throw new IllegalArgumentException("Invalid submodel type! Type was neither prefixed by 'class:' nor by 'file:'");
			}

			for (int i = 0; i < params.innerWires.length; i++)
			{
				InnerWireParams innerWire = params.innerWires[i];
				new GUIWire(comp.submodelModifiable,
						comp.submodelModifiable.getComponents().get(innerWire.pin1.compId).getPins().get(innerWire.pin1.pinIndex),
						comp.submodelModifiable.getComponents().get(innerWire.pin2.compId).getPins().get(innerWire.pin2.pinIndex),
						innerWire.path);
			}
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException | IllegalArgumentException e)
		{
			System.err.println("Failed to initialize custom component!");
			e.printStackTrace();
		}
	}

	private static GUIComponent createInnerComponentFromClass(SubmodelComponent parent, String classname, int logicWidth)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		Class<?> c = Class.forName(classname);
		Object comp;
		try
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class);
			comp = constructor.newInstance(parent.submodelModifiable);
		}
		catch (@SuppressWarnings("unused") NoSuchMethodException e)
		{
			Constructor<?> constructor = c.getConstructor(ViewModelModifiable.class, int.class);
			comp = constructor.newInstance(parent.submodelModifiable, logicWidth);
		}

		if (comp instanceof GUIComponent)
			return (GUIComponent) comp;
		throw new IllegalArgumentException("Class given as subcomponent was not a GUIComponent!");
	}
}
