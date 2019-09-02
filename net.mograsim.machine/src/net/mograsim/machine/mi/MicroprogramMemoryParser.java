package net.mograsim.machine.mi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
	public static MicroprogramMemory parseMemory(MicroInstructionDefinition definition, String input) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input))))
		{
			return parseMemory(definition, reader);
		}
	}

	public static MicroprogramMemory parseMemory(MicroInstructionDefinition definition, BufferedReader input)
	{
		List<MicroInstruction> instructions = new ArrayList<>();
		try
		{
			String line;
			while (input.ready() && !"".equals((line = input.readLine())))
				instructions.add(parse(definition, line));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		int maxAddress = instructions.size() - 1;
		MicroprogramMemory memory = MicroprogramMemory
				.create(MemoryDefinition.create((int) Math.ceil(Math.log(maxAddress)), 0, maxAddress));
		int i = 0;
		for (MicroInstruction inst : instructions)
			memory.setCell(i++, inst);
		return memory;
	}

	public static MicroInstruction parse(MicroInstructionDefinition definition, String toParse)
	{
		int size = definition.size();
		String[] strings = toParse.split(",");
		if (size != strings.length)
			throw new MicroprogramMemoryParseException(
					"String does not match definition! The number of parameters does not match.");
		MicroInstructionParameter[] params = new MicroInstructionParameter[size];
		ParameterClassification[] classes = definition.getParameterClassifications();
		try
		{
			for (int i = 0; i < size; i++)
			{
				params[i] = classes[i].parse(strings[i]);
			}
			return new StandardMicroInstruction(params);
		} catch (Exception e)
		{
			throw new MicroprogramMemoryParseException(e.getCause());
		}
	}

	public static void write(MicroprogramMemory memory, String output) throws IOException
	{
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(output)))
		{
			write(memory, writer);
		}
	}

	public static void write(MicroprogramMemory memory, OutputStreamWriter output) throws IOException
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

	public static void main(String[] args)
	{
		MnemonicFamily family = new MnemonicFamily(new MnemonicPair("ZERO", BitVector.SINGLE_0),
				new MnemonicPair("ONE", BitVector.SINGLE_1));
		MicroInstructionDefinition def = MicroInstructionDefinition.create(new BooleanClassification(),
				new IntegerClassification(8), family);
		MicroprogramMemory memory = new StandardMicroprogramMemory(MemoryDefinition.create(4, 0, 16));
		for (int i = 0; i < 17; i++)
			memory.setCell(i, new StandardMicroInstruction(new BooleanImmediate(false),
					new IntegerImmediate(BigInteger.valueOf(i), 8), family.get(i % 2)));
		try
		{
			write(memory, "test.txt");
			MicroprogramMemory newMemory = parseMemory(def, "test.txt");
			write(newMemory, "test2.txt");
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
