package net.mograsim.logic.model.am2900.machine.registers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import net.mograsim.machine.StandardRegister;

public class QRegister extends StandardRegister
{
	public static final QRegister instance = new QRegister();

	private QRegister()
	{
		super("qreg", new HashSet<>(Arrays.asList(new String[] { "qreg", "Q register" })), 16, new HashMap<>());
	}
}