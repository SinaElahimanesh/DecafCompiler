package code_generator.operand;

import code_generator.instructions.Label;

public class Indirect implements Operand {
	Integer immediate;
	Label label = null;
	Register register;

	/**
	 * Receives the immediate value and the register for indirect addressing.
	 * @param immediate immediate part
	 * @param register base register
	 */
	public Indirect(Integer immediate, Register register) {
		this.immediate = immediate;
		this.register = register;
	}

	public Indirect(Label label, Register register) {
		this.label = label;
		this.register = register;
	}

	public Integer getImmediate() {
		return immediate;
	}

	@Override
	public String toString() {
		if (label == null)
			return immediate.toString() + '(' + register.toString() + ')';
		else
			return label.getName() + '(' + register.toString() + ')';
	}
}
