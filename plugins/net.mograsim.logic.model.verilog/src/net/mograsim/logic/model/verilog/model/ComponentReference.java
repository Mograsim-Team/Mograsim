package net.mograsim.logic.model.verilog.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComponentReference
{
	private final String name;
	private final VerilogComponentDeclaration referencedComponent;
	private final List<Signal> arguments;

	public ComponentReference(String name, VerilogComponentDeclaration referencedComponent, List<Signal> arguments)
	{
		this.name = Objects.requireNonNull(name);
		this.referencedComponent = Objects.requireNonNull(referencedComponent);
		this.arguments = List.copyOf(arguments);

		check();
	}

	private void check()
	{
		List<IOPort> ioPorts = referencedComponent.getIOPorts();
		if (ioPorts.size() != arguments.size())
			throw new IllegalArgumentException(
					"Incorrect nubmer of arguments given: " + arguments.size() + ", but should be " + ioPorts.size());

		for (int i = 0; i < ioPorts.size(); i++)
			if (ioPorts.get(i).getWidth() != arguments.get(i).getWidth())
				throw new IllegalArgumentException("Argument #" + i + "(" + ioPorts.get(i) + "): Incorrect width: "
						+ arguments.get(i).getWidth() + ", but shoud be " + ioPorts.get(i).getWidth());
	}

	public String getName()
	{
		return name;
	}

	public VerilogComponentDeclaration getReferencedComponent()
	{
		return referencedComponent;
	}

	public List<Signal> getArguments()
	{
		return arguments;
	}

	public String toVerilogCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(referencedComponent.getID() + " " + name);
		sb.append(arguments.stream().map(Signal::toReferenceVerilogCode).collect(Collectors.joining(", ", "(", ")")));
		sb.append(";");

		return sb.toString();
	}

	@Override
	public String toString()
	{
		return name + "[" + referencedComponent.getID() + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((referencedComponent == null) ? 0 : referencedComponent.hashCode());
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
		ComponentReference other = (ComponentReference) obj;
		if (arguments == null)
		{
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (referencedComponent == null)
		{
			if (other.referencedComponent != null)
				return false;
		} else if (!referencedComponent.equals(other.referencedComponent))
			return false;
		return true;
	}
}
