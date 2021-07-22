package code_generator.nodes;

import java.util.ArrayList;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

public class EqualityNode implements Node {

  CallNode inner;

  public EqualityNode(DecafCodeGenerator codeGenerator) {
    inner = new CallNode(codeGenerator);
  }

  @Override
  public Indirect getAddress() throws SyntaxException, SemanticException {
    return inner.getAddress();
  }

  @Override
  public Symbol getSymbol() throws SyntaxException, SemanticException {
    return inner.getSymbol();
  }

  @Override
  public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
    inner.implement(mipsLines);
  }

  @Override
  public void addIdent(String token) throws SyntaxException, SemanticException {
    inner.addIdent(token);
  }

  @Override
  public void addOperator(String operator) throws SyntaxException, SemanticException {
    inner.addOperator(operator);
  }

  @Override
  public boolean isLValue() throws SyntaxException, SemanticException {
    return inner.isLValue();
  }

  @Override
  public boolean isComplete() throws SyntaxException, SemanticException {
    return inner.isComplete();
  }
}
