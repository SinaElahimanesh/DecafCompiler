package code_review;

import code_generator.instructions.Directive;
import code_generator.instructions.MipsLine;
import code_generator.stack.Display;
import code_review.symbol_table.symbols.Symbol;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.util.ArrayList;

public class DecafCodeReviewer implements CodeGenerator {
	CompilerScanner scanner;
	ArrayList<MipsLine> mipsLines = new ArrayList<>();
	Display display = new Display(mipsLines);

	Symbol classSymbol;

	public DecafCodeReviewer(CompilerScanner scanner) {
		this.scanner = scanner;
		display.addNewScope();
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {

	}
}
