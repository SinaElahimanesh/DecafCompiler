package code_generator.operand;

import code_generator.instructions.Label;

public class LabelOperand implements Operand {
	Label label;

	/**
	 * Receives a label for instructions like `jal ...` that accept a label as their operand.
	 * @param label the label.
	 */
	public LabelOperand(Label label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label.getName();
	}
}
