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
import net.mograsim.logic.model.model.components.atomic.ModelSplitter;
import net.mograsim.logic.model.model.components.atomic.ModelSplitter.SplitterParams;
import net.mograsim.logic.model.model.components.atomic.ModelTextComponent;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer;
import net.mograsim.logic.model.model.components.atomic.ModelTriStateBuffer.ModelTriStateBufferParams;
import net.mograsim.logic.model.model.wires.ModelWireCrossPoint;
import net.mograsim.logic.model.serializing.IdentifyParams;
import net.mograsim.logic.model.serializing.IndirectModelComponentCreator;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogComponentDeclarationMapping;
import net.mograsim.logic.model.verilog.converter.ModelComponentToVerilogConverter;
import net.mograsim.logic.model.verilog.model.VerilogComponentImplementation;

public class ExportAm2900
{
	public static void main(String[] args) throws IOException
	{
		Am2900Loader.setup();
		try (Scanner sysin = new Scanner(System.in))
		{
			System.out.print("Directory to export Verilog into >");
			Path target = Paths.get(sysin.nextLine());
			if (!Files.exists(target))
				Files.createDirectories(target);
			else if (!Files.isDirectory(target))
				throw new IllegalArgumentException("Target exists and is not a directory");

			System.out.print("Component ID to serialize recursively >");
			String rootComponentID = sysin.nextLine();
			{
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
						new ModelWireCrossPoint(model, 1), //
						new ModelWireCrossPoint(model, 2), //
						new ModelWireCrossPoint(model, 4), //
						new ModelWireCrossPoint(model, 9), //
						new ModelWireCrossPoint(model, 12), //
						new ModelWireCrossPoint(model, 16), //
						new ModelSplitter(model, new SplitterParams(2, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(2, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(2, Orientation.UP)), //
						new ModelSplitter(model, new SplitterParams(2, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(3, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(3, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(3, Orientation.UP)), //
						new ModelSplitter(model, new SplitterParams(3, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(3, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(4, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(4, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(4, Orientation.UP)), //
						new ModelSplitter(model, new SplitterParams(4, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(4, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(5, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(5, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(6, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(6, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(6, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(6, Orientation.UP)), //
						new ModelSplitter(model, new SplitterParams(6, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(8, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(8, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(9, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(9, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(12, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(12, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(12, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(12, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(13, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(13, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(16, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(16, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(16, Orientation.UP)), //
						new ModelSplitter(model, new SplitterParams(16, Orientation.DOWN)), //
						new ModelSplitter(model, new SplitterParams(16, Orientation.DOWN_ALT)), //
						new ModelSplitter(model, new SplitterParams(80, Orientation.LEFT)), //
						new ModelSplitter(model, new SplitterParams(80, Orientation.RIGHT)), //
						new ModelSplitter(model, new SplitterParams(80, Orientation.UP_ALT)), //
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
							.generateCanonicalDeclarationMapping(c, id, params, ModelComponentToVerilogConverter
									.sanitizeVerilogID("mgs_" + id + (params.isJsonNull() ? "" : "_" + params)));
					return generateCanonicalDeclarationMapping;
				}).collect(Collectors.toSet());

				ModelComponent root = IndirectModelComponentCreator.createComponent(model, rootComponentID);
				Set<VerilogComponentImplementation> convertResult = ModelComponentToVerilogConverter.convert(atomicComponentMappings,
						Set.of(root), "mgs_conv_");
				for (VerilogComponentImplementation convertedComponent : convertResult)
					Files.writeString(target.resolve(convertedComponent.getDeclaration().getID() + ".v"),
							convertedComponent.toVerilogCode());
			}
		}
	}
}
