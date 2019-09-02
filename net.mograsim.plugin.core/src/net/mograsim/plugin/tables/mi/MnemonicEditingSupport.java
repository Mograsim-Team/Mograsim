package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.Mnemonic;
import net.mograsim.machine.mi.parameters.MnemonicFamily;

public class MnemonicEditingSupport extends EditingSupport
{
	private final ComboBoxCellEditor editor;
	private final MnemonicFamily family;
	private final TableViewer viewer;
	private final int index;

	public MnemonicEditingSupport(TableViewer viewer, MicroInstructionDefinition definition, int index)
	{
		super(viewer);
		this.viewer = viewer;
		family = (MnemonicFamily) definition.getParameterClassifications()[index];
		editor = new ComboBoxCellEditor(viewer.getTable(), family.getStringValues(), SWT.READ_ONLY);
		this.index = index;
		editor.setValidator(new MnemonicCellEditorValidator(family));
	}

	@Override
	protected boolean canEdit(Object element)
	{
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		return editor;
	}

	@Override
	protected Object getValue(Object element)
	{
		return ((Mnemonic) ((MicroInstruction) element).getParameter(index)).ordinal();
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		((MicroInstruction) element).setParameter(index, family.get((Integer) value));
		viewer.update(element, null);
	}

}
