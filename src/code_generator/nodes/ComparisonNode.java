package code_generator.nodes;

import java.util.ArrayList;

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
import code_review.symbol_table.symbols.DoubleSymbol;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Primitive;
import code_review.symbol_table.symbols.Symbol;

public class ComparisonNode implements Node {

  ArrayList<AdditionNode> children;
  ArrayList<String> operators;
  Indirect address;
  Indirect globalTmp = TemporaryMemoryBank.allocateTemporaryMemory(4);
  Symbol symbol;
  DecafCodeGenerator codeGenerator;

  public ComparisonNode(DecafCodeGenerator codeGenerator) {
    this.codeGenerator = codeGenerator;
    children = new ArrayList<>();
    operators = new ArrayList<>();
    children.add(new AdditionNode(codeGenerator));
  }

  private AdditionNode lastChild() throws SyntaxException {
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
    if (!isComplete()) throw new SyntaxException("incomplete AddNode");
    AdditionNode child = children.get(0);
    child.implement(mipsLines);
    address = child.getAddress();
    symbol = child.getSymbol();
    if (children.size() == 1) return;
		if (!symbol.equals(IntSymbol.get()) && !symbol.equals(DoubleSymbol.get())) {
			throw new SemanticException("mult is only for numbers");
		}
    if (children.size() != 2) {
    	throw new SemanticException("can not cascade less operators");  
    }
		address = globalTmp;
		Register register = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", register, child.getAddress()));
		mipsLines.add(new Instruction("sw", register, address));
		RegisterBank.freeRegister(register);
    for (int i = 1; i < children.size(); i += 1) {
      child = children.get(i);
      child.implement(mipsLines);
      if (!child.getSymbol().equals(symbol)) {
        throw new SemanticException("type mismatch in less");
      }
			if (operators.get(i-1).equals("<")) {
			  ((Primitive)symbol).isLess(address, child.getAddress(), address);
      } else if (operators.get(i-1).equals(">=")) {
			  ((Primitive)symbol).isLess(address, child.getAddress(), address);
        Register r = RegisterBank.allocateRegister(BoolSymbol.get());
        DecafCodeGenerator.mipsLines.add(new Instruction("lw", r, address));
        mipsLines.add(new Instruction("xori", r, r, new Immediate(1)));
        DecafCodeGenerator.mipsLines.add(new Instruction("sw", r, address));
      } else if (operators.get(i-1).equals(">")) {
			  ((Primitive)symbol).isLess(child.getAddress(), address, address);
      } else if (operators.get(i-1).equals("<=")) {
			  ((Primitive)symbol).isLess(child.getAddress(), address, address);
        Register r = RegisterBank.allocateRegister(BoolSymbol.get());
        DecafCodeGenerator.mipsLines.add(new Instruction("lw", r, address));
        mipsLines.add(new Instruction("xori", r, r, new Immediate(1)));
        DecafCodeGenerator.mipsLines.add(new Instruction("sw", r, address));
      }
    }
    symbol = BoolSymbol.get();
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
      if (operator.equals("<") || operator.equals(">")
       || operator.equals("<=") || operator.equals(">=")) {
        if (lastChild().isComplete()) {
          children.add(new AdditionNode(codeGenerator));
          operators.add(operator);
        } else {
          throw new SyntaxException("Unexpected "+operator+" operator");
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
