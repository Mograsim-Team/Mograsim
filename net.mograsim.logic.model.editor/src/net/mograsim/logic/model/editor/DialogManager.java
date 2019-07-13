package net.mograsim.logic.model.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
	
	public static class InteractiveDialog
	{
		private String[] finalInput;
		private final Display display;
		private final Shell shell;
		private final Button b1, b2;
		private Text[] textFields;
		private InteractiveDialog.InteractiveDialogState state;

		public InteractiveDialog(String title, String acceptLabel, String cancelLabel, String... inputs)
		{
			display = Display.getDefault();
			shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.ON_TOP | SWT.APPLICATION_MODAL);
			shell.setMinimumSize(500, 150);
			shell.setText(title);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			shell.setLayout(layout);

			this.textFields = new Text[inputs.length];
			for (int i = 0; i < inputs.length; i++)
			{
				Label textFieldName = new Label(shell, SWT.NONE);
				textFieldName.setText(inputs[i].concat(":"));
				GridData g = new GridData();
				g.grabExcessHorizontalSpace = true;
				g.horizontalAlignment = SWT.FILL;
				Text newTextField = new Text(shell, SWT.BORDER);
				newTextField.setLayoutData(g);
				textFields[i] = newTextField;
			}
			b1 = new Button(shell, SWT.PUSH);
			b1.addListener(SWT.Selection, e ->
			{
				state = InteractiveDialogState.ACCEPTED;
				buildFinalInput();
				dispose();
			});
			b1.setText(acceptLabel);
			b2 = new Button(shell, SWT.PUSH);
			b2.addListener(SWT.Selection, e ->
			{
				state = InteractiveDialogState.CANCELLED;
				buildFinalInput();
				dispose();
			});
			b2.setText(cancelLabel);

			state = InteractiveDialogState.ACTIVE;

			shell.pack();
		}

		public String getText()
		{
			return getText(0);
		}

		public String getText(int index)
		{
			if (!shell.isDisposed())
				return textFields[index].getText();
			else
				return finalInput[index];
		}

		public void open()
		{
			shell.open();
			while (!shell.isDisposed())
				if (!display.readAndDispatch())
					display.sleep();
		}

		public void dispose()
		{
			shell.dispose();
		}

		public InteractiveDialog.InteractiveDialogState getState()
		{
			return state;
		}

		private void buildFinalInput()
		{
			finalInput = new String[textFields.length];
			for (int i = 0; i < textFields.length; i++)
				finalInput[i] = textFields[i].getText();
		}

		public static enum InteractiveDialogState
		{
			ACTIVE, ACCEPTED, CANCELLED;
		}
	}
	
	public static void openAddPinDialog(Editor editor, double x, double y)
	{
		
	}
}
