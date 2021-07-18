package code_generator.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.instructions.SystemCall;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;

import java.util.ArrayList;

public class BoolSymbol extends Symbol implements Primitive {
	private BoolSymbol() {
		super("bool");
		size = 4;
	}

	private static BoolSymbol instance = null;

	public static BoolSymbol get() {
		if (instance == null) {
			return new BoolSymbol();
		} else {
			return instance;
		}
	}

	@Override
	public void addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool addition due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void subtraction(Register firstRegister, Register secondRegister, Register resultRegister) {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool subtraction due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sub", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void multiplication(Register firstRegister, Register secondRegister, Register resultRegister) {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool multiple due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("mul", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void division(Register firstRegister, Register secondRegister, Register resultRegister) {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool division due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("div", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void print(Register register) throws ClassNotFoundException {
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_int)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", new Register("a0"), register, new Immediate(0)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}
}
