package code_generator.nodes;

import java.util.ArrayList;

import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

public class EqualityNode implements Node {

  @Override
  public Indirect getAddress() throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Symbol getSymbol() throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void implement(ArrayList<MipsLine> mipsLines) throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addIdent(String token) throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addOperator(String operator) throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isLValue() throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isComplete() throws SyntaxException, SemanticException {
    // TODO Auto-generated method stub
    return false;
  }
}
