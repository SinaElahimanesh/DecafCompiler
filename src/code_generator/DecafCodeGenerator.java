package code_generator;

import code_generator.instructions.*;
import code_generator.nodes.AssignmentNode;
import code_generator.operand.*;
import code_generator.stack.Display;
import code_generator.stack.Scope;
import code_generator.stack.TemporaryMemoryBank;
import code_review.DecafCodeReviewer;
import code_review.symbol_table.Function;
import code_review.symbol_table.SymbolTable;
import code_review.symbol_table.Variable;
import code_review.symbol_table.symbols.ArraySymbol;
import code_review.symbol_table.symbols.BoolSymbol;
import code_review.symbol_table.symbols.Symbol;
import code_review.symbol_table.symbols.VoidSymbol;
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

	public AssignmentNode currentNode = new AssignmentNode(this);

	String latestRecordedIdent = null;
	Symbol latestRecordedSymbol = null;

	boolean haveDeclaredVariable = false;
	boolean canDeclareVariable = true;

	Exception savedException;

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
		currentNode.addIdent(LabelMaker.createConstantLabel(integer, "integer", 4).toString());
	}

	public void doubleConstant() throws SyntaxException, SemanticException {
		String integer = scanner.getToken();
		currentNode.addIdent(LabelMaker.createConstantLabel(integer, "double", 4).toString());
	}

	public void booleanConstant() throws SyntaxException, SemanticException {
		String x = scanner.getToken();
		currentNode.addIdent(LabelMaker.createConstantLabel(x, "bool", 4).toString());
	}

	public void stringConstant() throws SyntaxException, SemanticException {
		String x = scanner.getToken();
		currentNode.addIdent(LabelMaker.createConstantLabel(x, "string", 4).toString());
	}

	ArrayList<String> receivedWords = new ArrayList<>();
	ArrayList<String> receivedTokens = new ArrayList<>();

	public void operator() throws SyntaxException, SemanticException {
		receivedWords.add(scanner.getToken());
		receivedTokens.add("operator");

		try {
			currentNode.addOperator(scanner.getToken());
		} catch (SyntaxException | SemanticException e) {
			if (!canDeclareVariable)
				throw e;
			if (!(savedException instanceof SyntaxException))
				savedException = e;
		}
	}

	public void ident() throws SyntaxException, SemanticException {
		receivedWords.add(scanner.getToken());
		receivedTokens.add("ident");

		try {
			currentNode.addIdent(scanner.getToken());
		} catch (SyntaxException | SemanticException e) {
			if (!canDeclareVariable)
				throw e;
			if (!(savedException instanceof SyntaxException))
				savedException = e;
		}
	}

	public void implementExpression() throws SyntaxException, SemanticException {
		try {
			currentNode.implement(mipsLines);
		} catch (SyntaxException | SemanticException e) {
			if (!canDeclareVariable)
				throw e;
			if (!(savedException instanceof SyntaxException))
				savedException = e;
		}
	}

	public void type() {
		receivedWords.add(scanner.getToken());
		receivedTokens.add("type");
		latestRecordedSymbol = symbolTable.getSymbol(scanner.getToken());
	}

	public void returnValue() throws SyntaxException, SemanticException, NoSuchFieldException {
		if (!currentNode.getSymbol().equals(currentFunction.getReturnType()))
			throw new SemanticException("Incompatible returned function with type");
		Indirect returnAddress = display.getVariableAddress("return");
		Indirect returnResultAddress = currentNode.getAddress();
		Register register = RegisterBank.allocateRegister(currentNode.getSymbol());
		mipsLines.add(new Instruction("lw", register, returnResultAddress));
		mipsLines.add(new Instruction("sw", register, returnAddress));

		RegisterBank.freeRegister(register);
	}

	public void assertVoidFunction() throws SemanticException {
		if (!currentFunction.getReturnType().equals(VoidSymbol.get())) {
			throw new SemanticException("You can't just return from non-void function.");
		}
	}

	public void returnFunction() {
		int scopesDepth = display.findScopeDepth(currentFunctionScope);
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(-scopesDepth)));
		mipsLines.add(new Instruction("jr", new Register("ra")));
	}

	public void endLine() throws Exception {
		if (canDeclareVariable && declareVariableWithLine())
		{
			savedException = null;
			haveDeclaredVariable = true;
		}

		if (savedException != null)
			throw savedException;
		if (!haveDeclaredVariable)
			canDeclareVariable = false;
		haveDeclaredVariable = false;
		TemporaryMemoryBank.resetMemory();
		receivedWords.clear();
		receivedTokens.clear();
		currentNode = new AssignmentNode(this);
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

	private boolean declareVariableWithLine() {
		if (receivedWords.size() < 2 || receivedWords.size() % 2 == 1) return false;
		Symbol type = symbolTable.getSymbol(receivedWords.get(0));
		if (type == null) return false;
		for (int i = 1; i < receivedWords.size() - 1; i ++) {
			if (i % 2 == 0 && !receivedWords.get(i).equals("]")) return false;
			if (i % 2 == 1 && !receivedWords.get(i).equals("[")) return false;

			if (i % 2 == 0)
				type = new ArraySymbol(type);
		}

		if (!receivedTokens.get(receivedTokens.size() - 1).equals("ident"))
			return false;

		display.allocateVariable(type, receivedWords.get(receivedWords.size()-1));
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(4)));
		return true;
	}

	public void declareInstantVariable() throws SyntaxException {
		haveDeclaredVariable = true;
		if (!canDeclareVariable)
			throw new SyntaxException("Can't declare variables in this scope.");

		display.allocateVariable(latestRecordedSymbol, scanner.getToken());
		currentNode = new AssignmentNode(this);
	}

	public void startScope() throws Exception {
		if (savedException != null)
			throw savedException;
		receivedWords.clear();
		receivedTokens.clear();
		canDeclareVariable = true;
		display.addNewScope();
	}

	public void endScope() {
		display.popScope();
		receivedWords.clear();
		receivedTokens.clear();
		canDeclareVariable = false;
	}

	Stack<Label> endLabels = new Stack<>();

	public void breakLoop() {
		mipsLines.add(new Instruction("j", new LabelOperand(endLabels.peek())));
	}

	public void ifStatement() throws SemanticException, ClassNotFoundException, SyntaxException {
		Symbol symbol = currentNode.getSymbol();
		Indirect address = currentNode.getAddress();

		currentNode = new AssignmentNode(this);

		if (!(symbol instanceof BoolSymbol)) {
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");
		}
		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("beq", value, new Register("zero"), new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void completeIf() {
		mipsLines.add(labels.pop());
	}

	public void elseStatement() {
		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		mipsLines.add(new Instruction("j", new LabelOperand(jumpLabel)));
		labels.push(jumpLabel);
	}

	public void completeElse() {
		//
		Label label = labels.pop();
		mipsLines.add(label);
	}

	public void whileLabel() {
		Label label = LabelMaker.createNonFunctionLabel();
		mipsLines.add(label);
		labels.push(label);
	}

	public void startFor() throws SemanticException, SyntaxException {
		Label endLabel = LabelMaker.createNonFunctionLabel();
		Label startStep = LabelMaker.createNonFunctionLabel();
		Label startBody = LabelMaker.createNonFunctionLabel();
		Label startCondition = LabelMaker.createNonFunctionLabel();

		labels.push(startCondition);
		labels.push(startBody);
		labels.push(startStep);
		labels.push(endLabel);

		endLabels.push(endLabel);

		mipsLines.add(startCondition);
	}


	public void endForCondition() throws SemanticException, SyntaxException {
		Symbol symbol = currentNode.getSymbol();
		Indirect address = currentNode.getAddress();

		if (!(symbol instanceof BoolSymbol))
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");

		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label endLabel = labels.get(labels.size() - 1);
		Label startStep = labels.get(labels.size() - 2);
		Label startBody = labels.get(labels.size() - 3);
		Label startCondition = labels.get(labels.size() - 4);

		mipsLines.add(new Instruction("beq", value, new Register("zero"), new LabelOperand(endLabel)));
		mipsLines.add(new Instruction("j", new LabelOperand(startBody)));
		RegisterBank.freeRegister(value);
		mipsLines.add(startStep); // start step
	}

	public void endForStep() throws SemanticException, SyntaxException {
		Label endLabel = labels.get(labels.size() - 1);
		Label startStep = labels.get(labels.size() - 2);
		Label startBody = labels.get(labels.size() - 3);
		Label startCondition = labels.get(labels.size() - 4);

		mipsLines.add(new Instruction("j", new LabelOperand(startCondition)));
		mipsLines.add(startBody);
	}

	public void forComplete() throws SemanticException, SyntaxException {
		Label endLabel = labels.get(labels.size() - 1);
		Label startStep = labels.get(labels.size() - 2);
		Label startBody = labels.get(labels.size() - 3);
		Label startCondition = labels.get(labels.size() - 4);

		mipsLines.add(new Instruction("j", new LabelOperand(startStep)));
		mipsLines.add(endLabel);

		for (int i = 0; i < 4; i ++) labels.pop();

		endLabels.pop();
	}

	public void whileLoop() throws SemanticException, SyntaxException {
		Symbol symbol = currentNode.getSymbol();
		Indirect address = currentNode.getAddress();

		if (!(symbol instanceof BoolSymbol))
			throw new SemanticException("Boolean expression: the condition of If statement is not Boolean Expression");

		Register value = RegisterBank.allocateRegister(symbol);
		mipsLines.add(new Instruction("lw", value, address));

		Label jumpLabel = LabelMaker.createNonFunctionLabel();
		labels.push(jumpLabel);
		mipsLines.add(new Instruction("beq", value, new Register("zero"), new LabelOperand(jumpLabel)));

		RegisterBank.freeRegister(value);
	}

	public void whileComplete() {
		Label endLabel = labels.pop();
		mipsLines.add(new Instruction("j", new LabelOperand(labels.pop())));
		mipsLines.add(endLabel);
	}

	public Function currentFunction;
	public Scope currentFunctionScope;
	public void declareFunction() throws NoSuchFieldException {
		if (currentClassSymbol != null) {
			currentFunction = currentClassSymbol.getMethod(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(currentClassSymbol.getName(), latestRecordedIdent));
		} else {
			currentFunction = codeReviewer.getGlobalScope().getFunction(latestRecordedIdent);
			mipsLines.add(LabelMaker.createFunctionLabel(latestRecordedIdent));
		}
		latestRecordedIdent = null;

		currentFunctionScope = display.addNewScope();
		display.allocateVariable(currentFunction.getReturnType(), "return");
		for (Variable variable: currentFunction.getArguments()) {
			display.allocateVariable(variable.getSymbol(), variable.getName());
		}
		mipsLines.add(new Instruction("addi", new Register("sp"), new Register("sp"), new Immediate(display.getLastScope().getSize())));
	}

	public void endFunctionBlock() {
		display.popScope();
		mipsLines.add(new Instruction("jr", new Register("ra")));
	}

	public Symbol currentClassSymbol;
}
