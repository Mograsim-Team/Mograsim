package net.mograsim.plugin.tables.memory;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import net.mograsim.plugin.asm.AsmNumberUtil;
import net.mograsim.plugin.asm.AsmNumberUtil.NumberType;

public class NumberVerifyListener implements VerifyListener
{

	@Override
	public void verifyText(VerifyEvent e)
	{
		String text = computeModifiedText(e);
		e.doit = !NumberType.NONE.equals(AsmNumberUtil.prefixOfType(text));
	}

	private static String computeModifiedText(VerifyEvent e)
	{
		String modifiedText = ((Text) e.getSource()).getText();
		modifiedText = modifiedText.substring(0, e.start).concat(e.text).concat(modifiedText.substring(e.end));
		return modifiedText;
	}
}
