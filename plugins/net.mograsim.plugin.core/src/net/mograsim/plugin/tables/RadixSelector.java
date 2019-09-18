package net.mograsim.plugin.tables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class RadixSelector
{
	private final Composite parent;
	private final DisplaySettings target;
	private Label label;
	private Combo combo;

	public RadixSelector(Composite parent, DisplaySettings target)
	{
		this.parent = parent;
		this.target = target;
		setupRadixSelector();
	}

	private void setupRadixSelector()
	{
		label = new Label(parent, SWT.NONE);
		label.setText("Radix: ");
		combo = new Combo(parent, SWT.READ_ONLY);

		String entries[] = new String[] { "Binary", "Octal", "Decimal", "Hexadecimal" };
		NumberType corTypes[] = new NumberType[] { NumberType.BINARY, NumberType.OCTAL, NumberType.DECIMAL, NumberType.HEXADECIMAL };
		combo.setItems(entries);
		combo.select(3);
		combo.addListener(SWT.Selection, e ->
		{
			int index = combo.getSelectionIndex();
			if (index == -1)
				target.setDataNumberType(NumberType.HEXADECIMAL);
			else
				target.setDataNumberType(corTypes[index]);
		});
	}

	public Label getLabel()
	{
		return label;
	}

	public Combo getCombo()
	{
		return combo;
	}
}
