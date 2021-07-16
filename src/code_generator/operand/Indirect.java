package code_generator.operand;

public class Indirect implements Operand {
	Integer immediate;
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

	@Override
	public String toString() {
		return immediate.toString() + '(' + register.toString() + ')';
	}
}
