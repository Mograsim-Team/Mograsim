package net.mograsim.machine.mi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.ParameterClassification;

public class MicroInstructionMemoryParser
{
	private final static String lineSeparator = System.getProperty("line.separator");

	public static void parseMemory(final MicroInstructionMemory memory, String inputPath) throws IOException
	{
		try (InputStream input = new FileInputStream(inputPath))
		{
			parseMemory(memory, input);
		}
	}

	/**
	 * @param input The input to parse must be in csv format; The stream is closed after being consumed.
	 * 
	 * @throws IOException
	 */
	public static MicroInstructionMemory parseMemory(MicroInstructionMemoryDefinition memDef, InputStream input) throws IOException
	{
		try
		{
			MicroInstructionMemory memory = new StandardMicroInstructionMemory(memDef);
			parseMemory(memory, input);
			return memory;
		}
		catch (NullPointerException e)
		{
			throw new MicroInstructionMemoryParseException(e);
		}
	}

	/**
	 *
	 * @param input The input to parse must be in csv format; The stream is closed after being consumed.
	 * 
	 * @throws IOException
	 */
	public static void parseMemory(final MicroInstructionMemory memory, InputStream input) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
		{
			MicroInstructionMemoryDefinition def = memory.getDefinition();
			MicroInstructionDefinition miDef = def.getMicroInstructionDefinition();

			long minAddr = def.getMinimalAddress();
			long maxAddr = def.getMaximalAddress();

			String line;
			long i = minAddr;
			try
			{
				for (; i <= maxAddr && reader.ready() && !"".equals((line = reader.readLine())); i++)
				{
					memory.setCell(i, parse(miDef, line));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			for (; i <= maxAddr; i++)
			{
				memory.setCell(i, miDef.createDefaultInstruction());
			}
		}
	}

	/**
	 * must be in csv format
	 */
	public static MicroInstruction parse(MicroInstructionDefinition definition, String input)
	{
		int size = definition.size();
		String[] strings = input.split(",");
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
			throw new MicroInstructionMemoryParseException(e);
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

	public static InputStream write(MicroInstructionMemory memory)
	{
		return new InputStream()
		{
			long instIndex = memory.getDefinition().getMinimalAddress(), maxAddress = memory.getDefinition().getMaximalAddress();
			InputStream instStream = new ByteArrayInputStream(new byte[0]);

			@Override
			public int read() throws IOException
			{
				int val = instStream.read();
				if (val == -1 && instIndex <= maxAddress)
				{
					instStream = new ByteArrayInputStream((toCSV(memory.getCell(instIndex++)) + lineSeparator).getBytes());
					val = instStream.read();
				}
				return val;
			}
		};
	}
}
