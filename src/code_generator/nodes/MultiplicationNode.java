package code_generator.nodes;

import java.util.ArrayList;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.Label;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_generator.stack.TemporaryMemoryBank;
import code_review.symbol_table.symbols.DoubleSymbol;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Symbol;

public class MultiplicationNode implements Node {

  ArrayList<UnaryNode> children;
  ArrayList<String> operators;
  Indirect address;
  Indirect globalTmp = TemporaryMemoryBank.allocateTemporaryMemory(4);
  Symbol symbol;
  DecafCodeGenerator codeGenerator;

  public MultiplicationNode(DecafCodeGenerator codeGenerator) {
    this.codeGenerator = codeGenerator;
    children = new ArrayList<>();
    operators = new ArrayList<>();
    children.add(new UnaryNode(codeGenerator));
  }

  private UnaryNode lastChild() throws SyntaxException {
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
    UnaryNode child = children.get(0);
    child.implement(mipsLines);
    address = child.getAddress();
    symbol = child.getSymbol();
    if (children.size() == 1) return;
		if (!symbol.equals(IntSymbol.get()) && !symbol.equals(DoubleSymbol.get())) {
			throw new SemanticException("mult is only for numbers");
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
        throw new SemanticException("type mismatch in add or sub");
      }
        Register register1 = RegisterBank.allocateRegister(symbol);
        Register register2 = RegisterBank.allocateRegister(symbol);
        mipsLines.add(new Instruction("lw", register1, child.getAddress()));
        mipsLines.add(new Instruction("lw", register2, address));
        if (operators.get(i-1).equals("*")) {
          mipsLines.add(new Instruction("mul", register1, register1, register2));
        } else if (operators.get(i-1).equals("/")) {
          mipsLines.add(new Instruction("div", register2, register1));
					mipsLines.add(new Instruction("mflo", register1));
        } else {
          mipsLines.add(new Instruction("div", register2, register1));  
        	mipsLines.add(new Instruction("mfhi", register1));
				}
				mipsLines.add(new Instruction("sw", register1, address));
        RegisterBank.freeRegister(register1);
        RegisterBank.freeRegister(register2);
      
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
      if (operator.equals("*") || operator.equals("/") || operator.equals("%")) {
        if (lastChild().isComplete()) {
          children.add(new UnaryNode(codeGenerator));
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
