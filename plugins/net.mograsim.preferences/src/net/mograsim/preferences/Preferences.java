package net.mograsim.preferences;

import java.util.function.Consumer;

import org.eclipse.swt.graphics.Color;

public interface Preferences
{
	public boolean getBoolean(String name);

	public void addBooleanListener(String name, Consumer<Boolean> listener);

	public void removeBooleanListener(String name, Consumer<Boolean> listener);

	public int getInt(String name);

	public void addIntListener(String name, Consumer<Integer> listener);

	public void removeIntListener(String name, Consumer<Integer> listener);

	public double getDouble(String name);

	public void addDoubleListener(String name, Consumer<Double> listener);

	public void removeDoubleListener(String name, Consumer<Double> listener);

	public String getString(String name);

	public void addStringListener(String name, Consumer<String> listener);

	public void removeStringListener(String name, Consumer<String> listener);

	public ColorDefinition getColorDefinition(String name);

	public void addColorDefinitionListener(String name, Consumer<ColorDefinition> listener);

	public void removeColorDefinitionListener(String name, Consumer<ColorDefinition> listener);

	public default Color getColor(String name)
	{
		return ColorManager.current().toColor(getColorDefinition(name));
	}
}
