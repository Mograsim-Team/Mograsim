package net.mograsim.logic.model.am2900.machine.registers.am2901;

public class QRegister extends Am2901Register
{
	public static final QRegister instance = new QRegister();

	private QRegister()
	{
		super("Q", "qreg.q", 4);
	}
}