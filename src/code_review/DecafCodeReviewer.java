package code_review;

import code_generator.SemanticException;
import code_generator.instructions.Directive;
import code_generator.instructions.Label;
import code_generator.instructions.LabelMaker;
import code_generator.instructions.MipsLine;
import code_generator.stack.Scope;
import code_review.symbol_table.AccessMode;
import code_review.symbol_table.Function;
import code_review.symbol_table.SymbolTable;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.IntSymbol;
import code_review.symbol_table.symbols.Symbol;
import code_review.symbol_table.symbols.VoidSymbol;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class DecafCodeReviewer implements CodeGenerator {
	CompilerScanner scanner;
	ArrayList<MipsLine> mipsLines = new ArrayList<>();
	Scope globalScope = new Scope();
	SymbolTable symbolTable = new SymbolTable();

	Symbol currentClassSymbol;
	Symbol latestRecordedSymbol;
	Function currentFunction;
	AccessMode latestAccessMode;
	String latestRecordedIdent;

	public DecafCodeReviewer(CompilerScanner scanner) throws SemanticException {
		this.scanner = scanner;
		mipsLines.add(new Directive("data"));

//		mipsLines.add(new Label("bool__false"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
//		mipsLines.add(new Label("bool__true"));
//		mipsLines.add(new Directive("word", Collections.singletonList("1")));
//
//		mipsLines.add(new Label("and__tmp"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
//		mipsLines.add(new Label("or__tmp"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
//		mipsLines.add(new Label("add__tmp"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
//		mipsLines.add(new Label("mult__tmp"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
//		mipsLines.add(new Label("unary__tmp"));
//		mipsLines.add(new Directive("word", Collections.singletonList("0")));
		
		mipsLines.add(new Label("string__newline"));
		mipsLines.add(new Directive("asciiz", Collections.singletonList("\"\\n\"")));

		mipsLines.add(new Label("string__true"));
		mipsLines.add(new Directive("asciiz", Collections.singletonList("\"true\"")));

		mipsLines.add(new Label("string__false"));
		mipsLines.add(new Directive("asciiz", Collections.singletonList("\"false\"")));

		globalScope.addFunction(new Function("Print").setReturnType(VoidSymbol.get()));
		globalScope.addFunction(new Function("ReadInteger").setReturnType(IntSymbol.get()));
		// FIXME Complete return types and arguments.
		globalScope.addFunction(new Function("ReadLine"));
		globalScope.addFunction(new Function("NewArray"));
		globalScope.addFunction(new Function("getArrVal"));
		globalScope.addFunction(new Function("btoi"));
		globalScope.addFunction(new Function("itob"));
		globalScope.addFunction(new Function("dtoi"));
		globalScope.addFunction(new Function("itod"));
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

		if (currentFunction != null) {
			currentFunction.addArgument(variable);
			return;
		}

		if (currentClassSymbol != null) {
			variable.setAccessMode(latestAccessMode);
			latestAccessMode = AccessMode.NONE;
			currentClassSymbol.addField(variable);
		} else {
			globalScope.addVariable(variable);
		}
	}

	public void declareFunction() throws NoSuchFieldException {
		if (currentClassSymbol != null) {
			currentFunction = new Function(currentClassSymbol.getName(), latestRecordedIdent);
			currentFunction.setReturnType(latestRecordedSymbol);
			latestRecordedSymbol = null;
			currentFunction.setAccessMode(latestAccessMode);
			latestAccessMode = AccessMode.NONE;
			currentClassSymbol.addField(currentFunction);
		} else {
			currentFunction = new Function(latestRecordedIdent);
			currentFunction.setReturnType(latestRecordedSymbol);
			latestRecordedSymbol = null;
			globalScope.addFunction(currentFunction);
		}
	}

	public void endArguments() {
		currentFunction = null;
	}

	// Constant Interpretation Methods
	public void integerConstant() {
		String integer = scanner.getToken();
		mipsLines.add(LabelMaker.createConstantLabel(integer, "integer", 4));
		mipsLines.add(new Directive("word", Collections.singletonList(integer.toLowerCase(Locale.ROOT))));
	}

	public void doubleConstant() {
		String double_ = scanner.getToken();
		mipsLines.add(LabelMaker.createConstantLabel(double_, "double", 4));
		mipsLines.add(new Directive("float", Collections.singletonList(double_)));
	}

	public void booleanConstant() {
		String bool = scanner.getToken();
		mipsLines.add(LabelMaker.createConstantLabel(bool, "bool", 4));
		mipsLines.add(new Directive("word", Collections.singletonList(bool.equals("true")?"1":"0")));
	}

	public void stringConstant() {
		String string = scanner.getToken();
		mipsLines.add(LabelMaker.createConstantLabel(string, "string", string.length()));
		mipsLines.add(new Directive("asciiz", Collections.singletonList(string)));
	}
}
