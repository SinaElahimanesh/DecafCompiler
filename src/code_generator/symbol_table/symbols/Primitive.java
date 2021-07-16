package code_generator.symbol_table.symbols;

import code_generator.instructions.MipsLine;
import code_generator.operand.Register;

import java.util.ArrayList;


//TODO Add all primitive types and support them.
public interface Primitive {
	/**
	 * Gets the address in memory where Registers are stored and where result should be stored, and returns the set
	 * of lines to add firstRegister and secondRegister in resultRegister.
	 * <p>
	 * Note that the registers should contain the address of the data, not the data itself.
	 *
	 * @param firstRegister  First Register
	 * @param secondRegister Seciond Register
	 * @param resultRegister Where FistRegister + secondRegister should be stored
	 * @return The set of lines to store ` FistRegister + secondRegister` in `resultRegister`
	 */
	ArrayList<MipsLine> addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	ArrayList<MipsLine> subtraction(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	ArrayList<MipsLine> multiplication(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;

	ArrayList<MipsLine> division(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException;
}
