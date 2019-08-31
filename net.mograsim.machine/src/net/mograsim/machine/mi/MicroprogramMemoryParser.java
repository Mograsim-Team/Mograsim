package net.mograsim.machine.mi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.MemoryDefinition;
import net.mograsim.machine.mi.parameters.BooleanClassification;
import net.mograsim.machine.mi.parameters.BooleanImmediate;
import net.mograsim.machine.mi.parameters.IntegerClassification;
import net.mograsim.machine.mi.parameters.IntegerImmediate;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.MnemonicFamily;
import net.mograsim.machine.mi.parameters.MnemonicFamily.MnemonicPair;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public class MicroprogramMemoryParser
{
	public static void parse(MicroprogramMemory memory, long startAddress, MicroInstructionDefinition definition, String input) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input))))
		{
			parse(memory, startAddress, definition, reader);
		}
	}

	public static void parse(MicroprogramMemory memory, long startAddress, MicroInstructionDefinition definition,
			BufferedReader input)
	{
		MemoryDefinition def = memory.getDefinition();
		long minAddress = Long.max(startAddress, def.getMinimalAddress()), maxAddress = def.getMaximalAddress();
		try
		{
			String line;
			for (long i = minAddress; i < maxAddress && input.ready() && !"".equals((line = input.readLine())); i++)
				memory.setCell(i, parse(definition, line));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static MicroInstruction parse(MicroInstructionDefinition definition, String toParse)
	{
		int size = definition.size();
		String[] strings = toParse.split(",");
		if (size != strings.length)
			throw new IllegalArgumentException(
					"String does not match definition! The number of parameters does not match.");
		MicroInstructionParameter[] params = new MicroInstructionParameter[size];
		ParameterClassification[] classes = definition.getParameterClassifications();
		for (int i = 0; i < size; i++)
		{
			params[i] = classes[i].parse(strings[i]);
		}
		return new StandardMicroInstruction(params);
	}
	
	public static void write(MicroprogramMemory memory, long startAddress, long endAddress, String output) throws IOException
	{
		try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(output)))
		{
			write(memory, startAddress, endAddress, writer);
		}
	}
	
	public static void write(MicroprogramMemory memory, long startAddress, long endAddress, OutputStreamWriter output) throws IOException
	{
		MemoryDefinition def = memory.getDefinition();
		long min = Long.max(def.getMinimalAddress(), startAddress), max = Long.min(def.getMaximalAddress(), endAddress) + 1;
		for(long i = min; i < max; i++)
		{
			output.write(toCSV(memory.getCell(i)) + "\n");
		}
	}
	
	private static String toCSV(MicroInstruction inst)
	{
		int max = inst.getSize() - 1;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < max; i++)
		{
			sb.append(inst.getParameter(i).toString());
			sb.append(",");
		}
		sb.append(inst.getParameter(max).toString());
		return sb.toString();
	}
}
