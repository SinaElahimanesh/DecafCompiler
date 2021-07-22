package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class LValueNode implements Node {
	DecafCodeGenerator codeGenerator;
	CallNode callNode;

	Symbol symbol;
	Indirect address;

	public LValueNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		callNode = new CallNode(codeGenerator);
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
		callNode.implement(mipsLines);
		symbol = callNode.symbol;
		address = callNode.address;
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		callNode.addIdent(token);
	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {
		callNode.addOperator(operator);
	}

	@Override
	public boolean isLValue() throws SyntaxException, SemanticException {
		return false;
	}
}
