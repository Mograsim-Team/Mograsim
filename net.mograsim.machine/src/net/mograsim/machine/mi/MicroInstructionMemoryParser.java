package net.mograsim.machine.mi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.mograsim.machine.MemoryDefinition;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public class MicroInstructionMemoryParser
{
	public static void parseMemory(final MicroInstructionMemory memory, String inputPath) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath))))
		{
			parseMemory(memory, reader);
		}
	}

	public static void parseMemory(final MicroInstructionMemory memory, BufferedReader input)
	{
		MicroInstructionMemoryDefinition def = memory.getDefinition();
		MicroInstructionDefinition miDef = def.getMicroInstructionDefinition();

		long minAddr = def.getMinimalAddress();
		long maxAddr = def.getMaximalAddress();

		String line;
		long i = minAddr;
		try
		{
			for (; i <= maxAddr && input.ready() && !"".equals((line = input.readLine())); i++)
				memory.setCell(i, parse(miDef, line));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for (; i <= maxAddr; i++)
			memory.setCell(i, miDef.createDefaultInstruction());
	}

	public static MicroInstruction parse(MicroInstructionDefinition definition, String toParse)
	{
		int size = definition.size();
		String[] strings = toParse.split(",");
		if (size != strings.length)
			throw new MicroInstructionMemoryParseException("String does not match definition! The number of parameters does not match.");
		MicroInstructionParameter[] params = new MicroInstructionParameter[size];
		ParameterClassification[] classes = definition.getParameterClassifications();
		try
		{
			for (int i = 0; i < size; i++)
			{
				params[i] = classes[i].parse(strings[i]);
			}
			return new StandardMicroInstruction(params);
		}
		catch (Exception e)
		{
			throw new MicroInstructionMemoryParseException(e.getCause());
		}
	}

	public static void write(MicroInstructionMemory memory, String outputPath) throws IOException
	{
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath)))
		{
			write(memory, writer);
		}
	}

	public static void write(MicroInstructionMemory memory, OutputStreamWriter output) throws IOException
	{
		MemoryDefinition def = memory.getDefinition();
		long min = def.getMinimalAddress(), max = def.getMaximalAddress() + 1;
		for (long i = min; i < max; i++)
		{
			output.write(toCSV(memory.getCell(i)) + "\n");
		}
	}

	private static String toCSV(MicroInstruction inst)
	{
		int max = inst.getSize() - 1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < max; i++)
		{
			sb.append(inst.getParameter(i).toString());
			sb.append(",");
		}
		sb.append(inst.getParameter(max).toString());
		return sb.toString();
	}
}
