package code_generator.symbol_table.symbols;

import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;

import java.util.ArrayList;

public class IntSymbol extends Symbol implements Primitive {
	public IntSymbol() {
		super("int");
		size = 4;
	}


	@Override
	public ArrayList<MipsLine> addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException {
		ArrayList<MipsLine> result = new ArrayList<>();

		if (RegisterBank.getUseCase(firstRegister).getClass() != IntSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != IntSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != IntSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use int addition due to non-int types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		result.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		result.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		result.add(new Instruction("add", r3, r1, r2));
		result.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);

		return result;
	}

	@Override
	public ArrayList<MipsLine> subtraction(Register firstRegister, Register secondRegister, Register resultRegister) {
		return null;
	}

	@Override
	public ArrayList<MipsLine> multiplication(Register firstRegister, Register secondRegister, Register resultRegister) {
		return null;
	}

	@Override
	public ArrayList<MipsLine> division(Register firstRegister, Register secondRegister, Register resultRegister) {
		return null;
	}
}
