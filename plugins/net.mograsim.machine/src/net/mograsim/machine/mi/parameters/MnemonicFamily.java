package net.mograsim.machine.mi.parameters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.MachineException;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class MnemonicFamily implements ParameterClassification
{
	private final Mnemonic[] values;
	private final Mnemonic defaultValue;
	private final String[] stringValues;
	private Map<String, Mnemonic> byText;
	private int vectorLength;

	MnemonicFamily(String defaultValueName, MnemonicPair... values)
	{
		if (values.length == 0)
			throw new MachineException("Mnemonics must not be empty!");
		this.values = new Mnemonic[values.length];
		this.stringValues = new String[values.length];

		setup(values);

		// if no valid defaultValue is specified, pick first value as default
		int defaultValueIndex = 0;
		for (int i = 0; i < values.length; i++)
			if (stringValues[i].equals(defaultValueName))
			{
				defaultValueIndex = i;
				break;
			}
		this.defaultValue = this.values[defaultValueIndex];
	}

	private void setup(MnemonicPair[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			this.values[i] = createMnemonic(values[i], i);
			this.stringValues[i] = values[i].name;
		}
		if (values.length == 0)
			vectorLength = 0;
		else
		{
			vectorLength = values[0].value.length();
			for (int i = 1; i < values.length; i++)
				if (values[i].value.length() != vectorLength)
					throw new IllegalArgumentException("MnemonicFamily is not of uniform vector length!");
		}
		byText = Arrays.stream(this.values).collect(Collectors.toMap(m -> m.getText(), m -> m));
		if (values.length != byText.keySet().size())
			throw new IllegalArgumentException("MnemonicFamily contains multiple Mnemonics with the same name!");
	}

	private Mnemonic createMnemonic(MnemonicPair mnemonicPair, int ordinal)
	{
		return new Mnemonic(mnemonicPair.name, mnemonicPair.value, this, ordinal);
	}

	public Mnemonic[] values()
	{
		return values.clone();
	}

	public Mnemonic get(int ordinal)
	{
		return values[ordinal];
	}

	public Mnemonic get(String text)
	{
		return byText.get(text);
	}

	@Override
	public MicroInstructionParameter getDefault()
	{
		return defaultValue;
	}

	public boolean contains(Mnemonic m)
	{
		if (m != null)
			return m.owner == this;
		return false;
	}

	public boolean contains(String value)
	{
		return byText.keySet().contains(value);
	}

	public int size()
	{
		return values.length;
	}

	public int getVectorLength()
	{
		return vectorLength;
	}

	@Override
	public boolean conforms(MicroInstructionParameter param)
	{
		return ParameterClassification.super.conforms(param) && (param instanceof Mnemonic ? contains((Mnemonic) param) : false);
	}

	@Override
	public ParameterType getExpectedType()
	{
		return ParameterType.MNEMONIC;
	}

	@Override
	public int getExpectedBits()
	{
		return vectorLength;
	}

	public String[] getStringValues()
	{
		return stringValues.clone();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}

	@Override
	public Mnemonic parse(String toParse)
	{
		Mnemonic parsed = get(toParse);
		if (parsed == null)
			throw new UnknownMnemonicException(toParse);
		return parsed;
	}

	public static class MnemonicPair
	{
		public final String name;
		public final BitVector value;

		public MnemonicPair(String name, BitVector value)
		{
			this.name = name;
			this.value = value;
		}
	}

	public static class MnemonicFamilyBuilder
	{
		private final List<MnemonicPair> pairs;
		private final int bits;
		private String defaultValue;

		public MnemonicFamilyBuilder(int bits)
		{
			this.pairs = new LinkedList<>();
			this.bits = bits;
		}

		public MnemonicFamilyBuilder addX()
		{
			pairs.add(new MnemonicPair("X", BitVector.of(Bit.ZERO, bits)));
			return this;
		}

		public MnemonicFamilyBuilder add(MnemonicPair... pairs)
		{
			this.pairs.addAll(List.of(pairs));
			return this;
		}

		/**
		 * Adds a name with its corresponding value to the {@link MnemonicFamily}
		 * 
		 * @return this {@link MnemonicFamilyBuilder}
		 */
		public MnemonicFamilyBuilder add(String name, BitVector value)
		{
			add(new MnemonicPair(name, value));
			return this;
		}

		/**
		 * Adds names with their corresponding values to the {@link MnemonicFamily}
		 * 
		 * @param names  The names to be added to the {@link MnemonicFamily}
		 * @param values The values as {@link BitVector}s
		 * @return this {@link MnemonicFamilyBuilder}
		 */
		public MnemonicFamilyBuilder add(String name, long value)
		{
			add(new MnemonicPair(name, BitVector.from(value, bits)));
			return this;
		}

		/**
		 * Adds names with their corresponding values to the {@link MnemonicFamily}
		 * 
		 * @param names  The names to be added to the {@link MnemonicFamily}
		 * @param values The values as {@link BitVector}s
		 * @return this {@link MnemonicFamilyBuilder}
		 */
		public MnemonicFamilyBuilder add(String names[], BitVector[] values)
		{
			if (names.length != values.length)
				throw new IllegalArgumentException("Cannot add Mnemonics! Amount of names does not match amount of values!");
			for (int i = 0; i < names.length; i++)
				add(new MnemonicPair(names[i], values[i]));
			return this;
		}

		/**
		 * Adds names with their corresponding values (converted to a BitVector) to the {@link MnemonicFamily}
		 * 
		 * @param names  The names to be added to the {@link MnemonicFamily}
		 * @param values The values to be converted to {@link BitVector}s with a given amount of {@link MnemonicFamilyBuilder#bits}
		 * @return this {@link MnemonicFamilyBuilder}
		 */
		public MnemonicFamilyBuilder add(String names[], long values[])
		{
			if (names.length != values.length)
				throw new IllegalArgumentException("Cannot add Mnemonics! Amount of names does not match amount of values!");
			for (int i = 0; i < names.length; i++)
				add(new MnemonicPair(names[i], BitVector.from(values[i], bits)));
			return this;
		}

		/**
		 * Adds names to the {@link MnemonicFamily}; The corresponding {@link BitVector} value to a name is the value of its index
		 * 
		 * @param names The names to be added to the {@link MnemonicFamily}
		 * @return this {@link MnemonicFamilyBuilder}
		 */
		public MnemonicFamilyBuilder add(String... names)
		{
			for (int i = 0; i < names.length; i++)
				add(names[i], i);
			return this;
		}

		/**
		 * Sets the name of the default {@link Mnemonic} of this {@link MnemonicFamily}
		 */
		public MnemonicFamilyBuilder setDefault(String defaultValue)
		{
			this.defaultValue = defaultValue;
			return this;
		}

		/**
		 * Sets the name of the default {@link Mnemonic} of this {@link MnemonicFamily} to "X"
		 */
		public MnemonicFamilyBuilder setXDefault()
		{
			this.defaultValue = "X";
			return this;
		}

		public MnemonicFamily build()
		{
			return new MnemonicFamily(defaultValue, pairs.toArray(new MnemonicPair[pairs.size()]));
		}
	}
}
