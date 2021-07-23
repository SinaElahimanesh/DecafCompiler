package code_generator.nodes;

import code_generator.DecafCodeGenerator;
import code_generator.SemanticException;
import code_generator.SyntaxException;
import code_generator.instructions.MipsLine;
import code_generator.operand.Indirect;
import code_review.symbol_table.symbols.Symbol;

import java.util.ArrayList;

public class AssignmentNode implements Node {
	ArrayList<OrNode> children = new ArrayList<>();
	DecafCodeGenerator codeGenerator;

	AssignmentNode(DecafCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
		children.add(new OrNode(codeGenerator));
	}

  private OrNode lastChild() throws SyntaxException {
    return children.get(children.size() - 1);
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
		if (children.size() > 2)
			throw new SyntaxException("More than one assignment operators used in an expression.");
		children.get(0).implement(mipsLines);
	}

	@Override
	public void addIdent(String token) throws SyntaxException, SemanticException {
		
	}
	@Override
  public void addOperator(String operator) throws SyntaxException, SemanticException {
    try {
      lastChild().addOperator(operator);
    } catch(SyntaxException | SemanticException e) {
      if (operator.equals("=")) {
        if (lastChild().isComplete()) {
          children.add(new OrNode(codeGenerator));
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
}
