package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import net.mograsim.logic.core.types.Bit;
import net.mograsim.machine.mi.MicroInstruction;
import net.mograsim.machine.mi.MicroInstructionDefinition;
import net.mograsim.machine.mi.parameters.BooleanClassification;
import net.mograsim.machine.mi.parameters.MicroInstructionParameter;
import net.mograsim.machine.mi.parameters.Mnemonic;

public class BooleanEditingSupport extends EditingSupport
{
	private final ComboBoxCellEditor editor;
	private final BooleanClassification boolClass;
	private final TableViewer viewer;
	private final int index;

	public BooleanEditingSupport(TableViewer viewer, MicroInstructionDefinition definition, int index)
	{
		super(viewer);
		this.viewer = viewer;
		this.boolClass = (BooleanClassification) definition.getParameterClassification(index);
		editor = new ComboBoxCellEditor(viewer.getTable(), boolClass.getStringValues(), SWT.READ_ONLY);
		editor.setActivationStyle(
				ComboBoxCellEditor.DROP_DOWN_ON_TRAVERSE_ACTIVATION | ComboBoxCellEditor.DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION
						| ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION | ComboBoxCellEditor.DROP_DOWN_ON_KEY_ACTIVATION);
		this.index = index;
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
		InstructionTableRow row = (InstructionTableRow) element;
		// true is 0 because the true value comes first in the combo box
		return row.data.getCell(row.address).getParameter(index).getValue().getMSBit(0).equals(Bit.ONE) ? 0 : 1;
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		MicroInstructionParameter[] params = row.data.getCell(row.address).getParameters();
		// true is 0 because the true value comes first in the combo box
		Mnemonic newParam = boolClass.get(value.equals(0) ? true : false);
		if (newParam.equals(params[index]))
			return;
		params[index] = newParam;
		row.data.setCell(row.address, MicroInstruction.create(params));
		viewer.update(element, null);
	}

}
