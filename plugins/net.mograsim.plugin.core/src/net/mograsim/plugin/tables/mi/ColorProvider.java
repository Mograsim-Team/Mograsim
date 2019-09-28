package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.themes.IThemeManager;

import net.mograsim.machine.mi.MicroInstructionMemory;

public class ColorProvider
{
	private final TableViewer viewer;
	private final IThemeManager themeManager;
	private long highlightedAddress = -1;
	private ColorRegistry cRegistry;
	private FontRegistry fRegistry;

	private final static String font = "net.mograsim.plugin.table_font",
			colorModifBackground = "net.mograsim.plugin.modified_cell_bg_color",
			colorModifForeground = "net.mograsim.plugin.modified_cell_fg_color",
			colorHighlightedForeground = "net.mograsim.plugin.highlighted_cell_fg_color",
			colorHighlightedBackground = "net.mograsim.plugin.highlighted_cell_bg_color";
	private final IPropertyChangeListener updateListener;

	public ColorProvider(TableViewer viewer, IThemeManager themeManager)
	{
		this.viewer = viewer;
		this.themeManager = themeManager;
		this.cRegistry = themeManager.getCurrentTheme().getColorRegistry();
		this.fRegistry = themeManager.getCurrentTheme().getFontRegistry();
		updateListener = e ->
		{
			switch (e.getProperty())
			{
			case IThemeManager.CHANGE_CURRENT_THEME:
				cRegistry = themeManager.getCurrentTheme().getColorRegistry();
				fRegistry = themeManager.getCurrentTheme().getFontRegistry();
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
		return !isDefault(row, column) || isHighlighted(row) ? fRegistry.getBold(font) : fRegistry.get(font);
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
