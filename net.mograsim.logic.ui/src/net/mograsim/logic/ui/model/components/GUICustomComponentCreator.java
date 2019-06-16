package net.mograsim.logic.ui.model.components;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.params.GeneralComponentParams;
import net.mograsim.logic.ui.model.components.params.GeneralComponentParams.InnerComponentParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams.InnerWireParams;
import net.mograsim.logic.ui.model.components.params.SubComponentParams.InterfacePinParams;
import net.mograsim.logic.ui.model.wires.GUIWire;

public class GUICustomComponentCreator
{
	private static final String rectC = SimpleRectangularSubmodelComponent.class.getSimpleName();

	private static class CustomRectComponent extends SimpleRectangularSubmodelComponent
	{
		private String path;

		protected CustomRectComponent(ViewModelModifiable model, int logicWidth, String label, String path)
		{
			super(model, logicWidth, label);
			this.path = path;
		}

		@Override
		public String getIdentifier()
		{
			return "file:".concat(path);
		}
	}

	public static SubmodelComponent create(ViewModelModifiable model, String path)
	{
		try
		{
			SubComponentParams params = SubComponentParams.readJson(path);
			SubmodelComponent ret = create(model, params, path);
			return ret;
		}
		catch (IOException e)
		{
			System.err.println("Failed to construct GUICustomComponent. Parameters were not found.");
			e.printStackTrace();
		}
		return new CustomRectComponent(model, 0, "ERROR", "NONE");
	}

	/**
	 * @param path This value is used when the new SubmodelComponent is an inner component to a different SubmodelComponent, which is being
	 *             saved to a file; Then, the new SubmodelComponent is referenced by its given path within the file.
	 */
	public static SubmodelComponent create(ViewModelModifiable model, SubComponentParams params, String path)
	{
		// TODO: Clean up this mess
		SubmodelComponent comp = null;
		if (rectC.equals(params.type))
		{
			try
			{
				Map<String, Object> m = params.specialized;
				SimpleRectangularSubmodelComponent rect = new CustomRectComponent(model,
						((Number) m.get(SimpleRectangularSubmodelComponent.kLogicWidth)).intValue(),
						(String) m.get(SimpleRectangularSubmodelComponent.kLabel), path);
				rect.setSubmodelScale(params.composition.innerScale);
				rect.setSize(params.width, params.height);
				rect.setInputCount(((Number) m.get(SimpleRectangularSubmodelComponent.kInCount)).intValue());
				rect.setOutputCount(((Number) m.get(SimpleRectangularSubmodelComponent.kOutCount)).intValue());
				comp = rect;
			}
			catch (ClassCastException | NullPointerException e)
			{
				System.err.println("Failed to specialize component!");
				e.printStackTrace();
			}
		}
		if (comp == null)
		{
			// As SubmodelComponent is abstract, for now SubmodelComponents are instantiated as SimpleRectangularSubmodelComponents
			comp = new CustomRectComponent(model, 0, "", path);

			comp.setSubmodelScale(params.composition.innerScale);
			comp.setSize(params.width, params.height);
			for (InterfacePinParams iPinParams : params.interfacePins)
			{
				comp.addSubmodelInterface(iPinParams.logicWidth, iPinParams.location.x, iPinParams.location.y);
			}
		}
		initSubmodelComponents(comp, params.composition);
		return comp;
	}

	@SuppressWarnings("unused")
	private static void initSubmodelComponents(SubmodelComponent comp, GeneralComponentParams params)
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
		catch (Exception e)
		{
			System.err.println("Failed to create custom component!");
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
