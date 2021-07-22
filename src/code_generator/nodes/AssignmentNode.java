package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class AssignmentNode implements Node {
	ArrayList<OrNode> orNodes;
	DecafCodeGenerator codeGenerator;

	AssignmentNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}

	@Override
	public Indirect getAddress() throws SyntaxException, SemanticException {
		return null;
	}

	@Override
	public Symbol getSymbol() throws SyntaxException, SemanticException {
		return null;
	}

	@Override
	public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
		if (orNodes.size() > 2)
			throw new SyntaxException("More than one assignment operators used in an expression.");

	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {

	}

	@Override
	public void addOperator(String operator) throws SyntaxException, SemanticException {

	}

	@Override
	public boolean isLValue() throws SyntaxException, SemanticException {
		return false;
	}
}
