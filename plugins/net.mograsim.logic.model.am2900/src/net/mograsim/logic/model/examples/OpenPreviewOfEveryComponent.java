package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;

import net.mograsim.logic.model.SimpleLogicUIStandalone;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.util.JsonHandler;

public class OpenPreviewOfEveryComponent
{
	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();

		List<String> allComponents = new ArrayList<>();

		// TODO this is ugly. Is there a better way?
		try (InputStream resourceAsStream = Am2900Loader.class.getResourceAsStream("standardComponentIDMapping.json"))
		{
			allComponents.addAll(JsonHandler.readJson(resourceAsStream, JsonObject.class).keySet());
		}
		try (InputStream resourceAsStream = IndirectModelComponentCreator.class.getResourceAsStream("standardComponentIDMapping.json"))
		{
			allComponents.addAll(JsonHandler.readJson(resourceAsStream, JsonObject.class).keySet());
		}

		Collections.sort(allComponents, String::compareToIgnoreCase);

		SimpleLogicUIStandalone.executeVisualisation(model ->
		{
			int y = 0;
			for (String componentID : allComponents)
				try
				{
					ModelComponent createComponent = IndirectModelComponentCreator.createComponent(model, componentID);
					createComponent.moveTo(0, y);
					new ModelTextComponent(model, componentID).moveTo(createComponent.getWidth() + 10, y);
					y += createComponent.getHeight() + 10;
				}
				catch (RuntimeException e)
				{
					new ModelTextComponent(model, "Error creating " + componentID + ": " + e).moveTo(0, y);
					y += 20;
				}
		});
	}
}
