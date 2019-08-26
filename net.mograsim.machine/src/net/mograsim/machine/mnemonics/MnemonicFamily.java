package net.mograsim.machine.mnemonics;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MnemonicFamily
{
	private final Mnemonic[] values;
	private final Map<String, Mnemonic> byText;
	private final int vectorLenght;
	
	public MnemonicFamily(Mnemonic... values)
	{
		this.values = values;
		if(values.length == 0)
			vectorLenght = 0;
		else
		{
			vectorLenght = values[0].getVector().width();
			for(int i = 1; i < values.length; i++)
				if(values[i].getVector().width() != vectorLenght)
					throw new IllegalArgumentException("MnemonicFamily is not of uniform vector length!");
		}
		byText = Arrays.stream(values).collect(Collectors.toMap(m -> m.getText(), m -> m));
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
	
	public int size()
	{
		return values.length;
	}
	
	public int getVectorLength()
	{
		return vectorLenght;
	}
}
