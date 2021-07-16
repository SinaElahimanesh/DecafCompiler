package code_generator;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import parser.Action;
import parser.CodeGenerator;

public class DecafCodeGenerator implements CodeGenerator {
	int call_number = 0;
	int current_call = 0;

	@Override
	public void doSemantic(String sem, Action action) {
		if (action != Action.SHIFT)
			return;
		current_call ++;
		if (sem.equals("is_lvalue_friendly"))
			call_number = current_call;
		else if (sem.equals("assignment_operator")) {
			if (current_call - call_number > 2) {
				throw new SyntaxException("wrong assignment");
			}
		}
	}
}
