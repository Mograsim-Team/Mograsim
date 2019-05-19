package era.mi.logic.components;

import java.util.List;

import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayEnd;

/**
 * Models a multiplexer. Takes an arbitrary amount of outputs {@link WireArray}s, one of which, as determined by select, receives the input
 * signal.
 * 
 * @author Fabian Stemmler
 *
 */
public class Demux extends BasicComponent
{
	private final WireArray select, in;
	private final WireArray[] outputs;
	private final WireArrayEnd[] outputsI;
	private final int outputSize;
	private int selected = -1;

	/**
	 * Output {@link WireArray}s and in must be of uniform length
	 * 
	 * @param in      Must be of uniform length with all outputs.
	 * @param select  Indexes the output array to which the input is mapped. Must have enough bits to index all outputs.
	 * @param outputs One of these outputs receives the input signal, depending on the select bits
	 */
	public Demux(int processTime, WireArray in, WireArray select, WireArray... outputs)
	{
		super(processTime);
		outputSize = in.length;

		this.in = in;
		this.outputs = outputs;
		this.outputsI = new WireArrayEnd[outputs.length];
		for (int i = 0; i < this.outputsI.length; i++)
		{
			if (outputs[i].length != outputSize)
				throw new IllegalArgumentException("All DEMUX wire arrays must be of uniform length!");
			this.outputsI[i] = outputs[i].createInput();
		}

		this.select = select;
		select.addObserver(this);

		int maxInputs = 1 << select.length;
		if (this.outputsI.length > maxInputs)
			throw new IllegalArgumentException("There are more outputs (" + this.outputsI.length + ") to the DEMUX than supported by "
					+ select.length + " select bits (" + maxInputs + ").");
		in.addObserver(this);
	}

	@Override
	public void compute()
	{
		int selectValue = select.hasNumericValue() ? (int) select.getUnsignedValue() : -1;
		if (selectValue >= outputsI.length)
			selectValue = -1;

		if (selected != selectValue && selected != -1)
			outputsI[selected].clearSignals();

		selected = selectValue;

		if (selectValue != -1)
			outputsI[selectValue].feedSignals(in.getValues());
	}

	@Override
	public List<WireArray> getAllInputs()
	{
		return List.of(in, select);
	}

	@Override
	public List<WireArray> getAllOutputs()
	{
		return List.of(outputs);
	}
}
