package net.mograsim.plugin.asm.editor;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;

import net.mograsim.plugin.asm.editor.rules.AsmLabelRule;
import net.mograsim.plugin.asm.editor.rules.AsmNumberRule;
import net.mograsim.plugin.asm.editor.rules.InstructionRule;

public class AsmPresentationReconciler extends PresentationReconciler
{

	private final Token comment = new Token(null);
	private final Token std = new Token(null);
	private final Token op = new Token(null);
	private final Token label = new Token(null);
	private final Token number = new Token(null);

	private IRule[] rules;

	public AsmPresentationReconciler()
	{
		RuleBasedScanner scanner = new RuleBasedScanner();
		rules = new IRule[4];
		rules[0] = new EndOfLineRule(";", comment);
		rules[1] = new AsmLabelRule(label);
		rules[2] = new InstructionRule(op, true);
		rules[3] = new AsmNumberRule(number);

		scanner.setRules(rules);
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		ITheme current = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
		updateStyle(current);
		current.getColorRegistry().addListener(e -> updateStyle(current));
		current.getFontRegistry().addListener(e -> updateStyle(current));
	}

	void updateStyle(ITheme current)
	{
		ColorRegistry cr = current.getColorRegistry();
		FontRegistry fr = current.getFontRegistry();
		comment.setData(new TextAttribute(cr.get("net.mograsim.plugin.asm_comment_color")));
		op.setData(new TextAttribute(cr.get("net.mograsim.plugin.asm_operation_color"), null, 0,
				fr.get("net.mograsim.plugin.asm_operation_font")));
		label.setData(new TextAttribute(cr.get("net.mograsim.plugin.asm_label_color")));
		number.setData(new TextAttribute(cr.get("net.mograsim.plugin.asm_number_color")));
	}
}