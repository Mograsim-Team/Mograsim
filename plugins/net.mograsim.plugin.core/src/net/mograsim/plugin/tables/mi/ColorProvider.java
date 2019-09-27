package net.mograsim.plugin.tables.mi;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.themes.IThemeManager;

public class ColorProvider
{
	private final TableViewer viewer;
	private final IThemeManager themeManager;
	private ColorRegistry cRegistry;

	private final static String modifBackground = "net.mograsim.plugin.modified_cell_bg_color",
			modifForeground = "net.mograsim.plugin.modified_cell_fg_color";
	private final IPropertyChangeListener updateListener;

	public ColorProvider(TableViewer viewer, IThemeManager themeManager)
	{
		this.viewer = viewer;
		this.themeManager = themeManager;
		this.cRegistry = themeManager.getCurrentTheme().getColorRegistry();
		updateListener = e ->
		{
			switch (e.getProperty())
			{
			case IThemeManager.CHANGE_CURRENT_THEME:
				cRegistry = themeManager.getCurrentTheme().getColorRegistry();
				//$FALL-THROUGH$
			case modifBackground:
			case modifForeground:
				viewer.refresh();
				break;
			default:
				break;
			}
		};
		themeManager.addPropertyChangeListener(updateListener);
	}

	public Color getBackground(Object element, int index)
	{
		InstructionTableRow row = (InstructionTableRow) element;

		return row.data.getCell(row.address).getParameter(index).isDefault() ? viewer.getTable().getBackground()
				: cRegistry.get(modifBackground);
	}

	public Color getForeground(Object element, int index)
	{
		InstructionTableRow row = (InstructionTableRow) element;
		return row.data.getCell(row.address).getParameter(index).isDefault() ? viewer.getTable().getForeground()
				: cRegistry.get(modifForeground);
	}

	public void dispose()
	{
		themeManager.removePropertyChangeListener(updateListener);
	}
}
