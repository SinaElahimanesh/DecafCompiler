package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.operand.Immediate;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_generator.stack.TemporaryMemoryBank;
import code_review.symbol_table.symbols.BoolSymbol;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class UnaryNode implements Node {
	DecafCodeGenerator codeGenerator;
	Node lValueNode;

	Symbol symbol;
	Indirect address;
	Indirect globalTmp = TemporaryMemoryBank.allocateTemporaryMemory(4);
  boolean begin = true;
	String operator;

	public UnaryNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		lValueNode = new LValueNode(codeGenerator);
	}

	@Override
	public Indirect getAddress() throws SyntaxException, SemanticException {
		return address;
	}

	@Override
	public Symbol getSymbol() throws SyntaxException, SemanticException {
		return symbol;
	}

	@Override
	public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
		lValueNode.implement(mipsLines);
		symbol = lValueNode.getSymbol();
		address = lValueNode.getAddress();
		if (operator == null) return;
		if (operator.equals("-")) {
			if (symbol.equals(IntSymbol.get())) {
				Register register = RegisterBank.allocateRegister(symbol);
        mipsLines.add(new Instruction("lw", register, address));
        mipsLines.add(new Instruction("sub", register, new Register("zero"), register));
        mipsLines.add(new Instruction("sw", register, globalTmp));
			} else {
				throw new SemanticException("Can not apply - on non int");
			}
		} else if (operator.equals("!")) {
			if (symbol.equals(BoolSymbol.get())) {
				Register register = RegisterBank.allocateRegister(symbol);
				Register one = RegisterBank.allocateRegister(symbol);
        mipsLines.add(new Instruction("lw", register, address));
				mipsLines.add(new Instruction("li", one, new Immediate(1)));
        mipsLines.add(new Instruction("sub", register, one, register));
        mipsLines.add(new Instruction("sw", register, globalTmp));
			} else {
				throw new SemanticException("Can not apply ! on non bool");
			}
		}
		address = globalTmp;
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		begin = false;
		lValueNode.addIdent(token);
	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {
		if (begin && (operator.equals("!") || operator.equals("-") || operator.equals("new"))) {
			this.operator = operator;
			begin = false;
			lValueNode = new UnaryNode(codeGenerator);
		} else {
			lValueNode.addOperator(operator);
		}
	}

	@Override
	public boolean isLValue() throws SyntaxException, SemanticException {
		return lValueNode.isLValue();
	}

	@Override
	public boolean isComplete() throws SyntaxException, SemanticException {
		return lValueNode.isComplete();
	}

	@Override
	public Symbol getType() throws SyntaxException, SemanticException {
		return lValueNode.getType();
	}
}
