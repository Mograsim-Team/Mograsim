package net.mograsim.logic.model.am2900.machine.registers.am2901;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberedRegister extends Am2901Register
{
	public static final List<NumberedRegister> instancesCorrectOrder;

	static
	{
		List<NumberedRegister> instancesCorrectOrderModifiable = new ArrayList<>();
		for (int i = 0; i < 16; i++)
			instancesCorrectOrderModifiable.add(new NumberedRegister(i));
		instancesCorrectOrder = Collections.unmodifiableList(instancesCorrectOrderModifiable);
	}

	private NumberedRegister(int index)
	{
		super("R" + index, "regs.c" + getIndexAsBitstring(index) + ".q", 4);

	}

	private static String getIndexAsBitstring(int index)
	{
		StringBuilder sb = new StringBuilder();
		sb.append((index & 0b1000) != 0 ? '1' : '0');
		sb.append((index & 0b0100) != 0 ? '1' : '0');
		sb.append((index & 0b0010) != 0 ? '1' : '0');
		sb.append((index & 0b0001) != 0 ? '1' : '0');
		return sb.toString();
	}
}