package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.ILazyContentProvider;

public interface InstructionTableContentProvider extends ILazyContentProvider
{
	public void update(long address);
}
