package code_generator.operand;


public class Register implements Operand {
	String name;

	/**
	 * Receives a string like "t1".
	 * @param name  is the name of register in this operand.
	 */
	public Register(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "$" + name;
	}
}
