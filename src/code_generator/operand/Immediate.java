package code_generator.operand;

public class Immediate implements Operand {
	Integer value;

	/**
	 * Receives an immediate number.
	 * @param value  is the immediate value.
	 */
	public Immediate(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
