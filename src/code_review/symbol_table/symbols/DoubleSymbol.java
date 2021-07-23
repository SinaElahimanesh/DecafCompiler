package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.instructions.Instruction;
import code_generator.instructions.LabelMaker;
import code_generator.instructions.SystemCall;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.LabelOperand;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_generator.instructions.Label;

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
	public void isLess(Indirect a, Indirect b, Indirect r) throws SemanticException {
		
		Register r1 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateDoubleRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("c.lt.s", r1, r2));
		Label l1 = LabelMaker.createNonFunctionLabel();
		DecafCodeGenerator.mipsLines.add(new Instruction("bc1t", new LabelOperand(l1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("li", r3, new Immediate(0)));
		Label l2 = LabelMaker.createNonFunctionLabel();
		DecafCodeGenerator.mipsLines.add(new Instruction("j", new LabelOperand(l2)));
		DecafCodeGenerator.mipsLines.add(l1);
		DecafCodeGenerator.mipsLines.add(new Instruction("li", r3, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(l2);
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
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

		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("subu", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sltu", r3, new Register("zero"), r3));
		DecafCodeGenerator.mipsLines.add(new Instruction("xori", r3, r3, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void print(Register register) throws ClassNotFoundException {
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_float)));
		DecafCodeGenerator.mipsLines.add(new Instruction("l.s", new Register("f12"), new Indirect(0, register)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}

}
