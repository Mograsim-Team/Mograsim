package net.mograsim.logic.model.preferences;

import net.mograsim.preferences.Preferences;

public interface RenderPreferences extends Preferences
{
	public static final String PREFIX = "net.mograsim.logic.model.";

	public static final String IMPROVE_SCALING = PREFIX + "improvescaling.enable";
	public static final String LINE_DASH_IMPROVEMENT_FACTOR = PREFIX + "improvescaling.factor";
	public static final String TEXT_COLOR = PREFIX + "color.text";
	public static final String FOREGROUND_COLOR = PREFIX + "color.foreground";
	public static final String BACKGROUND_COLOR = PREFIX + "color.background";
	public static final String BIT_ZERO_COLOR = PREFIX + "color.bit.zero";
	public static final String BIT_Z_COLOR = PREFIX + "color.bit.z";
	public static final String BIT_X_COLOR = PREFIX + "color.bit.x";
	public static final String BIT_U_COLOR = PREFIX + "color.bit.u";
	public static final String BIT_ONE_COLOR = PREFIX + "color.bit.one";
	public static final String SUBMODEL_ZOOM_ALPHA_1 = PREFIX + "submodel.zoomalpha1";
	public static final String SUBMODEL_ZOOM_ALPHA_0 = PREFIX + "submodel.zoomalpha0";
	public static final String WIRE_WIDTH_MULTIBIT = PREFIX + "linewidth.wire.multibit";
	public static final String WIRE_WIDTH_SINGLEBIT = PREFIX + "linewidth.wire.singlebit";
	public static final String DEFAULT_LINE_WIDTH = PREFIX + "linewidth.default";
	public static final String ZOOM_BUTTON = PREFIX + "button.zoom";
	public static final String DRAG_BUTTON = PREFIX + "button.drag";
	public static final String ACTION_BUTTON = PREFIX + "button.action";
	public static final String DEBUG_OPEN_HLSSHELL = PREFIX + "debug.openhlsshell";
	public static final String DEBUG_HLSSHELL_DEPTH = PREFIX + "debug.hlsshelldepth";
}
