package net.mograsim.logic.model.am2900.machine.registers.am2910.stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mograsim.logic.model.am2900.machine.registers.Am2900Register;
import net.mograsim.machine.registers.HighLevelStateBasedRegister;

public class StackCellRegister extends HighLevelStateBasedRegister implements Am2900Register
{
	public static final List<StackCellRegister> instancesCorrectOrder;

	static
	{
		List<StackCellRegister> instancesCorrectOrderModifiable = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			instancesCorrectOrderModifiable.add(new StackCellRegister(i));
		instancesCorrectOrder = Collections.unmodifiableList(instancesCorrectOrderModifiable);
	}

	private StackCellRegister(int index)
	{
		super("cell #" + index, "am2910.stack.c" + getIndexAsBitstring(index) + ".q", 12);

	}

	private static String getIndexAsBitstring(int index)
	{
		StringBuilder sb = new StringBuilder();
		sb.append((index & 0b100) != 0 ? '1' : '0');
		sb.append((index & 0b010) != 0 ? '1' : '0');
		sb.append((index & 0b001) != 0 ? '1' : '0');
		return sb.toString();
	}
}