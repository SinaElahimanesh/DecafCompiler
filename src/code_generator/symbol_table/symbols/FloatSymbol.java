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

public class FloatSymbol extends Symbol implements Primitive {
	private FloatSymbol() {
		super("float");
		size = 4;
	}

	private static FloatSymbol instance = null;

	public static FloatSymbol get() {
		if (instance == null) {
			return new FloatSymbol();
		} else {
			return instance;
		}
	}

	@Override
	public void addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != FloatSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use float addition due to non-float types");
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
		if (RegisterBank.getUseCase(firstRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != FloatSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use float subtraction due to non-float types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sub.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void multiplication(Register firstRegister, Register secondRegister, Register resultRegister) {
		if (RegisterBank.getUseCase(firstRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != FloatSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use float multiple due to non-float types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("mul.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void division(Register firstRegister, Register secondRegister, Register resultRegister) {
		if (RegisterBank.getUseCase(firstRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != FloatSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != FloatSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use float multiple due to non-float types");
		}

		Register r1 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r2 = RegisterBank.allocateRegister(BlankSymbol.get());
		Register r3 = RegisterBank.allocateRegister(BlankSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("div.s", r3, r1, r2));
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
