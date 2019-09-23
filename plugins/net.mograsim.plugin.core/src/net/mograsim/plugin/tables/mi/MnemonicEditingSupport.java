package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
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
		editor.setActivationStyle(
				ComboBoxCellEditor.DROP_DOWN_ON_TRAVERSE_ACTIVATION | ComboBoxCellEditor.DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION
						| ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION | ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
		this.index = index;
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
		InstructionTableRow row = ((InstructionTableRow) element);
		return ((Mnemonic) row.data.getCell(row.address).getParameter(index)).ordinal();
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		InstructionTableRow row = ((InstructionTableRow) element);
		MicroInstructionParameter[] params = row.data.getCell(row.address).getParameters();
		Mnemonic newParam = family.get((Integer) value);
		if (newParam.equals(params[index]))
			return;
		params[index] = newParam;
		row.data.setCell(row.address, MicroInstruction.create(params));
		provider.update(row.address);
	}

}
