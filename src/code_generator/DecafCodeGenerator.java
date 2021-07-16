package code_generator;

import code_generator.instructions.Directive;
import code_generator.instructions.MipsLine;
import code_generator.symbol_table.SymbolType;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import parser.Action;
import parser.CodeGenerator;

import java.util.ArrayList;

public class DecafCodeGenerator implements CodeGenerator {
	ArrayList<MipsLine> mipsLines = new ArrayList<>();
	int call_number = 0;
	int current_call = 0;

	SymbolType declaration_type;

	public DecafCodeGenerator() {
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws SemanticException {
		if (action != Action.SHIFT)
			return;
		current_call ++;
		switch (sem) {
			case "is_lvalue_friendly":
				call_number = current_call;
				break;
			case "assignment_operator":
				if (current_call - call_number > 2) {
					throw new SyntaxException("wrong assignment");
				}
				break;
			default:
				break;
		}
	}

	public String get_result() {
		StringBuilder result = new StringBuilder();
		for (MipsLine line: mipsLines) {
			result.append(line).append('\n');
		}

		return result.toString();
	}
}
