package net.mograsim.logic.model.am2900.machine.registers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.mograsim.machine.StandardRegister;

public class NumberedRegister extends StandardRegister
{
	public static final List<NumberedRegister> instancesCorrectOrder;

	static
	{
		List<NumberedRegister> instancesCorrectOrderModifiable = new ArrayList<>();
		for (int i = 0; i < 16; i++)
			instancesCorrectOrderModifiable.add(new NumberedRegister(i));
		instancesCorrectOrder = Collections.unmodifiableList(instancesCorrectOrderModifiable);
	}

	private final int index;
	private final String indexBitstring;

	private NumberedRegister(int i)
	{
		super("R" + i, new HashSet<>(Arrays.asList(new String[] { "R" + i, "Register #" + i, "Register " + i })), 16, new HashMap<>());
		this.index = i;

		StringBuilder sb = new StringBuilder();
		sb.append((index & 0b1000) != 0 ? '1' : '0');
		sb.append((index & 0b0100) != 0 ? '1' : '0');
		sb.append((index & 0b0010) != 0 ? '1' : '0');
		sb.append((index & 0b0001) != 0 ? '1' : '0');
		this.indexBitstring = sb.toString();
	}

	public int getIndex()
	{
		return index;
	}

	public String getIndexAsBitstring()
	{
		return indexBitstring;
	}
}