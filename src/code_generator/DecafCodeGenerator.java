package code_generator;

import code_generator.instructions.*;
import code_generator.nodes.OrNode;
import code_generator.operand.*;
import code_generator.stack.Display;
import code_generator.stack.TemporaryMemoryBank;
import code_review.DecafCodeReviewer;
import code_review.symbol_table.Function;
import code_review.symbol_table.SymbolTable;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.BoolSymbol;
import code_review.symbol_table.symbols.Symbol;
import parser.Action;
import parser.CodeGenerator;
import scanner.CompilerScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DecafCodeGenerator implements CodeGenerator {
	public CompilerScanner scanner;
	public DecafCodeReviewer codeReviewer;

	public static ArrayList<MipsLine> mipsLines = new ArrayList<>();
	public Display display = new Display(mipsLines);
	public SymbolTable symbolTable = new SymbolTable();

	public Stack<Variable> variables = new Stack<>();
	public Stack<Indirect> addresses = new Stack<>();

	public Stack<Label> labels = new Stack<>();

	public OrNode currentOrNode = new OrNode(this);

	String latestRecordedIdent = null;
	Symbol latestRecordedSymbol = null;

	public DecafCodeGenerator(CompilerScanner scanner, DecafCodeReviewer codeReviewer) throws SemanticException {
		this.scanner = scanner;
		this.codeReviewer = codeReviewer;
		display.addScope(codeReviewer.getGlobalScope());
		mipsLines.add(new Directive("globl", Collections.singletonList("main")));
		mipsLines.addAll(codeReviewer.getMipsLines());
		mipsLines.add(new Directive("text"));
	}

	@Override
	public void doSemantic(String sem, Action action) throws Throwable {
		if (sem.equals(""))
			return;
		try {
			Method semanticMethod = this.getClass().getMethod(sem);
			semanticMethod.invoke(this);
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SemanticException("Semantic " + sem + " can't be called");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e.getCause();
		}
	}

	public String getResult() {
		StringBuilder result = new StringBuilder();
		for (MipsLine line : mipsLines) {
			result.append(line).append('\n');
		}

		return result.toString();
	}

	public void integerConstant() throws SyntaxException, SemanticException {
		String integer = scanner.getToken();
		currentOrNode.addIdent(LabelMaker.createConstantLabel(integer, "integer", 4).toString());
	}

	public void doubleConstant() throws SyntaxException, SemanticException {
		String integer = scanner.getToken();
		currentOrNode.addIdent(LabelMaker.createConstantLabel(integer, "double", 4).toString());
	}

	public void booleanConstant() throws SyntaxException, SemanticException {
		String x = scanner.getToken();
		currentOrNode.addIdent(LabelMaker.createConstantLabel(x, "bool", 4).toString());	
	}

	public void operator() throws SyntaxException, SemanticException {
		currentOrNode.addOperator(scanner.getToken());
	}

	public void ident() throws SyntaxException, SemanticException {
		currentOrNode.addIdent(scanner.getToken());
	}

	public void implementExpression() throws SyntaxException, SemanticException {
		currentOrNode.implement(mipsLines);
	}

	public void type() {
		latestRecordedSymbol = symbolTable.getSymbol(scanner.getToken());
	}

	public void endLine() {
		TemporaryMemoryBank.resetMemory();
		currentOrNode = new OrNode(this);
	}

	public void recordIdent() throws SemanticException, NoSuchFieldException {
		latestRecordedIdent = scanner.getToken();
		Variable variable = display.getVariable(latestRecordedIdent);
		if (symbolTable.getSymbol(latestRecordedIdent) != null) {
			latestRecordedSymbol = symbolTable.getSymbol(latestRecordedIdent);
		}

		if (variable != null) {
			variables.add(variable);
			addresses.add(display.getVariableAddress(latestRecordedIdent));
		}
	}

	public void declareInstantVariable() throws SyntaxException {
		if (!variables.empty())
			throw new SyntaxException("Declaration while variables is not empty.");

		display.allocateVariable(latestRecordedSymbol, scanner.getToken());
		currentOrNode = new OrNode(this);
	}

	public void startScope() {
		display.addNewScope();
	}

	public void endScope() {
		display.popScope();
	}

	public void ifStatement() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		Indirect address = addresses.pop();

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("bez", value, new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void elseStatement() {

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("j", new LabelOperand(jumpLabel)));

		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public void completeElse() {
		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public void whileLabel() {
		Label label = LabelMaker.createNonFunctionLabel();
		mipsLines.add(label);
		//
		labels.push(label);
	}

	public void whileLoop() throws SemanticException, ClassNotFoundException {
		Symbol symbol = variables.pop().getSymbol();
		Indirect address = addresses.pop();

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("bez", value, new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void whileComplete() {
		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public Function currentFunction;
	public void declareFunction() throws NoSuchFieldException {
		if (currentClassSymbol != null) {
			currentFunction = currentClassSymbol.getMethod(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(currentClassSymbol.getName(), latestRecordedIdent));
		} else {
			currentFunction = codeReviewer.getGlobalScope().getFunction(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(latestRecordedIdent));
		}
		latestRecordedIdent = null;

		display.addNewScope();
		display.allocateVariable(currentFunction.getReturnType(), "return");
		for (Variable variable: currentFunction.getArguments()) {
			display.allocateVariable(variable.getSymbol(), variable.getName());
		}
	}

	public void endFunctionBlock() {
		display.popScope();
		mipsLines.add(new Instruction("jr", new Register("ra")));
	}

	public Symbol currentClassSymbol;
}
