package net.mograsim.logic.model.verilog.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import net.mograsim.logic.model.verilog.converter.VerilogEmulatedModelPin.Type;
import net.mograsim.logic.model.verilog.model.VerilogComponentDeclaration;

public class ModelComponentToVerilogComponentDeclarationMapping
{
	private final String modelComponentID;
	private final JsonElement modelComponentParams;
	private final VerilogComponentDeclaration verilogComponentDeclaration;
	private final Set<VerilogEmulatedModelPin> pinMapping;

	private final Set<Set<PinNameBit>> internallyConnectedPins;
	private final Map<PinNameBit, VerilogEmulatedModelPin> prePinMapping;
	private final Map<PinNameBit, VerilogEmulatedModelPin> outPinMapping;
	private final Map<PinNameBit, VerilogEmulatedModelPin> resPinMapping;
	private final List<VerilogEmulatedModelPin> reversePinMapping;

	public ModelComponentToVerilogComponentDeclarationMapping(String modelComponentID, JsonElement modelComponentParams,
			VerilogComponentDeclaration verilogComponentDeclaration, Set<VerilogEmulatedModelPin> pinMapping)
	{
		this.modelComponentID = Objects.requireNonNull(modelComponentID);
		this.modelComponentParams = Objects.requireNonNull(modelComponentParams);
		this.verilogComponentDeclaration = Objects.requireNonNull(verilogComponentDeclaration);
		this.pinMapping = Set.copyOf(pinMapping);

		this.reversePinMapping = checkAndCalculateReversePinMapping();

		this.internallyConnectedPins = calculateInternallyConnectedPins();
		this.prePinMapping = filterPinMapping(Type.PRE);
		this.outPinMapping = filterPinMapping(Type.OUT);
		this.resPinMapping = filterPinMapping(Type.RES);
	}

	private List<VerilogEmulatedModelPin> checkAndCalculateReversePinMapping()
	{
		List<VerilogEmulatedModelPin> reverseMapping = new ArrayList<>(pinMapping.size());
		for (int i = 0; i < pinMapping.size(); i++)
			reverseMapping.add(null);
		Map<Type, Set<PinNameBit>> usedPinNameBits = new HashMap<>();
		for (Type t : Type.values())
			usedPinNameBits.put(t, new HashSet<>());
		for (VerilogEmulatedModelPin verilogEmulatedModelPin : pinMapping)
		{
			// TODO check if pre, out, res pins are consistent with each other
			for (PinNameBit pinbit : verilogEmulatedModelPin.getPinbits())
				if (!usedPinNameBits.get(verilogEmulatedModelPin.getType()).add(pinbit))
					throw new IllegalArgumentException("Pinbit occurs twice: " + pinbit);
			int verilogPinIndex = verilogEmulatedModelPin.getPortIndex();
			if (verilogComponentDeclaration.getIOPorts().get(verilogPinIndex) != verilogEmulatedModelPin.getVerilogPort())
				throw new IllegalArgumentException("Incorrect IO port index for port: " + verilogEmulatedModelPin);
			VerilogEmulatedModelPin oldVerilogEmulatedModelPin = reverseMapping.set(verilogPinIndex, verilogEmulatedModelPin);
			if (oldVerilogEmulatedModelPin != null)
				throw new IllegalArgumentException("Port is used twice: " + verilogEmulatedModelPin.getVerilogPort());
		}
		for (int i = 0; i < reverseMapping.size(); i++)
			if (reverseMapping.get(i) == null)
				throw new IllegalArgumentException("Unused IO port: " + verilogComponentDeclaration.getIOPorts().get(i));
		return reverseMapping;
	}

	private Set<Set<PinNameBit>> calculateInternallyConnectedPins()
	{
		return pinMapping.stream().map(VerilogEmulatedModelPin::getPinbits).collect(Collectors.toUnmodifiableSet());
	}

	private Map<PinNameBit, VerilogEmulatedModelPin> filterPinMapping(Type filteredType)
	{
		Map<PinNameBit, VerilogEmulatedModelPin> result = new HashMap<>();
		for (VerilogEmulatedModelPin p : pinMapping)
			if (p.getType() == filteredType)
				for (PinNameBit pinbit : p.getPinbits())
					result.put(pinbit, p);
		return Map.copyOf(result);
	}

	public String getModelComponentID()
	{
		return modelComponentID;
	}

	public JsonElement getModelComponentParams()
	{
		return modelComponentParams;
	}

	public VerilogComponentDeclaration getVerilogComponentDeclaration()
	{
		return verilogComponentDeclaration;
	}

	public Set<Set<PinNameBit>> getInternallyConnectedPins()
	{
		return internallyConnectedPins;
	}

	public Set<VerilogEmulatedModelPin> getPinMapping()
	{
		return pinMapping;
	}

	public Map<PinNameBit, VerilogEmulatedModelPin> getPrePinMapping()
	{
		return prePinMapping;
	}

	public Map<PinNameBit, VerilogEmulatedModelPin> getOutPinMapping()
	{
		return outPinMapping;
	}

	public Map<PinNameBit, VerilogEmulatedModelPin> getResPinMapping()
	{
		return resPinMapping;
	}

	public List<VerilogEmulatedModelPin> getReversePinMapping()
	{
		return reversePinMapping;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelComponentID == null) ? 0 : modelComponentID.hashCode());
		result = prime * result + ((modelComponentParams == null) ? 0 : modelComponentParams.hashCode());
		result = prime * result + ((pinMapping == null) ? 0 : pinMapping.hashCode());
		result = prime * result + ((verilogComponentDeclaration == null) ? 0 : verilogComponentDeclaration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelComponentToVerilogComponentDeclarationMapping other = (ModelComponentToVerilogComponentDeclarationMapping) obj;
		if (modelComponentID == null)
		{
			if (other.modelComponentID != null)
				return false;
		} else if (!modelComponentID.equals(other.modelComponentID))
			return false;
		if (modelComponentParams == null)
		{
			if (other.modelComponentParams != null)
				return false;
		} else if (!modelComponentParams.equals(other.modelComponentParams))
			return false;
		if (pinMapping == null)
		{
			if (other.pinMapping != null)
				return false;
		} else if (!pinMapping.equals(other.pinMapping))
			return false;
		if (verilogComponentDeclaration == null)
		{
			if (other.verilogComponentDeclaration != null)
				return false;
		} else if (!verilogComponentDeclaration.equals(other.verilogComponentDeclaration))
			return false;
		return true;
	}
}
