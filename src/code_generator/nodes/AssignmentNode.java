package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.Instruction;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_generator.operand.Register;
import code_generator.operand.RegisterBank;
import code_review.symbol_table.symbols.Primitive;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class AssignmentNode implements Node {
	ArrayList<OrNode> children = new ArrayList<>();
	DecafCodeGenerator codeGenerator;
	Symbol symbol;
	Indirect address;
	ArrayList<String> operators = new ArrayList<>();

	public AssignmentNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		children.add(new OrNode(codeGenerator));
	}

  private OrNode lastChild() throws SyntaxException {
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
		if (children.size() > 2)
			throw new SyntaxException("More than one assignment operators used in an expression.");
		
		OrNode rhs = children.get(children.size() - 1);
		rhs.implement(mipsLines);
		if (children.size() == 1) {
			symbol = rhs.symbol;
			address = rhs.address;
			return;
		}
		String operator = operators.get(0);
		OrNode lhs = children.get(0);
		lhs.implement(mipsLines);
		if (!lhs.isLValue()) throw new SemanticException("Can not assign");
		if (lhs.getSymbol() != rhs.getSymbol())
			throw new SemanticException("mismatch type in assign");
		if (operator.equals("=")) {
			Register r = RegisterBank.allocateRegister(symbol);
			mipsLines.add(new Instruction("lw", r, rhs.getAddress()));
			mipsLines.add(new Instruction("sw", r, lhs.getAddress()));
			RegisterBank.freeRegister(r);	
		} else if (operator.equals("+=")) {
			((Primitive)symbol)
			.addition(rhs.getAddress(),
			lhs.getAddress(), lhs.getAddress());	
		} else if (operator.equals("-=")) {
			((Primitive)symbol).subtraction(rhs.getAddress(), lhs.getAddress(), lhs.getAddress());	
		}
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		lastChild().addIdent(token);	
	}

	@Override
  public void addOperator(String operator) throws SyntaxException, SemanticException {
    try {
			System.out.println(operator);
      lastChild().addOperator(operator);
    } catch(SyntaxException | SemanticException e) {
      if (operator.equals("=") || operator.equals("+=") || operator.equals("*=") 
			|| operator.equals("-=") || operator.equals("/=")) {
        if (lastChild().isComplete()) {
					children.add(new OrNode(codeGenerator));
					operators.add(operator);
        } else {
          throw new SyntaxException("Unexpected = operator");
        }
      } else {
        throw e;
      }
    }
  }

	@Override
	public boolean isLValue() throws SyntaxException, SemanticException {
		return children.size() == 1 && lastChild().isLValue();
	}

	@Override
	public boolean isComplete() throws SyntaxException, SemanticException {
		return lastChild().isComplete();
	}

	@Override
	public Symbol getType() throws SyntaxException, SemanticException {
		return lastChild().getType();
	}
}
