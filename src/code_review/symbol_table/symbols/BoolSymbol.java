package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.instructions.Instruction;
import code_generator.instructions.Label;
import code_generator.instructions.LabelMaker;
import code_generator.instructions.SystemCall;
import code_generator.operand.*;

public class BoolSymbol extends Symbol implements Primitive {
	private BoolSymbol() {
		super("bool");
		size = 4;
	}

	private static BoolSymbol instance = null;

	public static BoolSymbol get() {
		if (instance == null) {
			instance = new BoolSymbol();
		}
		return instance;
	}

	@Override
	public void addition(Register firstRegister, Register secondRegister, Register resultRegister)
			throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool addition due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void subtraction(Register firstRegister, Register secondRegister, Register resultRegister) throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool subtraction due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sub", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void multiplication(Register firstRegister, Register secondRegister, Register resultRegister) throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool multiple due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, new Indirect(0, firstRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, new Indirect(0, secondRegister)));
		DecafCodeGenerator.mipsLines.add(new Instruction("mul", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, new Indirect(0, resultRegister)));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void division(Register firstRegister, Register secondRegister, Register resultRegister) throws ClassNotFoundException {
		if (RegisterBank.getUseCase(firstRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(secondRegister).getClass() != BoolSymbol.class ||
				RegisterBank.getUseCase(resultRegister).getClass() != BoolSymbol.class) {
			throw new IncompatibleClassChangeError("Can't use bool division due to non-bool types");
		}

		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

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
		Register value = RegisterBank.allocateRegister(VoidSymbol.get());
		Label finalLabel = LabelMaker.createNonFunctionLabel();
		Label falseLabel = LabelMaker.createNonFunctionLabel();
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_string)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", value, new Indirect(0, register)));
		DecafCodeGenerator.mipsLines.add(new Instruction("beq", value, new Register("zero"), new LabelOperand(falseLabel)));
		DecafCodeGenerator.mipsLines.add(new Instruction("la", new Register("a0"), new LabelOperand(new Label("string__true"))));
		DecafCodeGenerator.mipsLines.add(new Instruction("j", new LabelOperand(finalLabel)));
		DecafCodeGenerator.mipsLines.add(falseLabel);
		DecafCodeGenerator.mipsLines.add(new Instruction("la", new Register("a0"), new LabelOperand(new Label("string__false"))));
		DecafCodeGenerator.mipsLines.add(finalLabel);
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}
}
