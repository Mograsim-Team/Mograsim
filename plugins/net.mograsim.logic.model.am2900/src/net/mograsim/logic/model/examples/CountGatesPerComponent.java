package net.mograsim.logic.model.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.modeladapter.CoreModelParameters;
import net.mograsim.logic.model.modeladapter.LogicCoreAdapter;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;

public class CountGatesPerComponent
{
	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();

		Path root = Path.of("components");
		Files.walk(root).map(Path::toString).filter(s -> s.endsWith(".json")).map("jsonfile:"::concat)
				.forEach(CountGatesPerComponent::printGatesPerComponent);
	}

	private static void printGatesPerComponent(String componentID)
	{
		LogicModelModifiable model = new LogicModelModifiable();
		IndirectModelComponentCreator.createComponent(model, componentID);

		LogicCoreAdapter.gateCountsPerComponentClass.clear();
		LogicCoreAdapter.convert(model, CoreModelParameters.builder().build());

		System.out.println(componentID + ':');
		for (Entry<Class<? extends ModelComponent>, Integer> e : LogicCoreAdapter.gateCountsPerComponentClass.entrySet())
			System.out.println("  " + e.getKey().getSimpleName() + ": " + e.getValue());
		System.out.println();
	}
}