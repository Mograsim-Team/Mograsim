package net.mograsim.preferences;

import java.util.function.Consumer;

public abstract class DefaultPreferences implements Preferences
{
	// Adding / removing listeners. All are no-ops, since DefaultPreferences are immutable.

	//@formatter:off
	@Override public void addBooleanListener(String name, Consumer<Boolean> listener) {/**/}
	@Override public void removeBooleanListener(String name, Consumer<Boolean> listener) {/**/}
	@Override public void addIntListener(String name, Consumer<Integer> listener) {/**/}
	@Override public void removeIntListener(String name, Consumer<Integer> listener) {/**/}
	@Override public void addDoubleListener(String name, Consumer<Double> listener) {/**/}
	@Override public void removeDoubleListener(String name, Consumer<Double> listener) {/**/}
	@Override public void addColorDefinitionListener(String name, Consumer<ColorDefinition> listener) {/**/}
	@Override public void removeColorDefinitionListener(String name, Consumer<ColorDefinition> listener) {/**/}
	//@formatter:on
}