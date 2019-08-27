package net.mograsim.machine.mi.parameters;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import net.mograsim.machine.mi.parameters.MicroInstructionParameter.ParameterType;

public class MnemonicFamily implements ParameterClassification
{
	private final Mnemonic[] values;
	private final Map<String, Mnemonic> byText;
	private final int vectorLength;
	
	public MnemonicFamily(Mnemonic... values)
	{
		this.values = values;
		if(values.length == 0)
			vectorLength = 0;
		else
		{
			vectorLength = values[0].getValue().width();
			for(int i = 1; i < values.length; i++)
				if(values[i].getValue().width() != vectorLength)
					throw new IllegalArgumentException("MnemonicFamily is not of uniform vector length!");
		}
		byText = Arrays.stream(values).collect(Collectors.toMap(m -> m.getText(), m -> m));
		if(values.length != byText.keySet().size())
			throw new IllegalArgumentException("MnemonicFamily contains multiple Mnemonics with the same name!");
	}
	
	public Mnemonic[] getValues()
	{
		return values.clone();
	}
	
	public Mnemonic get(String text)
	{
		return byText.get(text);
	}
	
	public boolean contains(Mnemonic m)
	{
		if(m != null)
			return m.equals(byText.get(m.getText()));
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
		return byText.keySet().toArray(new String[values.length]);
	}
}
