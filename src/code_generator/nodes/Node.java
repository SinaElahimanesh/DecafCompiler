package code_generator.nodes;

import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public interface Node {
	Indirect getAddress() throws SyntaxException, SemanticException;

	Symbol getSymbol() throws SyntaxException, SemanticException;

	void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException;

	void addIdent(String token) throws SyntaxException, SemanticException;

	void addOperator(String operator) throws SyntaxException, SemanticException;

	boolean isLValue() throws SyntaxException, SemanticException;

	boolean isComplete() throws SyntaxException, SemanticException;
}
