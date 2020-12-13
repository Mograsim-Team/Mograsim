package net.mograsim.logic.model.verilog.converter;

import java.util.Objects;

import net.mograsim.logic.model.verilog.model.IOPort;
import net.mograsim.logic.model.verilog.model.Signal;

public class VerilogEmulatedModelPin
{
	private final IOPort verilogPort;
	private final int portIndex;
	private final PinNameBit pinbit;
	private final Type type;

	public VerilogEmulatedModelPin(IOPort verilogPort, int portIndex, PinNameBit pinbit, Type type)
	{
		this.verilogPort = Objects.requireNonNull(verilogPort);
		this.portIndex = portIndex;
		this.pinbit = Objects.requireNonNull(pinbit);
		this.type = Objects.requireNonNull(type);

		check();
	}

	private void check()
	{
		if (verilogPort.getWidth() != 2)
			throw new IllegalArgumentException("Every Verilog port has to have width 2");
		if (portIndex < 0)
			throw new IllegalArgumentException("Negative port index can't be negative");
		switch (type)
		{
		case PRE:
			if (verilogPort.getType() != Signal.Type.IO_INPUT)
				throw new IllegalArgumentException("A PRE pin has to be an input");
			break;
		case OUT:
			if (verilogPort.getType() != Signal.Type.IO_OUTPUT)
				throw new IllegalArgumentException("A OUT pin has to be an output");
			break;
		case RES:
			if (verilogPort.getType() != Signal.Type.IO_INPUT)
				throw new IllegalArgumentException("A RES pin has to be an input");
			break;
		default:
			throw new IllegalStateException("Unknown enum constant: " + type);
		}
	}

	public IOPort getVerilogPort()
	{
		return verilogPort;
	}

	public int getPortIndex()
	{
		return portIndex;
	}

	public PinNameBit getPinbit()
	{
		return pinbit;
	}

	public Type getType()
	{
		return type;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pinbit == null) ? 0 : pinbit.hashCode());
		result = prime * result + portIndex;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((verilogPort == null) ? 0 : verilogPort.hashCode());
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
		VerilogEmulatedModelPin other = (VerilogEmulatedModelPin) obj;
		if (pinbit == null)
		{
			if (other.pinbit != null)
				return false;
		} else if (!pinbit.equals(other.pinbit))
			return false;
		if (portIndex != other.portIndex)
			return false;
		if (type != other.type)
			return false;
		if (verilogPort == null)
		{
			if (other.verilogPort != null)
				return false;
		} else if (!verilogPort.equals(other.verilogPort))
			return false;
		return true;
	}

	public static enum Type
	{
		PRE, OUT, RES;
	}
}
