package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.Mnemonic;
import net.mograsim.machine.mi.parameters.MnemonicFamily;

public class MnemonicEditingSupport extends EditingSupport
{
	private final ComboBoxCellEditor editor;
	private final MnemonicFamily family;
	private final int index;
	private InstructionTableContentProvider provider;

	public MnemonicEditingSupport(TableViewer viewer, MicroInstructionDefinition definition, int index,
			InstructionTableContentProvider provider)
	{
		super(viewer);
		family = (MnemonicFamily) definition.getParameterClassifications()[index];
		editor = new ComboBoxCellEditor(viewer.getTable(), family.getStringValues(), SWT.READ_ONLY);
		this.index = index;
		editor.setValidator(new MnemonicCellEditorValidator(family));
		this.provider = provider;
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
		return ((Mnemonic) ((InstructionTableRow) element).data.getParameter(index)).ordinal();
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		InstructionTableRow row = ((InstructionTableRow) element);
		row.data.setParameter(index, family.get((Integer) value));
		provider.update(row.address);
	}

}
