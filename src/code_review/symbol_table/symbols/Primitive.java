package code_review.symbol_table.symbols;

import code_generator.SemanticException;
import code_generator.operand.Indirect;
import code_generator.operand.Register;


public interface Primitive {
	void addition(Indirect a, Indirect b, Indirect r) throws SemanticException;
	void subtraction(Indirect a, Indirect b, Indirect r) throws SemanticException;
	void multiplication(Indirect a, Indirect b, Indirect r) throws SemanticException;
	void division(Indirect a, Indirect b, Indirect r) throws SemanticException;
	void isEqual(Indirect a, Indirect b, Indirect r) throws SemanticException;
	void print(Register r) throws ClassNotFoundException, SemanticException;
}
