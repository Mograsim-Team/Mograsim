package net.mograsim.logic.model.editor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DialogManager
{
	private Shell parent;

	public DialogManager(Shell parent)
	{
		this.parent = parent;
	}

	public void openWarningDialog(String title, String message)
	{
		MessageBox b = new MessageBox(parent, SWT.ICON_WARNING | SWT.OK);
		b.setText(title);
		b.setMessage(message);
		b.open();
	}

	private static abstract class FlexibleInputsDialog extends Dialog
	{
		private final String title, acceptLabel, cancelLabel;

		protected String[] result;

		public FlexibleInputsDialog(String title, String acceptLabel, String cancelLabel, String... inputs)
		{
			this(false, title, acceptLabel, cancelLabel, inputs);
		}

		public FlexibleInputsDialog(boolean resizable, String title, String acceptLabel, String cancelLabel, String... inputs)
		{
			super(new Shell(SWT.CLOSE | (resizable ? SWT.RESIZE | SWT.MAX : 0) | SWT.TITLE | SWT.MIN | SWT.ON_TOP | SWT.APPLICATION_MODAL));
			this.title = title;
			this.acceptLabel = acceptLabel;
			this.cancelLabel = cancelLabel;
		}

		protected abstract void setupWidgets(Composite parent);

		protected abstract void buildResult();

		/**
		 * @return May be null (if {@link Dialog} was cancelled)
		 */
		public String[] open()
		{
			Shell shell = getParent();
			Display display = shell.getDisplay();
			shell.setMinimumSize(500, 150);
			shell.setText(title);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			shell.setLayout(layout);

			Composite inputContainer = new Composite(shell, SWT.BORDER);
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessHorizontalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			gd.grabExcessVerticalSpace = true;
			inputContainer.setLayoutData(gd);
			setupWidgets(inputContainer);

			Button b1 = new Button(shell, SWT.PUSH);
			b1.addListener(SWT.Selection, e ->
			{
				buildResult();
				shell.dispose();
			});

			b1.setText(acceptLabel);
			Button b2 = new Button(shell, SWT.PUSH);
			b2.addListener(SWT.Selection, e ->
			{
				shell.dispose();
			});
			b2.setText(cancelLabel);

			shell.pack();

			shell.open();
			while (!shell.isDisposed())
				if (!display.readAndDispatch())
					display.sleep();
			return result;
		}
	}

	private static class MultiTextFieldsDialog extends FlexibleInputsDialog
	{
		private final String[] inputs;
		private Text[] textFields;

		public MultiTextFieldsDialog(String title, String acceptLabel, String cancelLabel, String... inputs)
		{
			super(title, acceptLabel, cancelLabel);
			this.inputs = inputs;
		}

		@Override
		protected void setupWidgets(Composite parent)
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			parent.setLayout(layout);
			this.textFields = new Text[inputs.length];
			for (int i = 0; i < inputs.length; i++)
			{
				Label textFieldName = new Label(parent, SWT.NONE);
				textFieldName.setText(inputs[i].concat(":"));
				GridData g = new GridData();
				g.grabExcessHorizontalSpace = true;
				g.horizontalAlignment = SWT.FILL;
				Text newTextField = new Text(parent, SWT.BORDER);
				newTextField.setLayoutData(g);
				textFields[i] = newTextField;
			}
		}

		@Override
		protected void buildResult()
		{
			result = new String[textFields.length];
			for (int i = 0; i < textFields.length; i++)
				result[i] = textFields[i].getText();
		}

	}

	/**
	 * @return The Strings entered, in order of the input labels the dialog was opened with, if the dialog was accepted, null if the dialog
	 *         was cancelled.
	 */
	public static String[] openMultiTextDialog(String title, String acceptLabel, String cancelLabel, String... inputs)
	{
		return new MultiTextFieldsDialog(title, acceptLabel, cancelLabel, inputs).open();
	}

	public static class MultiLineTextFieldDialog extends FlexibleInputsDialog
	{
		private final String input;
		private Text textField;

		public MultiLineTextFieldDialog(String title, String acceptLabel, String cancelLabel, String input)
		{
			super(true, title, acceptLabel, cancelLabel);
			this.input = input;
		}

		@Override
		protected void setupWidgets(Composite parent)
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			parent.setLayout(layout);
			GridData gd = new GridData();
			Label l = new Label(parent, SWT.NONE);
			l.setText(input);
			gd.verticalAlignment = SWT.TOP;
			l.setLayoutData(gd);
			gd = new GridData();
			textField = new Text(parent, SWT.V_SCROLL);
			textField.setLayoutData(gd);
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			gd.verticalAlignment = SWT.FILL;
		}

		@Override
		protected void buildResult()
		{
			result = new String[] { textField.getText() };
		}
	}

	/**
	 * @return The String entered if the dialog was accepted, null if the dialog was cancelled.
	 */
	public static String openMultiLineTextDialog(String title, String acceptLabel, String cancelLabel, String input)
	{
		String[] result = new MultiLineTextFieldDialog(title, acceptLabel, cancelLabel, input).open();
		return result == null ? null : result[0];
	}
}
