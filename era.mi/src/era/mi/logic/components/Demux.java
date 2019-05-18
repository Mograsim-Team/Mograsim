package era.mi.logic.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import era.mi.logic.wires.WireArray;
import era.mi.logic.wires.WireArray.WireArrayInput;

/**
 * Models a multiplexer. Takes an arbitrary amount of input {@link WireArray}s, one of which, as determined by select, is put through to the
 * output.
 * 
 * @author Fabian Stemmler
 *
 */
public class Demux extends BasicComponent {
	private final WireArray select, in;
	private final WireArray[] outputs;
	private final WireArrayInput[] outputsI;
	private final int outputSize;
	private int selected = -1;

	/**
	 * Input {@link WireArray}s and out must be of uniform length
	 * 
	 * @param out     Must be of uniform length with all inputs.
	 * @param select  Indexes the input array which is to be mapped to the output. Must have enough bits to index all inputs.
	 * @param outputs One of these inputs is mapped to the output, depending on the select bits
	 */
	public Demux(int processTime, WireArray in, WireArray select, WireArray... outputs) {
		super(processTime);
		outputSize = in.length;

		this.in = in;
		this.outputs = outputs;
		this.outputsI = new WireArrayInput[outputs.length];
		for (int i = 0; i < this.outputsI.length; i++) {
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
	public void compute() {
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
	public List<WireArray> getAllInputs() {
		return Collections.unmodifiableList(Arrays.asList(in, select));
	}

	@Override
	public List<WireArray> getAllOutputs() {
		return Collections.unmodifiableList(Arrays.asList(outputs));
	}
}
