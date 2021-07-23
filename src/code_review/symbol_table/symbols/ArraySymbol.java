package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.instructions.Instruction;
import code_generator.instructions.Label;
import code_generator.instructions.LabelMaker;
import code_generator.instructions.SystemCall;
import code_generator.operand.*;
import code_generator.stack.Scope;

public class ArraySymbol extends Symbol implements Primitive {
	Symbol elementType;
	public ArraySymbol(Symbol elementType) {
		super("Array["+elementType.name+"]");
		this.elementType = elementType;
	}

	@Override
	public void addition(Indirect a, Indirect b, Indirect r) throws SemanticException {
		Register aStartAddress = RegisterBank.allocateRegister(VoidSymbol.get());
		Register aSize = RegisterBank.allocateRegister(VoidSymbol.get());
		Register bStartAddress = RegisterBank.allocateRegister(VoidSymbol.get());
		Register bSize = RegisterBank.allocateRegister(VoidSymbol.get());

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", aStartAddress, a));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", bStartAddress, b));

		DecafCodeGenerator.mipsLines.add(new Instruction("lw", aSize, new Indirect(0, aStartAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", bSize, new Indirect(0, bStartAddress)));

		DecafCodeGenerator.mipsLines.add(new Instruction("li", new Register("v0"), new Immediate(SystemCall.allocate)));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", new Register("a0"), aSize, bSize));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", new Register("a0"), new Register("zero"), new Immediate(1)));

		Register resultAddress = new Register("v0");
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", new Register("a0"), new Indirect(0, resultAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("add", resultAddress, resultAddress, new Immediate(4)));

		concatenate(resultAddress, aStartAddress, aSize);
		concatenate(resultAddress, bStartAddress, bSize);

		RegisterBank.freeRegister(aStartAddress);
		RegisterBank.freeRegister(aSize);
		RegisterBank.freeRegister(bStartAddress);
		RegisterBank.freeRegister(bSize);
	}

	private void concatenate(Register baseAddress, Register operandAddress, Register operandSize) {
		Register tempRegister = RegisterBank.allocateRegister(VoidSymbol.get());

		Label loopLabel = LabelMaker.createNonFunctionLabel();
		Label exitLabel = LabelMaker.createNonFunctionLabel();

		DecafCodeGenerator.mipsLines.add(loopLabel);
		DecafCodeGenerator.mipsLines.add(new Instruction("beq", operandSize, new Register("zero"), new LabelOperand(exitLabel)));
		DecafCodeGenerator.mipsLines.add(new Instruction("lw", tempRegister, new Indirect(0, operandAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("sw", tempRegister, new Indirect(0, baseAddress)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", operandSize, operandSize, new Immediate(-1)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", operandAddress, operandAddress, new Immediate(4)));
		DecafCodeGenerator.mipsLines.add(new Instruction("addi", baseAddress, baseAddress, new Immediate(4)));
		DecafCodeGenerator.mipsLines.add(new Instruction("j", new LabelOperand(loopLabel)));
		DecafCodeGenerator.mipsLines.add(exitLabel);

		RegisterBank.freeRegister(tempRegister);
	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Subtraction of arrays is not allowed.");
	}

	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Multiplication of arrays is not allowed.");

	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Division of arrays is not allowed.");
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {
		IntSymbol.get().isEqual(a, b, r);		// :D
	}

	@Override
	public void isLess(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Comparison of arrays is not allowed");
	}

	@Override
	public void print(Register r) throws ClassNotFoundException, SemanticException {
		throw new SemanticException("Printing arrays is not allowed");
	}
}
