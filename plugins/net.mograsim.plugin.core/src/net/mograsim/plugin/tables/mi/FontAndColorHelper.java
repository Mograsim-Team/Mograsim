package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import net.mograsim.machine.mi.MicroInstructionMemory;

public class FontAndColorHelper
{
	private final TableViewer viewer;
	private final IThemeManager themeManager;
	private long highlightedAddress = -1;
	private ColorRegistry cRegistry;
	private FontRegistry fRegistry;
	private Font boldItalic, bold, italic, normal;
	private Color modifBackground, modifForeground, highlightBackground, highlightForeground;

	private final static String font = "net.mograsim.plugin.table_font",
			colorModifBackground = "net.mograsim.plugin.modified_cell_bg_color",
			colorModifForeground = "net.mograsim.plugin.modified_cell_fg_color",
			colorHighlightForeground = "net.mograsim.plugin.highlighted_cell_fg_color",
			colorHighlightBackground = "net.mograsim.plugin.highlighted_cell_bg_color";
	private final IPropertyChangeListener updateListener;

	public FontAndColorHelper(TableViewer viewer, IThemeManager themeManager)
	{
		this.viewer = viewer;
		this.themeManager = themeManager;
		themeChanged(themeManager.getCurrentTheme());
		updateListener = e ->
		{
			switch (e.getProperty())
			{
			case IThemeManager.CHANGE_CURRENT_THEME:
				themeChanged(themeManager.getCurrentTheme());
				break;
			case font:
				fontChanged();
				break;
			case colorModifBackground:
				colorModifBackgroundChanged();
				break;
			case colorModifForeground:
				colorModifForegroundChanged();
				break;
			case colorHighlightBackground:
				colorHighlightBackgroundChanged();
				break;
			case colorHighlightForeground:
				colorHighlightForegroundChanged();
				break;
			default:
				return;
			}
			viewer.refresh();
		};
		themeManager.addPropertyChangeListener(updateListener);
	}

	private void themeChanged(ITheme theme)
	{
		cRegistry = theme.getColorRegistry();
		fRegistry = theme.getFontRegistry();
		fontChanged();
		colorHighlightBackgroundChanged();
		colorHighlightForegroundChanged();
		colorModifBackgroundChanged();
		colorModifForegroundChanged();
	}

	private void fontChanged()
	{
		boldItalic = fRegistry.getDescriptor(font).setStyle(SWT.BOLD | SWT.ITALIC).createFont(Display.getDefault());
		bold = fRegistry.getBold(font);
		italic = fRegistry.getItalic(font);
		normal = fRegistry.get(font);
		viewer.getTable().setFont(normal);
	}

	private void colorModifBackgroundChanged()
	{
		modifBackground = cRegistry.get(colorModifBackground);
	}

	private void colorModifForegroundChanged()
	{
		modifForeground = cRegistry.get(colorModifForeground);
	}

	private void colorHighlightBackgroundChanged()
	{
		highlightBackground = cRegistry.get(colorHighlightBackground);
	}

	private void colorHighlightForegroundChanged()
	{
		highlightForeground = cRegistry.get(colorHighlightForeground);
	}

	public Color getBackground(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		if (isDefault(row, column))
		{
			if (isHighlighted(row))
				return highlightBackground;
			return viewer.getTable().getBackground();
		}
		return modifBackground;
	}

	public Color getForeground(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		if (isDefault(row, column))
		{
			if (isHighlighted(row))
				return highlightForeground;
			return viewer.getTable().getForeground();
		}
		return modifForeground;
	}

	public Font getFont(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		boolean modified = !isDefault(row, column), highlighted = isHighlighted(row);
		if (modified && highlighted)
			return boldItalic;
		if (modified)
			return italic;
		if (highlighted)
			return bold;
		return normal;
	}

	private static boolean isDefault(InstructionTableRow row, int column)
	{
		return column == -1 ? true : row.data.getCell(row.address).getParameter(column).isDefault();
	}

	private boolean isHighlighted(InstructionTableRow row)
	{
		return highlightedAddress == row.address;
	}

	/**
	 * @param index Index of the row to highlight; An negative index means no row is highlighted
	 */
	public void highlight(long row)
	{
		highlightedAddress = row + ((MicroInstructionMemory) viewer.getInput()).getDefinition().getMinimalAddress();
	}

	public void dispose()
	{
		themeManager.removePropertyChangeListener(updateListener);
	}
}
