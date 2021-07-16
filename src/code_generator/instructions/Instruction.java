package code_generator.instructions;

import code_generator.operand.Operand;

public class Instruction implements MipsLine {
	String opCode;
	Integer operandsCount;
	Operand[] operands = new Operand[3];

	/**
	 * Creates an Instruction with no arguments.
	 * @param opCode The opcode of the instruction.
	 */
	public Instruction(String opCode) {
		operandsCount = 0;
		this.opCode = opCode;
	}

	/**
	 * Creates an Instruction with one argument.
	 * @param opCode The opcode of the instruction.
	 * @param firstOperand The first operand.
	 */
	public Instruction(String opCode, Operand firstOperand) {
		operandsCount = 1;
		this.opCode = opCode;
		operands[0] = firstOperand;
	}

	/**
	 * Creates an Instruction with one argument.
	 * @param opCode The opcode of the instruction.
	 * @param firstOperand The first operand.
	 * @param secondOperand The second operand.
	 */
	public Instruction(String opCode, Operand firstOperand, Operand secondOperand) {
		operandsCount = 2;
		this.opCode = opCode;
		operands[0] = firstOperand;
		operands[1] = secondOperand;
	}

	/**
	 * Creates an Instruction with one argument.
	 * @param opCode The opcode of the instruction.
	 * @param firstOperand The first operand.
	 * @param secondOperand The second operand.
	 * @param thirdOperand The third operand.
	 */
	public Instruction(String opCode, Operand firstOperand, Operand secondOperand, Operand thirdOperand) {
		operandsCount = 3;
		this.opCode = opCode;
		operands[0] = firstOperand;
		operands[1] = secondOperand;
		operands[2] = thirdOperand;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(opCode).append(' ');
		if (operandsCount > 0) {
			result.append(operands[0]);
		}

		for (int i = 1; i < operandsCount; i ++) {
			result.append(", ");
			result.append(operands[i]);
		}

		return result.toString();
	}

	public String getOpCode() {
		return opCode;
	}

	public Integer getOperandsCount() {
		return operandsCount;
	}

	public Operand[] getOperands() {
		return operands;
	}
}
