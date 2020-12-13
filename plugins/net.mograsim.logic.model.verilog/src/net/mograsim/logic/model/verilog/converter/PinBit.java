package net.mograsim.logic.model.verilog.converter;

import java.util.Objects;

import net.mograsim.logic.model.model.wires.Pin;

public class PinBit
{
	private final Pin pin;
	private final int bit;

	public PinBit(Pin pin, int bit)
	{
		this.pin = Objects.requireNonNull(pin);
		this.bit = bit;

		check();
	}

	private void check()
	{
		if (bit < 0 || bit >= pin.logicWidth)
			throw new IllegalArgumentException("Bit out of range for pin " + pin + ": " + bit);
	}

	public Pin getPin()
	{
		return pin;
	}

	public int getBit()
	{
		return bit;
	}

	public PinNameBit toPinNameBit()
	{
		return new PinNameBit(pin.name, bit);
	}

	@Override
	public String toString()
	{
		return pin + "[" + bit + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + bit;
		result = prime * result + ((pin == null) ? 0 : pin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PinBit other = (PinBit) obj;
		if (bit != other.bit)
			return false;
		if (pin == null)
		{
			if (other.pin != null)
				return false;
		} else if (!pin.equals(other.pin))
			return false;
		return true;
	}
}
