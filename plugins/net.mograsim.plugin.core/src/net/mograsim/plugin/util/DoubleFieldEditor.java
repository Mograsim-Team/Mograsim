package net.mograsim.plugin.util;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor for an double type preference.<br>
 * Adapted from {@link IntegerFieldEditor}.
 */
public class DoubleFieldEditor extends StringFieldEditor
{
	private double minValidValue = 0;

	private double maxValidValue = Double.MAX_VALUE;

	private static final int DEFAULT_TEXT_LIMIT = 10;

	/**
	 * Creates a new double field editor
	 */
	protected DoubleFieldEditor()
	{
	}

	/**
	 * Creates an double field editor.
	 *
	 * @param name      the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent    the parent of the field editor's control
	 */
	public DoubleFieldEditor(String name, String labelText, Composite parent)
	{
		this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
	}

	/**
	 * Creates an double field editor.
	 *
	 * @param name      the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent    the parent of the field editor's control
	 * @param textLimit the maximum number of characters in the text.
	 */
	public DoubleFieldEditor(String name, String labelText, Composite parent, int textLimit)
	{
		init(name, labelText);
		setTextLimit(textLimit);
		setEmptyStringAllowed(false);
		setErrorMessage("Value must be a double");
		createControl(parent);
	}

	/**
	 * Sets the range of valid values for this field.
	 *
	 * @param min the minimum allowed value (inclusive)
	 * @param max the maximum allowed value (inclusive)
	 */
	public void setValidRange(double min, double max)
	{
		minValidValue = min;
		maxValidValue = max;
		setErrorMessage("Value must be a double between " + min + " and " + max);
	}

	@Override
	protected boolean checkState()
	{
		Text text = getTextControl();

		if (text == null)
		{
			return false;
		}

		String numberString = text.getText();
		try
		{
			double number = Double.parseDouble(numberString);
			if (number >= minValidValue && number <= maxValidValue)
			{
				clearErrorMessage();
				return true;
			}

			showErrorMessage();
			return false;

		}
		catch (@SuppressWarnings("unused") NumberFormatException e1)
		{
			showErrorMessage();
		}

		return false;
	}

	@Override
	protected void doLoad()
	{
		Text text = getTextControl();
		if (text != null)
		{
			double value = getPreferenceStore().getDouble(getPreferenceName());
			String valueAsString = String.valueOf(value);
			text.setText(valueAsString);
			oldValue = valueAsString;
		}

	}

	@Override
	protected void doLoadDefault()
	{
		Text text = getTextControl();
		if (text != null)
		{
			double value = getPreferenceStore().getDefaultDouble(getPreferenceName());
			text.setText(String.valueOf(value));
		}
		valueChanged();
	}

	@Override
	protected void doStore()
	{
		Text text = getTextControl();
		if (text != null)
		{
			Double d = Double.valueOf(text.getText());
			getPreferenceStore().setValue(getPreferenceName(), d.doubleValue());
		}
	}

	/**
	 * Returns this field editor's current value as a double.
	 *
	 * @return the value
	 * @exception NumberFormatException if the <code>String</code> does not contain a parsable double
	 */
	public double getDoubleValue() throws NumberFormatException
	{
		return Double.valueOf(getStringValue()).doubleValue();
	}
}
