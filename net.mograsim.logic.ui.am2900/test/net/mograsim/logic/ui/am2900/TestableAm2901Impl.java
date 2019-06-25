package net.mograsim.logic.ui.am2900;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import net.mograsim.logic.core.components.BitDisplay;
import net.mograsim.logic.core.components.ManualSwitch;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.types.Bit;
import net.mograsim.logic.core.types.BitVector;
import net.mograsim.logic.core.types.BitVector.BitVectorMutator;
import net.mograsim.logic.ui.model.ViewModel;
import net.mograsim.logic.ui.model.ViewModelModifiable;
import net.mograsim.logic.ui.model.components.GUIBitDisplay;
import net.mograsim.logic.ui.model.components.GUIComponent;
import net.mograsim.logic.ui.model.components.GUIManualSwitch;
import net.mograsim.logic.ui.model.components.SubmodelComponent;
import net.mograsim.logic.ui.model.components.mi.nandbased.am2901.GUIAm2901;
import net.mograsim.logic.ui.model.wires.GUIWire;
import net.mograsim.logic.ui.modeladapter.LogicModelParameters;
import net.mograsim.logic.ui.modeladapter.ViewLogicModelAdapter;

public class TestableAm2901Impl implements TestableAm2901
{
	private GUIAm2901 am2901;
	private Timeline timeline;
	private ManualSwitch I8, I7, I6, I5, I4, I3, I2, I1, I0;
	private ManualSwitch C;
	private ManualSwitch Cn;
	private ManualSwitch D1, D2, D3, D4;
	private ManualSwitch A0, A1, A2, A3;
	private ManualSwitch B0, B1, B2, B3;
	private ManualSwitch IRAMn, IRAMn_3, IQn, IQn_3;
	private BitDisplay Y1, Y2, Y3, Y4;
	private BitDisplay F_0, Cn_4, OVR, F3;
	private BitDisplay ORAMn, ORAMn_3, OQn, OQn_3;

	private Set<String> wireDebugChangeSet;
	private boolean debugWires = false;
	public int debugEventThreshold = 10_000;
	public int debugEventCount = 500;

	private int eventCounter;

	@Override
	public Result run()
	{
		// Normal execution until completion or eventLimit
		int eventLimit = debugEventThreshold;
		eventCounter = 0;
		debugWires = false;
		while (eventCounter < eventLimit)
		{
			timeline.executeNext();
			if (!timeline.hasNext())
			{
				System.out.println("run() took " + eventCounter + " events");
				return Result.SUCCESS;
			}
		}
		// Start debugging if event limit is reached
		debugWires = true;
		wireDebugChangeSet = new TreeSet<>();
		Set<String> oldChangeSet;
		// observe wire changes to detect, if we are really stuck in an endless loop
		do
		{
			eventLimit += debugEventCount;
			oldChangeSet = wireDebugChangeSet;
			wireDebugChangeSet = new TreeSet<>();
			while (eventCounter < eventLimit)
			{
				timeline.executeNext();
				if (!timeline.hasNext())
				{
					// no endless loop, but more events needed than expected
					System.out.println("run() took longer than expected: " + eventCounter);
					return Result.SUCCESS;
				}
			}
		} while (!oldChangeSet.equals(wireDebugChangeSet));
		// if stuck, abort execution and print wires
		System.err.print("Problematic Wire updates:");
		wireDebugChangeSet.forEach(System.out::println);
		System.err.println("run() failed: " + eventCounter);
		return Result.OUT_OF_TIME;
	}

	@SuppressWarnings("unused")
	@Override
	public void setup()
	{
		// Create view model
		ViewModelModifiable viewModel = new ViewModelModifiable();
		am2901 = new GUIAm2901(viewModel);
		// Get switches
		HashMap<String, GUIManualSwitch> idSwitchMap = new HashMap<>();
		for (String id : am2901.getInputPinNames())
		{
			GUIManualSwitch sw = new GUIManualSwitch(viewModel);
			new GUIWire(viewModel, am2901.getPin(id), sw.getOutputPin());
			idSwitchMap.put(id, sw);
		}
		// Get displays
		HashMap<String, GUIBitDisplay> idDisplayMap = new HashMap<>();
		for (String id : am2901.getOutputPinNames())
		{
			GUIBitDisplay bd = new GUIBitDisplay(viewModel);
			new GUIWire(viewModel, am2901.getPin(id), bd.getInputPin());
			idDisplayMap.put(id, bd);
		}
		// Create logic model
		LogicModelParameters params = new LogicModelParameters();
		params.gateProcessTime = 50;
		params.wireTravelTime = 10;
		timeline = ViewLogicModelAdapter.convert(viewModel, params);
		// Bind switches/displays to this test class
		for (var entry : idSwitchMap.entrySet())
			setField(entry.getKey().replaceAll("\\+|=", "_"), entry.getValue().getManualSwitch());
		for (var entry : idDisplayMap.entrySet())
			setField(entry.getKey().replaceAll("\\+|=", "_"), entry.getValue().getBitDisplay());
		// Switch Clock off first
		C.switchOff();

		// Debug code
		HashSet<GUIWire> wiresIncludingSubmodels = new HashSet<>();
		Queue<ViewModel> modelsToIterate = new LinkedList<>();
		modelsToIterate.add(viewModel);
		while (modelsToIterate.size() > 0)
		{
			ViewModel model = modelsToIterate.poll();
			wiresIncludingSubmodels.addAll(model.getWires());
			for (GUIComponent comp : model.getComponents())
				if (comp instanceof SubmodelComponent)
					modelsToIterate.offer(((SubmodelComponent) comp).submodel);
		}
		wiresIncludingSubmodels.forEach(w -> w.addRedrawListener(() ->
		{
			if (debugWires)
			{
				System.out.println(w);
				wireDebugChangeSet.add(w.toString());
			}
		}));
		timeline.addEventAddedListener(te -> eventCounter++);
	}

	@Override
	public void setDest(Am2901_Dest dest)
	{
		var bits = of(dest.ordinal(), 3);
		I8.setToValueOf(bits.getBit(0));
		I7.setToValueOf(bits.getBit(1));
		I6.setToValueOf(bits.getBit(2));
	}

	@Override
	public void setFunc(Am2901_Func func)
	{
		var bits = of(func.ordinal(), 3);
		I5.setToValueOf(bits.getBit(0));
		I4.setToValueOf(bits.getBit(1));
		I3.setToValueOf(bits.getBit(2));
	}

	@Override
	public void setSrc(Am2901_Src src)
	{
		var bits = of(src.ordinal(), 3);
		I2.setToValueOf(bits.getBit(0));
		I1.setToValueOf(bits.getBit(1));
		I0.setToValueOf(bits.getBit(2));
	}

	@Override
	public void setReg_A(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		A3.setToValueOf(bits.getBit(0));
		A2.setToValueOf(bits.getBit(1));
		A1.setToValueOf(bits.getBit(2));
		A0.setToValueOf(bits.getBit(3));
	}

	@Override
	public void setReg_B(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		B3.setToValueOf(bits.getBit(0));
		B2.setToValueOf(bits.getBit(1));
		B1.setToValueOf(bits.getBit(2));
		B0.setToValueOf(bits.getBit(3));
	}

	@Override
	public void setCarryIn(String val_1_bit)
	{
		Cn.setToValueOf(Bit.parse(val_1_bit));
	}

	@Override
	public void setNotOutEnable(String val_1_bit)
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public void setD(String val_4_bit)
	{
		var bits = BitVector.parse(val_4_bit);
		D4.setToValueOf(bits.getBit(0));
		D3.setToValueOf(bits.getBit(1));
		D2.setToValueOf(bits.getBit(2));
		D1.setToValueOf(bits.getBit(3));
	}

	@Override
	public void setQ_0(String val_1_bit)
	{
		IQn.setToValueOf(Bit.parse(val_1_bit));
	}

	@Override
	public void setQ_3(String val_1_bit)
	{
		IQn_3.setToValueOf(Bit.parse(val_1_bit));
	}

	@Override
	public void setRAM_0(String val_1_bit)
	{
		IRAMn.setToValueOf(Bit.parse(val_1_bit));
	}

	@Override
	public void setRAM_3(String val_1_bit)
	{
		IRAMn_3.setToValueOf(Bit.parse(val_1_bit));
	}

	@Override
	public void toogleClock()
	{
		C.toggle();
	}

	@Override
	public String getQ_0()
	{
		return OQn.getDisplayedValue().toString();
	}

	@Override
	public String getQ_3()
	{
		return OQn_3.getDisplayedValue().toString();
	}

	@Override
	public String getRAM_0()
	{
		return ORAMn.getDisplayedValue().toString();
	}

	@Override
	public String getRAM_3()
	{
		return ORAMn_3.getDisplayedValue().toString();
	}

	@Override
	public String getNotP()
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public String getNotG()
	{
		throw new UnsupportedOperationException(); // TODO
	}

	@Override
	public String getCarryOut()
	{
		return Cn_4.getDisplayedValue().toString();
	}

	@Override
	public String getSign()
	{
		return F3.getDisplayedValue().toString();
	}

	@Override
	public String getZero()
	{
		return F_0.getDisplayedValue().toString();
	}

	@Override
	public String getOverflow()
	{
		return OVR.getDisplayedValue().toString();
	}

	@Override
	public String getY()
	{
		var y3 = Y4.getDisplayedValue();
		var y2 = Y3.getDisplayedValue();
		var y1 = Y2.getDisplayedValue();
		var y0 = Y1.getDisplayedValue();
		return y3.concat(y2).concat(y1).concat(y0).toString();
	}

	private void setField(String name, Object value)
	{
		try
		{
			Field f = TestableAm2901Impl.class.getDeclaredField(name);
			f.setAccessible(true);
			f.set(this, Objects.requireNonNull(value));
		}
		catch (Exception e)
		{
			fail(e);
		}
	}

	private static BitVector of(int value, int length)
	{
		BitVectorMutator mutator = BitVectorMutator.ofLength(length);
		int val = value;
		for (int i = length - 1; i >= 0; i--)
		{
			mutator.setBit(i, Bit.lastBitOf(val));
			val >>>= 1;
		}
		return mutator.toBitVector();
	}
}