package net.mograsim.logic.model.verilog.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VerilogComponentDeclaration
{
	private final String id;
	private final List<IOPort> ioPorts;

	public VerilogComponentDeclaration(String id, List<IOPort> ioPorts)
	{
		this.id = Objects.requireNonNull(id);
		this.ioPorts = List.copyOf(ioPorts);

		check();
	}

	private void check()
	{
		Set<String> usedNames = new HashSet<>();

		for (IOPort ioPort : ioPorts)
			if (!usedNames.add(ioPort.getName()))
				throw new IllegalArgumentException("Name occurs twice: " + ioPort.getName());
	}

	public String getID()
	{
		return id;
	}

	public List<IOPort> getIOPorts()
	{
		return ioPorts;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ioPorts == null) ? 0 : ioPorts.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return id + "[" + ioPorts.size() + "]";
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
		VerilogComponentDeclaration other = (VerilogComponentDeclaration) obj;
		if (ioPorts == null)
		{
			if (other.ioPorts != null)
				return false;
		} else if (!ioPorts.equals(other.ioPorts))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
