package net.mograsim.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class AsmOps
{
	public static Set<String> ops = new HashSet<>();
	public static Set<Consumer<Set<String>>> listeners = new HashSet<>();

	static
	{
		ops.add("add");
		ops.add("mul");
		ops.add("mov");
		ops.add("inc");
	}

	public static void setWords(Collection<String> s)
	{
		if (ops.size() == s.size() && ops.containsAll(s))
			return;
		ops.clear();
		ops.addAll(s);
		update();
	}

	public static void addListener(Consumer<Set<String>> con)
	{
		listeners.add(con);
		con.accept(ops);
	}

	public static void removeListener(Consumer<Set<String>> con)
	{
		listeners.remove(con);
	}

	public static void update()
	{
		listeners.forEach(c -> c.accept(ops));
	}
}
