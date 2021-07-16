package code_generator.operand;

import code_generator.symbol_table.symbols.BlankSymbol;
import code_generator.symbol_table.symbols.Symbol;

import java.util.HashMap;
import java.util.Set;

/**
 * Returns an unused temporary or saved register (We don't believe in saved register) Try so hard not to use temporary
 * registers, they may change during unwanted systemcall. (You can try by freeing registers ASAP)
 */
public class RegisterBank {
	static String[] registerNames = {
			"s0", "s1", "s2", "s3", "s4",
			"s5", "s6", "s7",
			"t0", "t1", "t2", "t3", "t4",
			"t5", "t6", "t7", "t8", "t9",
	};

	/**
	 * We use a symbol for registers which contain address of a datatype. This would help others to check type validity.
	 */
	static HashMap<String, Symbol> usedRegisters = new HashMap<>();

	public static Register allocateRegister(Symbol useCase) throws ClassNotFoundException {
		for (String registerName: registerNames) {
			if (!usedRegisters.containsKey(registerName)) {
				if (registerName.charAt(0) == 't') {
					System.out.println("WARNING::RegisterBank.allocateRegister: Temporary registers are begin used.");
				}

				Register register = new Register(registerName);
				usedRegisters.put(registerName, useCase);
				return register;
			}
		}
		throw new ClassNotFoundException("No registers are free");
	}

	public static void freeRegister(Register register) {
		usedRegisters.remove(register.name);
	}

	public static Symbol getUseCase(Register register) {
		if (usedRegisters.containsKey(register.name)) {
			return usedRegisters.get(register.name);
		}

		return BlankSymbol.get();
	}
}
