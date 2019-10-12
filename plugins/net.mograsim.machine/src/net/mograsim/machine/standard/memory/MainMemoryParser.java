package net.mograsim.machine.standard.memory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.MainMemory;
import net.mograsim.machine.MainMemoryDefinition;

public class MainMemoryParser
{
	private final static String lineSeparator = System.getProperty("line.separator");

	public static void parseMemory(final MainMemory memory, String inputPath) throws IOException
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
	public static MainMemory parseMemory(MainMemoryDefinition memDef, InputStream input) throws IOException
	{
		try
		{
			MainMemory memory = new WordAddressableMemory(memDef);
			parseMemory(memory, input);
			return memory;
		}
		catch (NullPointerException e)
		{
			throw new MainMemoryParseException(e);
		}
	}

	/**
	 *
	 * @param input The input to parse must be in csv format; The stream is closed after being consumed.
	 * 
	 * @throws IOException
	 */
	public static void parseMemory(final MainMemory memory, InputStream input) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
		{
			MainMemoryDefinition def = memory.getDefinition();

			long minAddr = def.getMinimalAddress();
			long maxAddr = def.getMaximalAddress();

			String line;
			long i = minAddr;
			try
			{
				for (; i <= maxAddr && reader.ready() && !"".equals((line = reader.readLine())); i++)
				{
					memory.setCell(i, BitVector.parseBitstring(line));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static InputStream write(MainMemory memory)
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
					instStream = new ByteArrayInputStream((memory.getCell(instIndex++).toBitstring() + lineSeparator).getBytes());
					val = instStream.read();
				}
				return val;
			}
		};
	}
}
