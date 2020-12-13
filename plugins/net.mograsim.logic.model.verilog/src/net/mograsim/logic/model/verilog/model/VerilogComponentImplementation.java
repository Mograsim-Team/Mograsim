package net.mograsim.logic.model.verilog.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.mograsim.logic.model.verilog.model.signals.IOPort;
import net.mograsim.logic.model.verilog.model.signals.Signal;
import net.mograsim.logic.model.verilog.model.statements.Statement;

public class VerilogComponentImplementation
{
	private final VerilogComponentDeclaration declaration;
	private final List<Statement> statements;

	public VerilogComponentImplementation(VerilogComponentDeclaration declaration, List<Statement> statements)
	{
		this.declaration = Objects.requireNonNull(declaration);
		this.statements = List.copyOf(statements);

		check();
	}

	private void check()
	{
		Set<String> usedNames = declaration.getIOPorts().stream().map(IOPort::getName).collect(Collectors.toCollection(HashSet::new));

		for (Statement statement : statements)
			for (String definedName : statement.getDefinedNames())
				if (!usedNames.add(definedName))
					throw new IllegalArgumentException("Name occurs twice: " + definedName);

		Set<Signal> allSignals = new HashSet<>();
		allSignals.addAll(declaration.getIOPorts());
		statements.stream().map(Statement::getDefinedSignals).forEach(allSignals::addAll);

		// do two passes, a signal may be referenced before it is defined
		for (Statement statement : statements)
			if (!allSignals.containsAll(statement.getReferencedSignals()))
				throw new IllegalArgumentException("Referenced an unknown signal: "
						+ statement.getReferencedSignals().stream().filter(s -> !allSignals.contains(s)).findAny().get());
	}

	public VerilogComponentDeclaration getDeclaration()
	{
		return declaration;
	}

	public List<Statement> getStatements()
	{
		return statements;
	}

	public String toVerilogCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("module " + declaration.getID());
		sb.append(declaration.getIOPorts().stream().map(IOPort::toDeclarationVerilogCode).collect(Collectors.joining(", ", "(", ")")));
		sb.append(";\n\n");

		for (Statement statement : statements)
			sb.append(statement.toVerilogCode() + "\n");
		if (!statements.isEmpty())
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
		result = prime * result + ((declaration == null) ? 0 : declaration.hashCode());
		result = prime * result + ((statements == null) ? 0 : statements.hashCode());
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
		if (declaration == null)
		{
			if (other.declaration != null)
				return false;
		} else if (!declaration.equals(other.declaration))
			return false;
		if (statements == null)
		{
			if (other.statements != null)
				return false;
		} else if (!statements.equals(other.statements))
			return false;
		return true;
	}
}
