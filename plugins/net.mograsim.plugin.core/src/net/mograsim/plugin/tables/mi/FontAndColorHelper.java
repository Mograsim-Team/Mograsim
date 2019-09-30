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
	private Font boldItalic;

	private final static String font = "net.mograsim.plugin.table_font",
			colorModifBackground = "net.mograsim.plugin.modified_cell_bg_color",
			colorModifForeground = "net.mograsim.plugin.modified_cell_fg_color",
			colorHighlightedForeground = "net.mograsim.plugin.highlighted_cell_fg_color",
			colorHighlightedBackground = "net.mograsim.plugin.highlighted_cell_bg_color";
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
				//$FALL-THROUGH$
			case font:
			case colorModifBackground:
			case colorModifForeground:
				viewer.refresh();
				break;
			default:
				break;
			}
		};
		themeManager.addPropertyChangeListener(updateListener);
	}

	private void themeChanged(ITheme theme)
	{
		cRegistry = theme.getColorRegistry();
		fRegistry = theme.getFontRegistry();
		boldItalic = fRegistry.getDescriptor(font).setStyle(SWT.BOLD | SWT.ITALIC).createFont(Display.getDefault());
		viewer.getTable().setFont(fRegistry.get(font));
	}

	public Color getBackground(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		if (isDefault(row, column))
		{
			if (isHighlighted(row))
				return cRegistry.get(colorHighlightedBackground);
			return viewer.getTable().getBackground();
		}
		return cRegistry.get(colorModifBackground);
	}

	public Color getForeground(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		if (isDefault(row, column))
		{
			if (isHighlighted(row))
				return cRegistry.get(colorHighlightedForeground);
			return viewer.getTable().getForeground();
		}
		return cRegistry.get(colorModifForeground);
	}

	public Font getFont(Object element, int column)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		boolean modified = !isDefault(row, column), highlighted = isHighlighted(row);
		if (modified && highlighted)
			return boldItalic;
		if (modified)
			return fRegistry.getItalic(font);
		if (highlighted)
			return fRegistry.getBold(font);
		return fRegistry.get(font);
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
