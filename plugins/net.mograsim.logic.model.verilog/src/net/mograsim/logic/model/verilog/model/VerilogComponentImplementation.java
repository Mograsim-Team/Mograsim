package net.mograsim.logic.model.verilog.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.mograsim.logic.model.verilog.model.Signal.Type;

public class VerilogComponentImplementation
{
	private final VerilogComponentDeclaration declaration;
	private final Set<Wire> internalWires;
	private final Set<Assign> assigns;
	private final Set<ComponentReference> subcomponents;

	public VerilogComponentImplementation(VerilogComponentDeclaration declaration, Set<Wire> internalWires, Set<Assign> assigns,
			Set<ComponentReference> subcomponents)
	{
		this.declaration = Objects.requireNonNull(declaration);
		this.internalWires = Set.copyOf(internalWires);
		this.assigns = Set.copyOf(assigns);
		this.subcomponents = Set.copyOf(subcomponents);

		check();
	}

	private void check()
	{
		Set<Signal> allSignals = new HashSet<>();
		allSignals.addAll(declaration.getIOPorts());
		allSignals.addAll(internalWires);

		Set<String> usedNames = declaration.getIOPorts().stream().map(IOPort::getName).collect(Collectors.toCollection(HashSet::new));

		for (Wire wire : internalWires)
			if (!usedNames.add(wire.getName()))
				throw new IllegalArgumentException("Name occurs twice: " + wire.getName());

		for (Assign assign : assigns)
			if (!allSignals.contains(assign.getSource()) || !allSignals.contains(assign.getTarget()))
				throw new IllegalArgumentException("Referenced an unknown signal: " + assign.getSource());

		for (ComponentReference subcomponent : subcomponents)
			if (!usedNames.add(subcomponent.getName()))
				throw new IllegalArgumentException("Name occurs twice: " + subcomponent.getName());
			else if (!subcomponent.getArguments().stream().filter(s -> s.getType() != Type.CONSTANT).allMatch(allSignals::contains))
			{
				List<Signal> unknownSignals = new ArrayList<>(subcomponent.getArguments());
				unknownSignals.removeAll(allSignals);
				// we know this list contains at least one element
				throw new IllegalArgumentException("Assigning a signal not in the component: " + unknownSignals.get(0));
			}
	}

	public VerilogComponentDeclaration getDeclaration()
	{
		return declaration;
	}

	public Set<Wire> getInternalWires()
	{
		return internalWires;
	}

	public Set<Assign> getAssigns()
	{
		return assigns;
	}

	public Set<ComponentReference> getSubcomponents()
	{
		return subcomponents;
	}

	public String toVerilogCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("module " + declaration.getID());
		sb.append(declaration.getIOPorts().stream().map(IOPort::toDeclarationVerilogCode).collect(Collectors.joining(", ", "(", ")")));
		sb.append(";\n\n");

		for (Wire wire : internalWires)
			sb.append(wire.toDeclarationVerilogCode() + "\n");
		if (!internalWires.isEmpty())
			sb.append("\n");

		for (Assign assign : assigns)
			sb.append(assign.toVerilogCode() + "\n");
		if (!assigns.isEmpty())
			sb.append("\n");

		for (ComponentReference subcomponent : subcomponents)
			sb.append(subcomponent.toVerilogCode() + "\n");
		if (!subcomponents.isEmpty())
			sb.append("\n");

		sb.append("endmodule\n");

		return sb.toString();
	}

	@Override
	public String toString()
	{
		return "Implementation[" + declaration.getID() + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assigns == null) ? 0 : assigns.hashCode());
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
		result = prime * result + ((internalWires == null) ? 0 : internalWires.hashCode());
		result = prime * result + ((subcomponents == null) ? 0 : subcomponents.hashCode());
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
		VerilogComponentImplementation other = (VerilogComponentImplementation) obj;
		if (assigns == null)
		{
			if (other.assigns != null)
				return false;
		} else if (!assigns.equals(other.assigns))
			return false;
		if (declaration == null)
		{
			if (other.declaration != null)
				return false;
		} else if (!declaration.equals(other.declaration))
			return false;
		if (internalWires == null)
		{
			if (other.internalWires != null)
				return false;
		} else if (!internalWires.equals(other.internalWires))
			return false;
		if (subcomponents == null)
		{
			if (other.subcomponents != null)
				return false;
		} else if (!subcomponents.equals(other.subcomponents))
			return false;
		return true;
	}
}
