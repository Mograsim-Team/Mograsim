package net.mograsim.logic.model.verilog.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.model.am2900.Am2900Loader;
import net.mograsim.logic.model.am2900.components.ModelAm2900MPROM;
import net.mograsim.logic.model.am2900.components.ModelAm2900MainMemory;
import net.mograsim.logic.model.am2900.components.ModelAm2900MicroInstructionMemory;
import net.mograsim.logic.model.model.LogicModelModifiable;
import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.model.components.Orientation;
import net.mograsim.logic.model.model.components.atomic.ModelBitDisplay;
import net.mograsim.logic.model.model.components.atomic.ModelClock;
import net.mograsim.logic.model.model.components.atomic.ModelClock.ModelClockParams;
import net.mograsim.logic.model.model.components.atomic.ModelFixedOutput;
import net.mograsim.logic.model.model.components.atomic.ModelNandGate;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer.ModelTriStateBufferParams;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogComponentDeclarationMapping;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogConverter;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;
import net.mograsim.logic.model.verilog.model.signals.IOPort;
import net.mograsim.logic.model.verilog.utils.UnionFind;

public class ExportAm2900
{
	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();
		Path target;
		String rootComponentID;
		try (Scanner sysin = new Scanner(System.in))
		{
			System.out.print("Directory to export Verilog into >");
			target = Paths.get(sysin.nextLine());
			if (!Files.exists(target))
				Files.createDirectories(target);
			else if (!Files.isDirectory(target))
				throw new IllegalArgumentException("Target exists and is not a directory");

			System.out.print("Component ID to serialize recursively >");
			rootComponentID = sysin.nextLine();
		}

		if (!Files.exists(target))
			Files.createDirectories(target);
		else if (!Files.isDirectory(target))
			throw new IllegalArgumentException("Target exists and is not a directory");

		LogicModelModifiable model = new LogicModelModifiable();

		Set<ModelComponentToVerilogComponentDeclarationMapping> atomicComponentMappings = Stream.of(//
				new ModelNandGate(model, 1), //
				new ModelFixedOutput(model, BitVector.SINGLE_0, null), //
				new ModelFixedOutput(model, BitVector.SINGLE_1, null), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(1, Orientation.RIGHT)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(1, Orientation.RIGHT_ALT)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(1, Orientation.DOWN)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(4, Orientation.RIGHT)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(12, Orientation.RIGHT_ALT)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(12, Orientation.DOWN)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(16, Orientation.LEFT)), //
				new ModelTriStateBuffer(model, new ModelTriStateBufferParams(16, Orientation.RIGHT_ALT)), //
				new ModelClock(model, new ModelClockParams(7000, Orientation.RIGHT)), //
				new ModelTextComponent(model, "A bus"), //
				new ModelTextComponent(model, "MPM addr"), //
				new ModelTextComponent(model, "D bus"), //
				new ModelAm2900MainMemory(model, null), //
				new ModelAm2900MPROM(model, null), //
				new ModelAm2900MicroInstructionMemory(model, null), //
				new ModelBitDisplay(model, 12), //
				new ModelBitDisplay(model, 16)//
		).map(c ->
		{
			String id = c.getIDForSerializing(new IdentifyParams());
			JsonElement params = c.getParamsForSerializingJSON(new IdentifyParams());
			ModelComponentToVerilogComponentDeclarationMapping generateCanonicalDeclarationMapping = ModelComponentToVerilogConverter
					.generateCanonicalDeclarationMapping(c, new UnionFind<>(), id, params,
							ModelComponentToVerilogConverter.sanitizeVerilogID("mgs_" + id + (params.isJsonNull() ? "" : "_" + params)));
			VerilogComponentDeclaration d = generateCanonicalDeclarationMapping.getVerilogComponentDeclaration();
			System.out.println("module " + d.getID() + " "
					+ d.getIOPorts().stream().map(IOPort::toDeclarationVerilogCode).collect(Collectors.joining(", ", "(", ")")) + ";");
			return generateCanonicalDeclarationMapping;
		}).collect(Collectors.toSet());

		ModelComponent root = IndirectModelComponentCreator.createComponent(model, rootComponentID);
		Set<VerilogComponentImplementation> convertResult = ModelComponentToVerilogConverter.convert(atomicComponentMappings, Set.of(root),
				"mgs_conv_");
		for (VerilogComponentImplementation convertedComponent : convertResult)
			Files.writeString(target.resolve(convertedComponent.getDeclaration().getID() + ".v"), convertedComponent.toVerilogCode());
	}
}
