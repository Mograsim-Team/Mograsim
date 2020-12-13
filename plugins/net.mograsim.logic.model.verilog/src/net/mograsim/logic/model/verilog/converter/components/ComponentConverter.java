package net.mograsim.logic.model.verilog.converter.components;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.model.components.ModelComponent;
import net.mograsim.logic.model.verilog.converter.ComponentConversionResult;

public interface ComponentConverter<C extends ModelComponent>
{
	public ComponentConversionResult convert(C modelComponent, String modelID, JsonElement params, String verilogID);
}
