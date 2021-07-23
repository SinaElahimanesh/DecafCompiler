package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.instructions.Instruction;
import code_generator.instructions.SystemCall;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;

public class DoubleSymbol extends Symbol implements Primitive {
	private DoubleSymbol() {
		super("double");
		size = 4;
	}

	private static DoubleSymbol instance = null;

	public static DoubleSymbol get() {
		if (instance == null) {
			return new DoubleSymbol();
		} else {
			return instance;
		}
	}

	@Override
	public void addition(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("add.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("s.s", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("sub.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("s.s", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}


	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("mul.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("s.s", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("div.s", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("s.s", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void print(Register register) throws ClassNotFoundException {
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_float)));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", new Register("f12"), new Indirect(0, register)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}
}
