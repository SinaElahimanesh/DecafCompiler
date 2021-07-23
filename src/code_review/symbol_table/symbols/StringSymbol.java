package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.instructions.*;
import code_generator.operand.*;

public final class StringSymbol extends Symbol implements Primitive {
	private static StringSymbol instance = null;

	private StringSymbol() {
		super("string");
		size = 4;
	}

	public static StringSymbol get() {
		if (instance == null)
			instance = new StringSymbol();
		return instance;
	}

	private void concatenate(Register baseAddress, Register operandAddress) {
		Label loopLabel = LabelMaker.createNonFunctionLabel();
		Label exitLabel = LabelMaker.createNonFunctionLabel();

		Register temp = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(loopLabel);
		DecafCodeGenerator.mipsLines.add(new Instruction("lb", temp, new Indirect(0, operandAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sb", temp, new Indirect(0, baseAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("beq", temp, new Register("zero"), new LabelOperand(exitLabel)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", operandAddress, operandAddress, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", baseAddress, baseAddress, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("j", new LabelOperand(loopLabel)));
		DecafCodeGenerator.mipsLines.add(exitLabel);

		RegisterBank.freeRegister(temp);
	}

	private void getSize(Register sizeRegister, Register stringAddress) {
		Label loopLabel = LabelMaker.createNonFunctionLabel();
		Label exitLabel = LabelMaker.createNonFunctionLabel();

		Register tempRegister = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("li", sizeRegister, new Immediate(0)));
		DecafCodeGenerator.mipsLines.add(loopLabel);
		DecafCodeGenerator.mipsLines.add(new Instruction("lb", tempRegister, new Indirect(0, stringAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("beq", tempRegister, new Register("zero"), new LabelOperand(exitLabel)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", sizeRegister, sizeRegister, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", stringAddress, stringAddress, new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("j", new LabelOperand(loopLabel)));
		DecafCodeGenerator.mipsLines.add(exitLabel);

		RegisterBank.freeRegister(tempRegister);
	}


	public void addition(Indirect a, Indirect b, Indirect r) {
		Register firstStartAddress = RegisterBank.allocateRegister(VoidSymbol.get());
		Register secondStartAddress = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", firstStartAddress, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", secondStartAddress, b));

		Register firstSize = RegisterBank.allocateRegister(VoidSymbol.get());
		Register secondSize = RegisterBank.allocateRegister(VoidSymbol.get());

		getSize(firstSize, firstStartAddress);
		getSize(secondSize, secondStartAddress);

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", firstStartAddress, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", secondStartAddress, b));

		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.allocate)));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", new Register("a0"), firstSize, secondSize));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", new Register("a0"), new Register("a0"), new Immediate(1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));

		DecafCodeGenerator.mipsLines.add(new Instruction("sw", new Register("v0"), r));

		concatenate(new Register("v0"), firstStartAddress);
		concatenate(new Register("v0"), secondStartAddress);

		RegisterBank.freeRegister(firstSize);
		RegisterBank.freeRegister(secondSize);
		RegisterBank.freeRegister(firstStartAddress);
		RegisterBank.freeRegister(secondStartAddress);
	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Subtraction of strings is not allowed.");
	}

	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Subtraction of strings is not allowed.");
	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Division of strings is not allowed.");
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void print(Register register) throws ClassNotFoundException {
		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.print_string)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", new Register("a0"), new Indirect(0, register)));
		DecafCodeGenerator.mipsLines.add(new Instruction("syscall"));
	}

	@Override
	public void isLess(Indirect a, Indirect b, Indirect r) throws SemanticException {
		// TODO Auto-generated method stub
		
	}
}
