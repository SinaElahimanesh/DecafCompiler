package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class UnaryNode implements Node {
	DecafCodeGenerator codeGenerator;
	LValueNode lValueNode;

	Symbol symbol;
	Indirect address;

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
		symbol = lValueNode.symbol;
		address = lValueNode.address;
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		lValueNode.addIdent(token);
	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {
		lValueNode.addOperator(operator);
	}

	@Override
	public boolean isLValue() throws SyntaxException, SemanticException {
		return false;
	}
}
