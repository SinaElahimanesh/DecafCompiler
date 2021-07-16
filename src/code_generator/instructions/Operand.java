package code_generator.instructions;

public class Operand {
	OperandType type;
	String register_name;
	Integer immediate;
	Label label;

	/**
	 * Creates a blank operand to be filled later.
	 */
	public Operand() {
		type = OperandType.BLANK;
	}

	/**
	 * Receives a string like "t1".
	 * @param register_name  is the name of register in this operand.
	 */
	public Operand(String register_name) {
		type = OperandType.REGISTER;
		this.register_name = register_name;
	}

	/**
	 * Receives an immediate number.
	 * @param immediate  is the immediate value.
	 */
	public Operand(Integer immediate) {
		type = OperandType.IMMEDIATE;
		this.immediate = immediate;
	}

	/**
	 * Receives the immediate value and the register for indirect addressing.
	 * @param immediate immediate part
	 * @param register_name base register's name
	 */
	public Operand(Integer immediate, String register_name) {
		type = OperandType.INDIRECT_ADDRESS;
		this.immediate = immediate;
		this.register_name = register_name;
	}

	/**
	 * Receives a label for instructions like `jal ...` that accept a label as their operand.
	 * @param label the label.
	 */
	public Operand(Label label) {
		type = OperandType.LABEL;
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		switch (type) {
			case LABEL:
				result.append(label.getName());
				break;
			case REGISTER:
				result.append('$').append(register_name);
				break;
			case IMMEDIATE:
				result.append(immediate.toString());
				break;
			case INDIRECT_ADDRESS:
				result.append(immediate).append('(').append(register_name).append(')');
				break;
			case BLANK:
				throw new RuntimeException("code_generator.instructions.operand blank type on toString.");
			default:
				throw new RuntimeException("code_generator.instructions.operand has invalid type.");
		}
		return result.toString();
	}

	public OperandType getType() {
		return type;
	}

	public String getRegister_name() {
		return register_name;
	}

	public Integer getImmediate() {
		return immediate;
	}

	public Label getLabel() {
		return label;
	}
}
