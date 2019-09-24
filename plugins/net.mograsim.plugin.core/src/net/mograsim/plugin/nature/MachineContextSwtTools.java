package net.mograsim.plugin.nature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import net.mograsim.machine.MachineDefinition;
import net.mograsim.machine.MachineRegistry;
import net.mograsim.plugin.nature.ProjectContextEvent.ProjectContextEventType;

public final class MachineContextSwtTools
{
	private static final Map<String, MachineDefinition> INSTALLED_MACHINES = MachineRegistry.getInstalledMachines();
	private static final Map<IProject, MachineContext> PROJECT_MACHINE_CONTEXTS = ProjectMachineContext.getAllProjectMachineContexts();

	private MachineContextSwtTools()
	{
		// not instantiable
	}

	public static MachineCombo createMachineSelector(Composite parent, int style)
	{
		return new MachineCombo(parent, style);
	}

	public static MograsimProjectCombo createMograsimProjectSelector(Composite parent, int style)
	{
		return new MograsimProjectCombo(parent, style);
	}

	public abstract static class AdvancedCombo<T>
	{
		final ComboViewer combo;
		private Set<Consumer<T>> listeners;

		public AdvancedCombo(Composite parent, Function<T, String> labelProvider)
		{
			this(parent, SWT.NONE, labelProvider);
		}

		public AdvancedCombo(Composite parent, int style, Function<T, String> labelProvider)
		{
			listeners = Collections.synchronizedSet(new HashSet<>());
			combo = new ComboViewer(parent, style);
			combo.addSelectionChangedListener(e -> updateSelection());
			combo.setComparator(new ViewerComparator());
			combo.setLabelProvider(new LabelProvider()
			{
				@SuppressWarnings("unchecked")
				@Override
				public String getText(Object element)
				{
					try
					{
						return labelProvider.apply((T) element);
					}
					catch (ClassCastException e)
					{
						return "Invalid Element: " + e.getLocalizedMessage();
					}
				}
			});
		}

		public final ComboViewer getCombo()
		{
			return combo;
		}

		@SuppressWarnings("unchecked")
		public T getSelection()
		{
			return (T) combo.getStructuredSelection().getFirstElement();
		}

		private void updateSelection()
		{
			T active = getSelection();
			listeners.forEach(l -> l.accept(active));
		}

		public final void addListener(Consumer<T> listener)
		{
			listeners.add(listener);
		}

		public final void removeListener(Consumer<T> listener)
		{
			listeners.remove(listener);
		}

		public void refreshContent()
		{
			Display.getDefault().asyncExec(combo::refresh);
		}
	}

	public static class MachineCombo extends AdvancedCombo<MachineDefinition>
	{
		private static final Set<MachineCombo> machineComboListeners = Collections.synchronizedSet(new HashSet<>());

		static
		{
			MachineRegistry.addMachineRegistryListener(newMap -> machineComboListeners.forEach(AdvancedCombo::refreshContent));
		}

		public MachineCombo(Composite parent)
		{
			this(parent, SWT.NONE);
		}

		public MachineCombo(Composite parent, int style)
		{
			super(parent, style, MachineDefinition::getId);
			combo.setContentProvider(new IStructuredContentProvider()
			{
				@Override
				public void dispose()
				{
					machineComboListeners.remove(MachineCombo.this);
				}

				@Override
				public Object[] getElements(Object inputElement)
				{
					return INSTALLED_MACHINES.values().toArray();
				}
			});
			combo.setInput(this);
			machineComboListeners.add(this);
		}
	}

	public static class MograsimProjectCombo extends AdvancedCombo<IProject>
	{
		private static final Set<MograsimProjectCombo> projectComboListeners = Collections.synchronizedSet(new HashSet<>());

		static
		{
			ProjectMachineContext.addProjectContextListener(projectEvent ->
			{
				if (projectEvent.getEventType() != ProjectContextEventType.OTHER_CHANGE)
					projectComboListeners.forEach(AdvancedCombo::refreshContent);
			});
		}

		public MograsimProjectCombo(Composite parent)
		{
			this(parent, SWT.NONE);
		}

		public MograsimProjectCombo(Composite parent, int style)
		{
			super(parent, style, IProject::getName);
			combo.setContentProvider(new IStructuredContentProvider()
			{
				@Override
				public void dispose()
				{
					projectComboListeners.remove(MograsimProjectCombo.this);
				}

				@Override
				public Object[] getElements(Object inputElement)
				{
					return PROJECT_MACHINE_CONTEXTS.values().stream().filter(mc -> mc.getProject().isOpen() && mc.isCurrentyValid())
							.map(MachineContext::getProject).toArray();
				}
			});
			combo.setInput(this);
			projectComboListeners.add(this);
		}
	}

	/**
	 * XXX: of no use?
	 */
	static Optional<String> getSelection(Combo c)
	{
		int selectionIndex = c.getSelectionIndex();
		if (selectionIndex == -1)
			return Optional.empty();
		return Optional.of(c.getItem(selectionIndex));
	}
}
