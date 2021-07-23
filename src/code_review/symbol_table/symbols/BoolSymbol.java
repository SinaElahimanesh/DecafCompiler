package code_review.symbol_table.symbols;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
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

	@Override
	public void addition(Indirect a, Indirect b, Indirect r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) {
		// TODO Auto-generated method stub
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}
}
