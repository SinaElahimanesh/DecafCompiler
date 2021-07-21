package code_review;

import code_generator.SemanticException;
import code_generator.instructions.MipsLine;
import code_generator.stack.Scope;
import code_review.symbol_table.AccessMode;
import code_review.symbol_table.Function;
import code_review.symbol_table.SymbolTable;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.Symbol;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

public class DecafCodeReviewer implements CodeGenerator {
	CompilerScanner scanner;
	ArrayList<MipsLine> mipsLines = new ArrayList<>();
	Scope globalScope = new Scope();
	SymbolTable symbolTable = new SymbolTable();

	Symbol currentClassSymbol;
	Symbol latestRecordedSymbol;
	AccessMode latestAccessMode;
	String latestRecordedIdent;

	public DecafCodeReviewer(CompilerScanner scanner) throws SemanticException {
		this.scanner = scanner;
		latestAccessMode = AccessMode.NONE;
	}

	public Scope getGlobalScope() {
		return globalScope;
	}

	public ArrayList<MipsLine> getMipsLines() {
		return mipsLines;
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {
		try {
			Method semanticMethod = this.getClass().getMethod(sem);
			semanticMethod.invoke(this);
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SemanticException("Semantic " + sem + " can't be called");
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public void declareClass() throws SemanticException {
		currentClassSymbol = new Symbol(scanner.getToken());
		symbolTable.addSymbol(currentClassSymbol);
	}

	public void endClassDeclaration() {
		currentClassSymbol = null;
	}

	public void accessMode() {
		latestAccessMode = AccessMode.valueOf(scanner.getToken().toUpperCase(Locale.ROOT));
	}

	public void type() {
		latestRecordedSymbol = symbolTable.getSymbol(scanner.getToken());
	}

	public void recordIdent() {
		latestRecordedIdent = scanner.getToken();
	}

	public void declareVariable() throws NoSuchFieldException {
		Variable variable = new Variable(latestRecordedSymbol, latestRecordedIdent);
		latestRecordedSymbol = null;
		latestRecordedIdent = null;

		if (currentClassSymbol != null) {
			variable.setAccessMode(latestAccessMode);
			latestAccessMode = AccessMode.NONE;
			currentClassSymbol.addField(variable);
		} else {
			globalScope.addVariable(variable);
		}
	}

	public void declareFunction() throws NoSuchFieldException {
		Function function;
		if (currentClassSymbol != null) {
			function = new Function(currentClassSymbol.getName(), latestRecordedIdent);
			function.setAccessMode(latestAccessMode);
			latestAccessMode = AccessMode.NONE;
			currentClassSymbol.addField(function);
		} else {
			function = new Function(latestRecordedIdent);
			globalScope.addFunction(function);
		}
	}
}
