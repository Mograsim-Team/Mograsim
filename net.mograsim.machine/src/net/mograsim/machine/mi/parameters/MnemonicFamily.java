package net.mograsim.machine.mi.parameters;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import net.mograsim.logic.core.types.BitVector;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class MnemonicFamily implements ParameterClassification
{
	private final Mnemonic[] values;
	private final String[] stringValues;
	private final Map<String, Mnemonic> byText;
	private final int vectorLength;
	
	public MnemonicFamily(MnemonicPair... values)
	{
		this.values = new Mnemonic[values.length];
		this.stringValues = new String[values.length];
		
		for(int i = 0; i < values.length; i++)
		{
			this.values[i] = createMnemonic(values[i], i);
			this.stringValues[i] = values[i].name;
		}
		if(values.length == 0)
			vectorLength = 0;
		else
		{
			vectorLength = values[0].value.length();
			for(int i = 1; i < values.length; i++)
				if(values[i].value.length() != vectorLength)
					throw new IllegalArgumentException("MnemonicFamily is not of uniform vector length!");
		}
		byText = Arrays.stream(this.values).collect(Collectors.toMap(m -> m.getText(), m -> m));
		if(values.length != byText.keySet().size())
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
	
	public boolean contains(Mnemonic m)
	{
		if(m != null)
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
		if(parsed == null)
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
