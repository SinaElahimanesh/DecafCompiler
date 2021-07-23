package code_review.symbol_table.symbols;

import code_generator.operand.Indirect;
import code_generator.operand.Register;


public interface Primitive {
	
	void addition(Indirect a, Indirect b, Indirect r);
	void subtraction(Indirect a, Indirect b, Indirect r);
	void multiplication(Indirect a, Indirect b, Indirect r);
	void division(Indirect a, Indirect b, Indirect r);
	void print(Register r)
			throws ClassNotFoundException;
}
