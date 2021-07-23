package code_review.symbol_table.symbols;

import code_generator.SemanticException;
import code_generator.operand.Indirect;
import code_generator.operand.Register;

public class ArraySymbol implements Primitive {
	@Override
	public void addition(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void subtraction(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Subtraction of arrays is not allowed.");
	}

	@Override
	public void multiplication(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Multiplication of arrays is not allowed.");

	}

	@Override
	public void division(Indirect a, Indirect b, Indirect r) throws SemanticException {
		throw new SemanticException("Division of arrays is not allowed.");
	}

	@Override
	public void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void isLess(Indirect a, Indirect b, Indirect r) throws SemanticException {

	}

	@Override
	public void print(Register r) throws ClassNotFoundException, SemanticException {

	}
}
