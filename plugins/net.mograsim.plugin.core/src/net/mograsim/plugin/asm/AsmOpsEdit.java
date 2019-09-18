package net.mograsim.plugin.asm;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import net.mograsim.plugin.AsmOps;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a
 * dummy model on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the
 * workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects
 * using different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that
 * objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class AsmOpsEdit extends ViewPart
{

	@Inject
	private MPart part;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "plugintest.views.AsmOpsEdit";

	@Inject
	IWorkbench workbench;

	private Text txtInput;
	private TableViewer viewer;

	private IAction saveAction;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		@Override
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj)
		{
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter new Asm OP");
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtInput.addListener(SWT.KeyDown, e ->
		{
			if (e.keyCode == SWT.CR || e.keyCode == SWT.LF)
			{
				String in = txtInput.getText().toLowerCase();
				if (in.startsWith("-"))
					viewer.remove(in.substring(1).trim());
				else
					viewer.add(in.trim());
				txtInput.setText("");
				part.setDirty(true);
				save();
			}
		});

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(createInitialDataModel());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "PluginTest.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		contributeToActionBars();
		hookContextMenu();
	}

	private Object createInitialDataModel()
	{
		return AsmOps.ops;
	}

	@Override
	public void setFocus()
	{
		txtInput.setFocus();
	}

	@Persist
	public void save()
	{
		AsmOps.setWords(Arrays.stream(viewer.getTable().getItems()).map(i -> (String) i.getData()).collect(Collectors.toList()));
		viewer.refresh();
	}

	private void makeActions()
	{
		saveAction = new Action()
		{
			@Override
			public void run()
			{
				save();
				part.setDirty(false);
			}
		};
		saveAction.setText("Save");
		saveAction.setToolTipText("Save Changes To ISA");
		saveAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(saveAction);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(saveAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(saveAction);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this::fillContextMenu);
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
}