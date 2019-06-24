package net.mograsim.logic.ui.model;

public interface Visitable
{
	void accept(ModelVisitor mv);
}
