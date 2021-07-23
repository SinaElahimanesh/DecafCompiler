package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.instructions.Instruction;
import code_generator.instructions.SystemCall;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;

public class IntSymbol extends Symbol implements Primitive {
	private IntSymbol() {
		super("int");
		size = 4;
	}

	private static IntSymbol instance = null;

	public static IntSymbol get() {
		if (instance == null) {
			instance = new IntSymbol();
		}
		return instance;
	}

	@Override
	public void addition(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("sub", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}


	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("mul", r3, r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) {
		
		Register r1 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r2 = RegisterBank.allocateRegister(VoidSymbol.get());
		Register r3 = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r1, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", r2, b));
		DecafCodeGenerator.mipsLines.add(new Instruction("div", r1, r2));
		DecafCodeGenerator.mipsLines.add(new Instruction("mflo", r3));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", r3, r));

		RegisterBank.freeRegister(r1);
		RegisterBank.freeRegister(r2);
		RegisterBank.freeRegister(r3);
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void print(Register register) throws ClassNotFoundException {
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_int)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", new Register("a0"), new Indirect(0, register)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}
}
