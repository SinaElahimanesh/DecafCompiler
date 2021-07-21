package code_review.symbol_table.symbols;

import code_generator.operand.Register;


//TODO Add all primitive types and support them.
public interface Primitive {
	/**
	 * Gets the address in memory where Registers are stored and where result should be stored, and adds the set
	 * of lines to add firstRegister and secondRegister in resultRegister.
	 * <p>
	 * Note that the registers should contain the address of the data, not the data itself.
	 *
	 * @param firstRegister  First Register
	 * @param secondRegister Seciond Register
	 * @param resultRegister Where FistRegister + secondRegister should be stored
	 */
	void addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	void subtraction(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	void multiplication(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	void division(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	void print(Register register)
			throws ClassNotFoundException;
}
