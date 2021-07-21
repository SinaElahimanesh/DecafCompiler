package code_review;

import code_generator.instructions.Directive;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

public class DecafCodeReviewer implements CodeGenerator {
	CompilerScanner scanner;

	public DecafCodeReviewer(CompilerScanner scanner) {
		this.scanner = scanner;
	}


	@Override
	public void doSemantic(String sem, Action action) throws Throwable {

	}
}
