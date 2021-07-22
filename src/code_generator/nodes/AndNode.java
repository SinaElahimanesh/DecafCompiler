package code_generator.nodes;

import java.util.ArrayList;

import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_review.symbol_table.symbols.Symbol;

public class AndNode implements Node {

  ArrayList<EqualityNode> children;
  Indirect address;
  Symbol symbol;

  private EqualityNode lastChild() throws SyntaxException {
    if (children.size() == 0) throw new SyntaxException("AndNode has no child");
    return children.get(children.size() - 1);
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
    if (!isComplete()) throw new SyntaxException("incomplete AndNode");
    EqualityNode child = children.get(0);
    child.implement(mipsLines);
    address = child.getAddress();
    symbol = child.getSymbol();
    for (int i = 1; i < children.size(); i += 1) {
      child = children.get(i);
      if (!child.getSymbol().equals(symbol)) {
        throw new SemanticException("Incompatible types in AndNode");
      }
      child.implement(mipsLines);
      try {
        Register register1 = RegisterBank.allocateRegister(symbol);
        Register register2 = RegisterBank.allocateRegister(symbol);
        mipsLines.add(new Instruction("lw", register1, child.getAddress()));
        mipsLines.add(new Instruction("lw", register2, address));
				mipsLines.add(new Instruction("and", register1, register1, register2));
				mipsLines.add(new Instruction("sw", register1, address));
        RegisterBank.freeRegister(register1);
        RegisterBank.freeRegister(register2);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        System.exit(1);  
      }
    }
  }

  @Override
  public void addIdent(String token) throws SyntaxException, SemanticException {
    lastChild().addIdent(token);
  }

  @Override
  public void addOperator(String operator) throws SyntaxException, SemanticException {
    try {
      lastChild().addOperator(operator);
    } catch(SyntaxException | SemanticException e) {
      if (operator.equals("&&")) {
        if (lastChild().isComplete()) {
          children.add(new EqualityNode());
        } else {
          throw new SyntaxException("Unexpected && operator");
        }
      } else {
        throw e;
      }
    }
  }

  @Override
  public boolean isLValue() throws SyntaxException, SemanticException {
    return false;
  }

  @Override
  public boolean isComplete() throws SyntaxException, SemanticException {
    return lastChild().isComplete();
  }
  
}
