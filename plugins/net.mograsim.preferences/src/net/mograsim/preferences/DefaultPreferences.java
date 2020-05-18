package net.mograsim.preferences;

import java.util.function.Consumer;

public abstract class DefaultPreferences implements Preferences
{
	// Adding / removing listeners. All are no-ops, since DefaultPreferences are immutable.

	//@formatter:off
	@Override public final void addBooleanListener(String name, Consumer<Boolean> listener) {/**/}
	@Override public final void removeBooleanListener(String name, Consumer<Boolean> listener) {/**/}
	@Override public final void addIntListener(String name, Consumer<Integer> listener) {/**/}
	@Override public final void removeIntListener(String name, Consumer<Integer> listener) {/**/}
	@Override public final void addDoubleListener(String name, Consumer<Double> listener) {/**/}
	@Override public final void removeDoubleListener(String name, Consumer<Double> listener) {/**/}
	@Override public final void addStringListener(String name, Consumer<String> listener) {/**/}
	@Override public final void removeStringListener(String name, Consumer<String> listener) {/**/}
	@Override public final void addColorDefinitionListener(String name, Consumer<ColorDefinition> listener) {/**/}
	@Override public final void removeColorDefinitionListener(String name, Consumer<ColorDefinition> listener) {/**/}
	//@formatter:on
}