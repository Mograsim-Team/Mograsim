package net.mograsim.machine.mi.parameters;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class MnemonicFamily implements ParameterClassification
{
	private final Mnemonic[] values;
	private final Mnemonic defaultValue;
	private final String[] stringValues;
	private Map<String, Mnemonic> byText;
	private int vectorLength;

	public MnemonicFamily(String defaultValueName, String... names)
	{
		this(false, defaultValueName, (int) Math.round(Math.ceil(Math.log(names.length) / Math.log(2))), names);
	}

	public MnemonicFamily(boolean reverse, String defaultValueName, String... names)
	{
		this(reverse, defaultValueName, (int) Math.round(Math.ceil(Math.log(names.length) / Math.log(2))), names);
	}

	public MnemonicFamily(String defaultValueName, int bits, String... names)
	{
		this(false, defaultValueName, bits, names);
	}

	public MnemonicFamily(boolean reverse, String defaultValueName, int bits, String... names)
	{
		this.values = new Mnemonic[names.length];
		this.stringValues = new String[names.length];
		BitVector[] values = new BitVector[names.length];
		for (int i = 0; i < names.length; i++)
		{
			values[i] = BitVector.from(i, bits);
		}

		setup(names, values, reverse);

		int defaultValueIndex = -1;
		for (int i = 0; i < names.length; i++)
			if (names[i].equals(defaultValueName))
			{
				defaultValueIndex = i;
				break;
			}
		this.defaultValue = this.values[defaultValueIndex];
	}

	public MnemonicFamily(String defaultValueName, String[] names, long[] values, int bits)
	{
		this(false, defaultValueName, names, values, bits);
	}

	public MnemonicFamily(boolean reverse, String defaultValueName, String[] names, long[] values, int bits)
	{
		if (names.length != values.length)
			throw new IllegalArgumentException();
		this.values = new Mnemonic[values.length];
		this.stringValues = new String[values.length];
		BitVector[] vectors = new BitVector[values.length];

		for (int i = 0; i < vectors.length; i++)
		{
			vectors[i] = BitVector.from(values[i], bits);
		}

		setup(names, vectors, reverse);

		int defaultValueIndex = -1;
		for (int i = 0; i < names.length; i++)
			if (names[i].equals(defaultValueName))
			{
				defaultValueIndex = i;
				break;
			}
		this.defaultValue = this.values[defaultValueIndex];
	}

	public MnemonicFamily(String defaultValueName, String[] names, BitVector[] values)
	{
		this(false, defaultValueName, names, values);
	}

	public MnemonicFamily(boolean reverse, String defaultValueName, String[] names, BitVector[] values)
	{
		if (names.length != values.length)
			throw new IllegalArgumentException();
		this.values = new Mnemonic[values.length];
		this.stringValues = new String[values.length];

		setup(names, values, reverse);

		int defaultValueIndex = -1;
		for (int i = 0; i < names.length; i++)
			if (names[i].equals(defaultValueName))
			{
				defaultValueIndex = i;
				break;
			}
		this.defaultValue = this.values[defaultValueIndex];
	}

	public MnemonicFamily(String defaultValueName, MnemonicPair... values)
	{
		this.values = new Mnemonic[values.length];
		this.stringValues = new String[values.length];

		setup(values);

		int defaultValueIndex = -1;
		for (int i = 0; i < values.length; i++)
			if (stringValues[i].equals(defaultValueName))
			{
				defaultValueIndex = i;
				break;
			}
		this.defaultValue = this.values[defaultValueIndex];
	}

	private void setup(String[] names, BitVector[] values, boolean reverse)
	{
		MnemonicPair[] mnemonics = new MnemonicPair[values.length];
		for (int i = 0; i < values.length; i++)
			mnemonics[i] = new MnemonicPair(names[i], reverse ? values[i].reverse() : values[i]);
		setup(mnemonics);
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
		else
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
}
